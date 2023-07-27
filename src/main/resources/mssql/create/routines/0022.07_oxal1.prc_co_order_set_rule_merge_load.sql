create or alter procedure oxal1.prc_co_order_set_rule_merge_load
    @load_id uniqueidentifier,
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @error_mess nvarchar(4000);
        declare @periods table (an smallint, luna tinyint);
        declare @err_orders table (an smallint, luna smallint, coarea char(4), comanda char(9));

        /* set session user for history backup */
        exec sys.sp_set_session_context @key = N'oxal1_user_id', @value = @kid, @readonly = 0;

        begin try
            /* collect periods */
            insert into @periods (an, luna)
            select distinct a.an, a.luna
            from oxal1.tbl_int_co_order_settle_rule_load as a
            where a.load_id = @load_id;

            begin transaction
            /* merge uploaded records */
            merge into oxal1.tbl_int_co_order_settle_rule as t
            using (select * from oxal1.tbl_int_co_order_settle_rule_load as a
                    where a.load_id = @load_id) as s
            on (t.coarea = s.coarea and t.cocode = s.cocode and t.an = s.an and t.luna = s.luna and t.comanda = s.comanda and t.cost_center = s.cost_center)
            when matched then
                update set
                    t.procent = s.procent,
                    t.mod_de = s.mod_de,
                    t.mod_timp = current_timestamp
            when not matched by target then
                insert (coarea, cocode, an, luna, comanda, cost_center, procent, mod_de, mod_timp)
                values (s.coarea, s.cocode, s.an, s.luna, s.comanda, s.cost_center, s.procent, s.mod_de, current_timestamp);

            /* check percentages */
            insert into @err_orders (an, luna, coarea, comanda)
            select top 10 x.an, x.luna, x.coarea, x.comanda
            from (select
                    a.an,
                    a.luna,
                    a.coarea,
                    a.comanda,
                    sum(a.procent) as valoare
                from oxal1.tbl_int_co_order_settle_rule as a
                inner join @periods as b
                on a.an = b.an and a.luna = b.luna
                group by a.an, a.luna, a.coarea, a.comanda) as x
            where x.valoare != 1.00
            order by x.an asc, x.luna asc, x.coarea asc, x.comanda asc;

            if exists (select * from @err_orders)
                begin
                    declare @message varchar(max);

                    select @message = string_agg(a.an + ' - ' + a.luna + ' - ' + a.coarea + ' - ' + a.comanda, ', ')                    
                    from @err_orders as a;

                    set @message = 'PERCENT NOT 100% FOR: ' + @message;

                    raiserror(@message, 16, 1);
                end;
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            if @@error != 0
                set @error_mess = error_message();
        end catch

        /* clear load table */
        delete from oxal1.tbl_int_co_order_settle_rule_load where load_id = @load_id;
        if @error_mess is not null
            raiserror(@error_mess, 16, 1);
    end;
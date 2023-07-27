create or alter procedure oxal1.prc_co_order_merge_load
    @load_id uniqueidentifier
as
    begin
        set nocount on;
        declare @error_mess nvarchar(4000);

        /* merge uploaded records */
        begin try
            begin transaction
                merge into oxal1.tbl_int_co_order as t
                using (select * from oxal1.tbl_int_co_order_load as a
                        where a.load_id = @load_id) as s
                on (t.coarea = s.coarea and t.cod = s.cod)
                when matched then
                    update set
                        t.nume = isnull(s.nume, t.nume),
                        t.profit_center = isnull(s.profit_center, t.profit_center),
                        t.cost_center_resp = isnull(s.cost_center_resp, t.cost_center_resp),
                        t.mod_de = s.mod_de,
                        t.mod_timp = current_timestamp
                when not matched by target then
                    insert (cod, coarea, cocode, nume, profit_center, cost_center_resp, mod_de, mod_timp)
                    values (s.cod, s.coarea, s.cocode, s.nume, s.profit_center, s.cost_center_resp, s.mod_de, current_timestamp);
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            if @@error != 0
                set @error_mess = error_message();
        end catch

        /* clear load table */
        delete from oxal1.tbl_int_co_order_load where load_id = @load_id;
        if @error_mess is not null
            raiserror(@error_mess, 16, 1);
    end;
create or alter procedure oxal1.prc_cost_center_merge_load
    @load_id uniqueidentifier
as
    begin
        set nocount on;
        declare @error_mess nvarchar(4000);

        /* merge uploaded records */
        begin try
            begin transaction
                merge into oxal1.tbl_int_cost_center as t
                using (select * from oxal1.tbl_int_cost_center_load as a
                        where a.load_id = @load_id) as s
                on (t.cod = s.cod and t.coarea = s.coarea)
                when matched then
                    update set
                        t.nume = isnull(s.nume, t.nume),
                        t.profit_center = isnull(s.profit_center, t.profit_center),
                        t.mod_de = s.mod_de,
                        t.mod_timp = current_timestamp
                when not matched by target then
                    insert (cod, coarea, cocode, nume, profit_center, mod_de, mod_timp)
                    values (s.cod, s.coarea, s.cocode, s.nume, s.profit_center, s.mod_de, current_timestamp);
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            if @@error != 0
                set @error_mess = error_message();
        end catch

        /* clear load table */
        delete from oxal1.tbl_int_cost_center_load where load_id = @load_id;
        if @error_mess is not null
            raiserror(@error_mess, 16, 1);
    end;
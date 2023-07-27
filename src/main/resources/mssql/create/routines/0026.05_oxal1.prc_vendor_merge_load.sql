create or alter procedure oxal1.prc_vendor_merge_load
    @load_id uniqueidentifier
as
    begin
        set nocount on;
        declare @error_mess nvarchar(4000);

        /* merge uploaded records */
        begin try
            begin transaction
                merge into oxal1.tbl_int_vendor as t
                using (select * from oxal1.tbl_int_vendor_load as a
                        where a.load_id = @load_id) as s
                on (t.cod = s.cod)
                when matched then
                    update set
                        t.nume = isnull(s.nume, t.nume),
                        t.mod_de = s.mod_de,
                        t.mod_timp = current_timestamp
                when not matched by target then
                    insert (cod, nume, mod_de, mod_timp)
                    values (s.cod, s.nume, s.mod_de, current_timestamp);
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
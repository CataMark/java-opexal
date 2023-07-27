create or alter procedure oxal1.prc_mrecs_col_merge_load
    @load_id uniqueidentifier
as
    begin
        set nocount on;
        declare @error_mess nvarchar(4000);

        /* merge uploaded records */
        begin try
            begin transaction
                merge into oxal1.tbl_int_mrecs_columns as t
                using (select * from oxal1.tbl_int_mrecs_columns_load as a
                        where a.load_id = @load_id) as s
                on (t.sap_tranz = s.sap_tranz and t.lang = s.lang and t.cod = s.cod)
                when matched then
                    update
                    set t.nume = s.nume, t.mod_de = s.mod_de, t.mod_timp = current_timestamp
                when not matched by target then
                    insert (sap_tranz, lang, cod, nume, mod_de, mod_timp)
                    values (s.sap_tranz, s.lang, s.cod, s.nume, s.mod_de, current_timestamp);
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            if @@error != 0
                set @error_mess = error_message();
        end catch

        /* clear load table */
        delete from oxal1.tbl_int_mrecs_columns_load where load_id = @load_id;
        if @error_mess is not null
            raiserror(@error_mess, 16, 1);
    end;
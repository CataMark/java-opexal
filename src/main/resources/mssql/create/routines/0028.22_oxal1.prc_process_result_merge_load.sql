create or alter procedure oxal1.prc_process_result_merge_load
    @load_id uniqueidentifier
as
begin
    set nocount on;
    declare @error_mess nvarchar(4000);

    begin try
            begin transaction
                merge into oxal1.tbl_int_process_result as t
                using (select * from oxal1.tbl_int_process_result_load as a
                        where a.load_id = @load_id) as s
                on (t.id = s.id)
            when matched then
                update set
                    t.ocateg = s.ocateg,
                    t.acuratete = null,
                    t.mod_de = s.mod_de,
                    t.mod_timp = current_timestamp;
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            if @@error != 0
                set @error_mess = error_message();
        end catch

        /* clear load table */
        delete from oxal1.tbl_int_process_result_load where load_id = @load_id;
        if @error_mess is not null
            raiserror(@error_mess, 16, 1);
    end;
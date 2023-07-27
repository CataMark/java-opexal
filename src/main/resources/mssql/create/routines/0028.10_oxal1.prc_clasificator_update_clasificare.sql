create or alter procedure oxal1.prc_clasificator_update_clasificare
    @id uniqueidentifier,
    @clasa int,
    @acuratete decimal(5,4),
    @kid varchar(20)
as
    begin
        exec sys.sp_set_session_context @key = N'oxal1_not_history', @value = 1, @readonly = 0;
        update oxal1.tbl_int_process_result
        set ocateg = @clasa, acuratete = @acuratete, mod_de = @kid, mod_timp = current_timestamp
        where id = @id;
    end;
begin
    declare @kid varchar(20) = ?;
    exec sys.sp_set_session_context @key = N'oxal1_user_id', @value = @kid, @readonly = 0;

    update q1
    set %s, q1.mod_de = @kid, q1.mod_timp = current_timestamp, q1.load_file = null, q1.acuratete = null
    from oxal1.tbl_int_fagll03 as q1
    where q1.id = ?;
end;
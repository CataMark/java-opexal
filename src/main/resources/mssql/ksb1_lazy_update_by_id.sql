begin
    declare @kid varchar(20) = ? ;
    exec sys.sp_set_session_context @key = N'oxal1_user_id', @value = @kid, @readonly = 0;

    update q
    set %s , q.mod_de = @kid, q.mod_timp = current_timestamp, q.load_file = null
    from oxal1.tbl_int_ksb1 as q
    where q.id = ? ;
end;
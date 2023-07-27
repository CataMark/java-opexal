begin
    exec sys.sp_set_session_context @key = N'oxal1_user_id', @value = ?, @readonly = 0;

    delete q
    from oxal1.tbl_int_fagll03 as q
    where q.id = ? ;
end;
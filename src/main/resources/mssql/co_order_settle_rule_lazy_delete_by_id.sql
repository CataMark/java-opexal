begin
    declare @kid varchar(20) = ? ;
    exec sys.sp_set_session_context @key = N'oxal1_user_id', @value = @kid, @readonly = 0;

    delete q
    from oxal1.tbl_int_co_order_settle_rule as q
    inner join
        (select
            m.coarea, m.cocode, m.an, m.luna, m.comanda
        from oxal1.tbl_int_co_order_settle_rule as m
        where m.id = ?) as a
    on q.coarea = a.coarea and q.cocode = a.cocode and q.an = a.an and q.luna = a.luna and q.comanda = a.comanda;
end;
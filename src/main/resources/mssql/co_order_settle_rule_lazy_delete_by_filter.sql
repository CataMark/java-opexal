begin
    exec sys.sp_set_session_context @key = N'oxal1_user_id', @value = ?, @readonly = 0;

    delete a
    from oxal1.tbl_int_co_order_settle_rule as a
    inner join
        (select distinct
            q.coarea, q.cocode, q.an, q.luna, q.comanda
        from oxal1.tbl_int_co_order_settle_rule as q

        left join oxal1.tbl_int_co_order as p
        on q.coarea = p.coarea and q.comanda = p.cod

        inner join oxal1.tbl_int_profit_center as r
        on p.profit_center = r.cod

        inner join oxal1.tbl_int_cocode as s
        on q.coarea = s.coarea and q.cocode = s.cod

        left join oxal1.tbl_int_cost_center as t
        on q.coarea = t.coarea and q.cost_center = t.cod

        where q.coarea = ?  and q.an = ?  and q.luna = ? %2$s) as b
    on a.coarea = b.coarea and a.cocode = b.cocode and a.an = b.an and a.luna = b.luna and a.comanda = b.comanda;
end;
begin
    exec sys.sp_set_session_context @key = N'oxal1_user_id', @value = ?, @readonly = 0;

    delete q
    from oxal1.tbl_int_kob1 as q

    outer apply
        (select p3.profit_center, p4.segment
        from
            (select p1.profit_center from oxal1.tbl_int_co_order as p1
                where p1.coarea = q.kokrs and p1.cocode = q.bukrs and p1.cod = q.aufnr
            union
            select p2.profit_center from oxal1.tbl_int_pm_order as p2
                where p2.coarea = q.kokrs and p2.cocode = q.bukrs and p2.cod = q.aufnr) as p3
        inner join oxal1.tbl_int_profit_center as p4
        on p3.profit_center = p4.cod) as p

    left join oxal1.vw_ic_partner as r
    on q.vbund = r.cod

    left join oxal1.tbl_int_sap_oper as s
    on q.vrgng = s.cod

    left join oxal1.tbl_int_sap_oper as t
    on q.orgvg = t.cod

    left join oxal1.tbl_int_sap_doc_tip as u
    on q.blart = u.cod

    where q.gjahr = ? and q.perio = ?  and q.kokrs = ? %s ;
end;
begin
    exec sys.sp_set_session_context @key = N'oxal1_user_id', @value = ?, @readonly = 0;

    delete q
    from oxal1.tbl_int_cji3 as q

    outer apply
        (select p1.profit_center, p2.segment
        from oxal1.tbl_int_wbs as p1
        inner join oxal1.tbl_int_profit_center as p2
        on p1.profit_center = p2.cod
        where p1.coarea = q.kokrs and p1.cocode = q.bukrs and p1.cod = q.posid) as p

    left join oxal1.vw_ic_partner as r
    on q.vbund = r.cod

    left join oxal1.tbl_int_sap_oper as s
    on q.vrgng = s.cod

    left join oxal1.tbl_int_sap_oper as t
    on q.orgvg = t.cod

    left join oxal1.tbl_int_sap_doc_tip as u
    on q.blart = u.cod

    where q.gjahr = ? and q.perio = ? and q.kokrs = ? %s ;
end;
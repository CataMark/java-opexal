begin
    exec sys.sp_set_session_context @key = N'oxal1_user_id', @value = ?, @readonly = 0;

    delete q
    from oxal1.tbl_int_ksb1 as q

    outer apply
        (select p1.profit_center, p3.segment
        from
            (select p2.cocode, p2.profit_center
            from oxal1.tbl_int_cost_center as p2
            where q.kokrs = p2.coarea and q.bukrs = p2.cocode and q.kostl = p2.cod) as p1
        inner join oxal1.tbl_int_profit_center as p3
        on p1.profit_center = p3.cod) as p

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
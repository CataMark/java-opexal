begin
    declare @kid varchar(20) = ?;
    exec sys.sp_set_session_context @key = N'oxal1_user_id', @value = @kid, @readonly = 0;

    update q
    set %1$s , q.mod_de = @kid, q.mod_timp = current_timestamp, q.load_file = null
    from oxal1.tbl_int_fagll03 as q

    left join oxal1.tbl_int_profit_center as p
    on q.prctr = p.cod

    left join oxal1.tbl_int_cost_center as r
    on q.kokrs = r.coarea and q.bukrs = r.cocode and q.kostl = r.cod

    outer apply
        (select s1.nume from oxal1.tbl_int_co_order as s1
            where q.kokrs = s1.coarea and q.bukrs = s1.cocode and q.aufnr = s1.cod
        union
        select s2.nume from oxal1.tbl_int_pm_order as s2
            where q.kokrs = s2.coarea and q.bukrs = s2.cocode and q.aufnr = s2.cod) as s

    left join oxal1.tbl_int_wbs as t
    on q.projk = t.cod

    left join oxal1.vw_ic_partner as u
    on q.vbund = u.cod

    left join oxal1.tbl_int_sap_oper as v
    on q.glvor = v.cod

    left join oxal1.tbl_int_sap_doc_tip as w
    on q.blart = w.cod

    left join oxal1.tbl_int_cont as x
    on q.konto = x.cod

    left join oxal1.tbl_int_material as y
    on q.u_matnr = y.cod

    left join oxal1.tbl_int_vendor as z
    on q.u_lifnr = z.cod

    where q.gjahr = ? and q.monat = ? and q.kokrs = ? %2$s ;
end;
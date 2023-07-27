begin
    declare @kid varchar(20) = ?;
    exec sys.sp_set_session_context @key = N'oxal1_user_id', @value = @kid, @readonly = 0;

    update q1
    set %1$s , q1.mod_de = @kid, q1.mod_timp = current_timestamp, q1.load_file = null, q1.acuratete = null
    from oxal1.tbl_int_process_result as q1
    
    inner join oxal1.tbl_int_acc_ledgers as q2
    on q1.u_ldgrp = q2.grup

    left join oxal1.tbl_int_cost_center as p1
    on q1.kokrs = p1.coarea and q1.kostl = p1.cod
    left join oxal1.tbl_int_profit_center as p2
    on p1.profit_center = p2.cod

    left join oxal1.tbl_int_co_order as r1
    on q1.kokrs = r1.coarea and q1.aufnr = r1.cod
    left join oxal1.tbl_int_profit_center as r2
    on r1.profit_center = r2.cod
    left join oxal1.tbl_int_cost_center as r3
    on r1.coarea = r3.coarea and r1.cost_center_resp = r3.cod

    outer apply (select * from oxal1.fnc_co_order_settle_rule_last_period(q1.kokrs, q1.gjahr, q1.perio, q1.aufnr)) as r4
    left join oxal1.tbl_int_co_order_settle_rule as r5
    on r1.coarea = r5.coarea and r4.comanda = r5.comanda and r4.an = r5.an and r4.luna = r5.luna
    left join oxal1.tbl_int_cost_center as r6
    on r5.coarea = r6.coarea and r5.cost_center = r6.cod

    left join oxal1.tbl_int_pm_order as s1
    on q1.kokrs = s1.coarea and q1.aufnr = s1.cod
    left join oxal1.tbl_int_profit_center as s2
    on s1.profit_center = s2.cod
    left join oxal1.tbl_int_cost_center as s3
    on s1.coarea = s3.coarea and s1.cost_center_resp = s3.cod

    left join oxal1.tbl_int_wbs as t1
    on q1.kokrs = t1.coarea and q1.posid = t1.cod
    left join oxal1.tbl_int_profit_center as t2
    on t1.profit_center = t2.cod
    left join oxal1.tbl_int_cost_center as t3
    on t1.coarea = t3.coarea and t1.cost_center_resp = t3.cod

    left join oxal1.vw_ic_partner as u1
    on q1.vbund = u1.cod

    left join oxal1.tbl_int_sap_oper as v1
    on q1.vrgng = v1.cod

    left join oxal1.tbl_int_sap_doc_tip as v2
    on q1.blart = v2.cod

    left join oxal1.tbl_int_cont as x1
    on q1.kstar = x1.cod

    left join oxal1.tbl_int_material as y1
    on q1.matnr = y1.cod

    left join oxal1.tbl_int_vendor as z1
    on q1.u_lifnr = z1.cod

    left join oxal1.vw_opex_categ as z2
    on q1.ocateg = z2.cod

    where q1.gjahr = ? and q1.perio = ? and q1.kokrs = ? and q2.ledger = ? %2
end;
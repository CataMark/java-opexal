select
    row_number() over (order by %1$s (select null) asc) as c_rand,
    q1.id,
    q2.stndrd,
    q1.u_ldgrp,
    q1.sap_tranz,
    q1.kokrs,
    q1.bukrs,
    q1.gjahr,
    q1.perio,
    isnull(p2.segment, isnull(r2.segment, isnull(s2.segment, t2.segment))) as segment,
    isnull(p1.profit_center,
        isnull(r1.profit_center,
            isnull(s1.profit_center,
                t1.profit_center
            )
        )
    ) as prctr,
    isnull(q1.kostl,
        isnull(r5.cost_center,
            isnull(r1.cost_center_resp,
                isnull(s1.cost_center_resp,
                    t1.cost_center_resp
                )
            )
        )
    ) as kostl,
    isnull(p1.nume,
        isnull(r6.nume,
            isnull(r3.nume,
                isnull(s3.nume,
                    t3.nume
                )
            )
        )
    ) as kostl_nume,
    isnull(cast(r1.cod as varchar(10)), cast(s1.cod as varchar(10))) as aufnr,
    isnull(r1.nume, s1.nume) as aufnr_nume,
    q1.posid,
    t1.nume as posid_nume,
    q1.vbund,
    u1.nume as vbund_nume,
    q1.pobart,
    q1.pobid,
    q1.pob_txt,
    q1.objnr_n1,
    q1.vrgng,
    v1.nume as vrgng_nume,
    q1.u_tcode,
    q1.logsystem,
    q1.blart,
    v2.nume as blart_nume,
    q1.kstar,
    x1.nume as kstar_nume,
    x1.alternativ,
    q1.sgtxt,
    q1.bltxt,
    q1.belnr,
    q1.buzei,
    q1.zzco_belnr,
    q1.refbz_fi,
    q1.werks,
    q1.ebeln,
    q1.ebelp,
    q1.ebtxt,
    q1.matnr,
    y1.nume as matnr_nume,
    q1.gbeextwg_ebx,
    q1.refbt,
    q1.refgj,
    q1.refbn,
    q1.stokz,
    q1.awref_rev,
    q1.gkoar,
    q1.gkont,
    q1.gkont_ltxt,
    q1.u_lifnr,
    z1.nume as u_lifnr_nume,
    (q1.wrgbtr * isnull(r5.procent, 1)) as wrgbtr,
    q1.kwaer,
    (q1.wtgbtr * isnull(r5.procent, 1)) as wtgbtr,
    q1.twaer,
    (q1.mbgbtr * isnull(r5.procent, 1)) as mbgbtr,
    q1.meinb,
    q1.usnam,
    q1.wsdat,
    q1.budat,
    q1.bldat,
    q1.cpudt,
    z2.cost_driver,
    q1.ocateg,
    z2.nume as ocateg_nume,
    q1.acuratete,
    (case when q1.acuratete > 0.8 then 'H' when q1.acuratete > 0.6 then 'M' else 'L' end) as acuratete_flag,
    q1.mod_de,
    q1.mod_timp
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

where q1.gjahr = ? and q1.perio = ? and q1.kokrs = ? and q2.ledger = ? %2$s


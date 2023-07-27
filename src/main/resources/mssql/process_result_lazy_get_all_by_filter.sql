select
    q1.id,
    q2.stndrd as [Standard],
    q1.u_ldgrp as [Ledger Group],
    q1.sap_tranz as [Sap Tranz.],
    q1.kokrs as [Controlling Area],
    q1.bukrs as [Company Code],
    q1.gjahr as [Fiscal Year],
    q1.perio as [Posting period],
    isnull(p2.segment, isnull(r2.segment, isnull(s2.segment, t2.segment))) as Segment,
    isnull(p1.profit_center,
        isnull(r1.profit_center,
            isnull(s1.profit_center,
                t1.profit_center
            )
        )
    ) as [Profit Center],
    isnull(q1.kostl,
        isnull(r5.cost_center,
            isnull(r1.cost_center_resp,
                isnull(s1.cost_center_resp,
                    t1.cost_center_resp
                )
            )
        )
    ) as [Cost Center],
    isnull(p1.nume,
        isnull(r6.nume,
            isnull(r3.nume,
                isnull(s3.nume,
                    t3.nume
                )
            )
        )
    ) as [Cost Center name],
    isnull(cast(r1.cod as varchar(10)), cast(s1.cod as varchar(10))) as [Order],
    isnull(r1.nume, s1.nume) as [Order name],
    q1.posid as [WBS element],
    t1.nume as [WBS element name],
    q1.vbund as [Trading partner],
    u1.nume as [Trading partner name],
    q1.pobart as [Partner object type],
    q1.pobid as [Partner object],
    q1.pob_txt as [CO partner object name],
    q1.objnr_n1 as [Aux. acct assignment],
    q1.vrgng as [Business Transaction],
    v1.nume as [Business Trans. name],
    q1.u_tcode as [Transaction Code],
    q1.logsystem as [Logical System],
    q1.blart as [Document type],
    v2.nume as [Doc. type name],
    q1.kstar as [Cost Eelement],
    x1.nume as [Cost element name],
    x1.alternativ as [Alternative Account No.],
    q1.sgtxt as [Name],
    q1.bltxt as [Document header text],
    q1.belnr as [Document Number],
    q1.buzei as [Posting row],
    q1.zzco_belnr as [FI Document Number],
    q1.refbz_fi as [FI Posting Item],
    q1.werks as Plant,
    q1.ebeln as [Purchasing Document],
    q1.ebelp as Item,
    q1.ebtxt as [Purchase order text],
    q1.matnr as Material,
    y1.nume as [Material Description],
    q1.gbeextwg_ebx as [Ext. Material Group],
    q1.refbt as [Ref. document type],
    q1.refgj as [Ref. fiscal year],
    q1.refbn as [Ref. document number],
    q1.stokz as Reversed,
    q1.awref_rev as [Reversal referen. no.],
    q1.gkoar as [Offsetting account type],
    q1.gkont as [Offsetting acct no.],
    q1.gkont_ltxt as [Name of offset. account],
    q1.u_lifnr as [Vendor],
    z1.nume as [Vendor name],
    (q1.wrgbtr * isnull(r5.procent, 1)) as [Val. in rep cur.],
    q1.kwaer as [CO area currency],
    (q1.wtgbtr * isnull(r5.procent, 1)) as [Value TranCurr],
    q1.twaer as [Transaction Currency],
    (q1.mbgbtr * isnull(r5.procent, 1)) as [Total quantity],
    q1.meinb as [Posted unit of meas.],
    q1.usnam as [User Name],
    q1.wsdat as [Value date],
    q1.budat as [Posting date],
    q1.bldat as [Document Date],
    q1.cpudt as [Created on],
    z2.cost_driver as [Cost driver],
    q1.ocateg as [OPEX cat.],
    z2.nume as [OPEX cat. name],
    q1.acuratete as [Accuracy],
    (case when q1.acuratete > 0.8 then 'H' when q1.acuratete > 0.6 then 'M' else 'L' end) as [Accuracy Flag],
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

where q1.gjahr = ? and q1.perio = ? and q1.kokrs = ? and q2.ledger = ? %s ;
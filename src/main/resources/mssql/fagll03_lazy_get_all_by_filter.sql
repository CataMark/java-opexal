select
    q.kokrs as [Controlling Area],
    q.bukrs as [Company Code],
    q.prctr as [Profit Center],
    p.segment as Segment,
    q.rldnr as [Ledger],
    q.u_ldgrp as [Ledger Group],
    q.gjahr as [Fiscal Year],
    q.monat as [Posting period],
    q.kostl as [Cost Center],
    r.nume as [Cost Center name],
    q.aufnr as [Order],
    s.nume as [Order name],
    q.projk as [WBS element],    
    t.nume as [WBS element name],
    q.vbund as [Trading partner],
    u.nume as [Trading partner name],
    q.glvor as [Business Transaction],
    v.nume as [Business Transaction name],
    q.u_tcode as [Transaction Code],
    q.blart as [Document type],
    w.nume as [Document type name],
    q.shkzg as [Debit/Credit ind],
    q.konto as [Account],
    q.u_lokkt as [Alternative Account No.],
    x.nume as [Account name],
    q.sgtxt as [Text],
    q.u_bktxt as [Document Header Text],
    q.belnr as [Document Number],
    q.buzei as [Line item],
    q.werks as [Plant],
    q.u_ebeln as [Purchasing Document],
    q.ebelp as [Item],
    q.u_matnr as [Material],
    y.nume as [Material name],
    q.gbeextwg_ebx as [Ext. Material Group],
    q.gkart as [Offsett.account type],
    q.gkont as [Offsetting acct no.],
    q.u_lifnr as [Vendor],
    z.nume as [Vendor name],
    q.dmshb as [Amount in local currency],
    q.hwaer as [Local Currency],
    q.wrshb as [Amount in doc. curr.],
    q.waers as [Document currency],
    q.menge as [Quantity],
    q.u_usnam as [User Name],
    q.valut as [Value date],
    q.budat as [Posting Date],
    q.bldat as [Document Date],
    q.u_cpudt as [Entry Date],
    q.mod_de,
    q.mod_timp
from oxal1.tbl_int_fagll03 as q

left join oxal1.tbl_int_profit_center as p
on q.prctr = p.cod

left join oxal1.tbl_int_cost_center as r
on q.kostl = r.cod

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

where q.gjahr = ? and q.monat = ? and q.kokrs = ? %s ;
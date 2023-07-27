select
    q.kokrs as [Controlling Area],
    q.bukrs as [Company Code],
    p.profit_center as [Profit Center],
    p.segment as Segment,
    q.gjahr as [Fiscal Year],
    q.perio as [Period],
    q.obart as [Object type],
    q.aufnr as [Order],
    q.obj_txt as [CO object name],
    q.vbund as [Trading partner],
    r.nume as [Trading partner name],
    q.pobart as [Partner object type],
    q.pobid as [Partner object],
    q.pob_txt as [CO partner object name],
    q.objnr_n1 as [Aux. acc assignment],
    q.vrgng as [Business Transaction],
    s.nume as [Business Trans. name],
    q.orgvg as [Original bus. trans],
    t.nume as [Orig. bus. trans. name],
    q.logsystem as [Logical System],
    q.blart as [Document type],
    u.nume as [Doc. type name],
    q.beknz as [Dr/Cr indicator],
    q.kstar as [Cost Element],
    q.cel_ktxt as [Cost element name],
    q.sgtxt as [Name],
    q.bltxt as [Document header text],
    q.belnr as [Document Number],
    q.buzei as [Posting row],
    q.zzco_belnr as [FI Document Number],
    q.refbz_fi as [FI Posting Item],
    q.werks as Plant,
    q.ebeln as [Purchasing Document],
    q.ebelp as Item,
    q.ebtxt as [Purchase order text],
    q.matnr as Material,
    q.mat_txt as [Material Description],
    q.gbeextwg_ebx as [Ext. Material Group],
    q.refbt as [Ref. document type],
    q.refgj as [Ref. fiscal year],
    q.refbn as [Ref. document number],
    q.stokz as Reversed,
    q.awref_rev as [Reversal referen. no.],
    q.gkoar as [Offsetting account type],
    q.gkont as [Offsetting acct no.],
    q.gkont_ktxt as [Name of offset. acount],
    q.zz_lif as [Vendor],
    q.wrgbtr as [Val. in rep cur.],
    q.kwaer as [CO area currency],
    q.wtgbtr as [Value TranCurr],
    q.twaer as [Transaction Currency],
    q.mbgbtr as [Total quantity],
    q.meinb as [Posted unit of meas.],
    q.usnam as [User Name],
    q.wsdat as [Value date],
    q.budat as [Posting date],
    q.bldat as [Document Date],
    q.cpudt as [Created on],
    q.mod_de,
    q.mod_timp
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
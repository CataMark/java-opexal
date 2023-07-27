select
    row_number() over (order by %1$s (select null) asc) as c_rand,
    q.*,
    p.profit_center,
    p.segment,
    r.nume as vbund_nume,
    s.nume as vrgng_nume,
    t.nume as orgvg_nume,
    u.nume as blart_nume
    
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

where q.gjahr = ? and q.perio = ? and q.kokrs = ? %2$s
select
    q.cod,
    q.coarea,
    q.cocode,    
    p.nume as cocode_nume,
    q.nume,
    q.profit_center,
    r.segment,
    q.cost_center_resp,
    s.nume as cost_center_resp_nume,
    q.mod_de,
    q.mod_timp
from oxal1.tbl_int_pm_order as q

inner join oxal1.tbl_int_cocode as p
on q.coarea = p.coarea and q.cocode = p.cod

left join oxal1.tbl_int_profit_center as r
on q.profit_center = r.cod

left join oxal1.tbl_int_cost_center as s
on q.coarea = s.coarea and q.cost_center_resp = s.cod

where q.coarea = ? %s ;
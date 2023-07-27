select
    row_number() over (order by %1$s q.coarea asc) as c_rand,
    q.*,
    p.nume as cocode_nume,
    r.segment,
    s.nume as cost_center_resp_nume
from oxal1.tbl_int_pm_order as q

inner join oxal1.tbl_int_cocode as p
on q.coarea = p.coarea and q.cocode = p.cod

left join oxal1.tbl_int_profit_center as r
on q.profit_center = r.cod

left join oxal1.tbl_int_cost_center as s
on q.coarea = s.coarea and q.cost_center_resp = s.cod

where q.coarea = ? %2$s
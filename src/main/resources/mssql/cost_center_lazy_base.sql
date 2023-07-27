select
    row_number() over (order by %1$s (select null) asc) as c_rand,
    q.*,
    p.nume as cocode_nume,
    r.segment
from oxal1.tbl_int_cost_center as q

inner join oxal1.tbl_int_cocode as p
on q.coarea = p.coarea and q.cocode = p.cod

left join oxal1.tbl_int_profit_center as r
on q.profit_center = r.cod

where q.coarea = ? %2$s
select
    row_number() over (order by %1$s q.coarea asc) as c_rand,
    q.*,
    p.nume as comanda_nume,
    s.nume as cocode_nume,
    r.segment,
    t.nume as cost_center_nume
from oxal1.tbl_int_co_order_settle_rule as q

left join oxal1.tbl_int_co_order as p
on q.coarea = p.coarea and q.comanda = p.cod

inner join oxal1.tbl_int_profit_center as r
on p.profit_center = r.cod

inner join oxal1.tbl_int_cocode as s
on q.coarea = s.coarea and q.cocode = s.cod

left join oxal1.tbl_int_cost_center as t
on q.coarea = t.coarea and q.cost_center = t.cod

where q.coarea = ?  and q.an = ?  and q.luna = ? %2$s
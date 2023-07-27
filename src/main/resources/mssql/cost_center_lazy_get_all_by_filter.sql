select
    q.cod,
    q.coarea,
    q.cocode,
    p.nume as cocode_nume,
    q.nume,
    q.profit_center,
    r.segment,
    q.mod_de,
    q.mod_timp
from oxal1.tbl_int_cost_center as q

inner join oxal1.tbl_int_cocode as p
on q.coarea = p.coarea and q.cocode = p.cod

left join oxal1.tbl_int_profit_center as r
on q.profit_center = r.cod

where q.coarea = ? %s ;


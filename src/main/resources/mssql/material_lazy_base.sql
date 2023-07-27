select
    row_number() over (order by %1$s (select null) asc) as c_rand,
    q.*
from oxal1.tbl_int_material as q

where 1=1 %2$s
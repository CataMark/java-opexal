select
    q.cod,
    q.nume,
    q.mod_de,
    q.mod_timp
from oxal1.tbl_int_vendor as q
where 1=1 %s ;
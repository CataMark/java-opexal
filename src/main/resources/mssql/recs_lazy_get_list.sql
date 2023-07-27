select top %1$s *
from ( %2$s ) as a
where a.c_rand > ?
order by a.c_rand asc;
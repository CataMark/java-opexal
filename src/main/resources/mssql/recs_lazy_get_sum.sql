select coalesce(sum( %1$s ),0) as suma
from ( %2$s ) as a;
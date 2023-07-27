create or alter procedure oxal1.prc_perioade_get_by_an
    @an smallint
as
    select * from oxal1.tbl_int_perioade
    where an = @an
    order by luna asc;
create or alter procedure oxal1.prc_segment_get_all
as
    select * from oxal1.tbl_int_segment as a
    order by a.cod asc;
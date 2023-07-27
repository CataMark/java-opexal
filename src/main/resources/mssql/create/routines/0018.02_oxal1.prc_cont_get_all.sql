create or alter procedure oxal1.prc_cont_get_all
as
    select * from oxal1.tbl_int_cont as a
    order by a.cod asc;
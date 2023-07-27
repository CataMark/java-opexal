create or alter procedure oxal1.prc_perioade_get_ani
as
    select distinct an from oxal1.tbl_int_perioade
    order by an asc;
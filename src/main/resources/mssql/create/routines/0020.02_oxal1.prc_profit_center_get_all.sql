create or alter procedure oxal1.prc_profit_center_get_all
as
    select * from oxal1.tbl_int_profit_center as a
    order by a.cod asc;
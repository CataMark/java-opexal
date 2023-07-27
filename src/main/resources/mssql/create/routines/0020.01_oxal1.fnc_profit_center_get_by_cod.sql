create or alter function oxal1.fnc_profit_center_get_by_cod(
    @cod char(10)
)
returns table
as
return
    select * from oxal1.tbl_int_profit_center as a
    where cod = @cod;
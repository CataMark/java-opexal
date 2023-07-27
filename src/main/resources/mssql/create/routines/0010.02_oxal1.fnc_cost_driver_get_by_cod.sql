create or alter function oxal1.fnc_cost_driver_get_by_cod(
    @cod char(5)
)
returns table
as
return
    select * from oxal1.vw_cost_driver where cod = @cod;
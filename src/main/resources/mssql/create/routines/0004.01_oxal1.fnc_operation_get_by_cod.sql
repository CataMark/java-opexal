create or alter function oxal1.fnc_operation_get_by_cod(
    @cod char(4)
)
returns table
as
return
    select * from oxal1.tbl_int_operations where cod = @cod;
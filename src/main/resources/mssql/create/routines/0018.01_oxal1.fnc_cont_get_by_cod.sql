create or alter function oxal1.fnc_cont_get_by_cod(
    @cod char(10)
)
returns table
as
return
    select * from oxal1.tbl_int_cont as a
    where a.cod = @cod;
create or alter function oxal1.fnc_coarea_get_by_cod(
    @cod char(4)
)
returns table
as
return
    select top 1 * from oxal1.tbl_int_coarea where cod = @cod;
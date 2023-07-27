create or alter function oxal1.fnc_material_get_by_cod(
    @cod char(10)
)
returns table
as
return
    select
        *
    from oxal1.tbl_int_material as a
    where a.cod = @cod;
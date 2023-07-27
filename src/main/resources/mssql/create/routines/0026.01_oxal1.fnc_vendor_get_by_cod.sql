create or alter function oxal1.fnc_vendor_get_by_cod(
    @cod char(7)
)
returns table
as
return
    select
        *
    from oxal1.tbl_int_vendor as a
    where a.cod = @cod;
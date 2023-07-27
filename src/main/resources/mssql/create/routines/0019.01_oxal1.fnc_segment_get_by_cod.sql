create or alter function oxal1.fnc_segment_get_by_cod(
    @cod varchar(30)
)
returns table
as
return
    select * from oxal1.tbl_int_segment as a
    where a.cod = @cod;
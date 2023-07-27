create or alter function oxal1.fnc_perioade_get_by_id(
    @id uniqueidentifier
)
returns table
as
return
    select * from oxal1.tbl_int_perioade where id = @id;
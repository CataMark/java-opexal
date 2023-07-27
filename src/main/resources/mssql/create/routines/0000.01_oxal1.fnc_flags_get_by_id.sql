create or alter function oxal1.fnc_flags_get_by_id(
    @id uniqueidentifier
)
returns table
as
return
    select * from oxal1.tbl_int_flags
    where id = @id;
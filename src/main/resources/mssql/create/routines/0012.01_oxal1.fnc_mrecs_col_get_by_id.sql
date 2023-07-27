create or alter function oxal1.fnc_mrecs_col_get_by_id(
    @id uniqueidentifier
)
returns table
as
return
    select * from oxal1.tbl_int_mrecs_columns as a
    where a.id = @id;
create or alter function oxal1.fnc_upload_matrix_get_by_id(
    @id uniqueidentifier
)
returns table
as
return
    select * from oxal1.tbl_int_upload_matrix where id = @id;
create or alter procedure oxal1.prc_clasificator_get_by_id
    @id uniqueidentifier
as
    select * from oxal1.tbl_int_clasificator_log as a
    where a.id = @id;
create or alter procedure oxal1.prc_flags_delete_by_list
    @uuidArray nvarchar(max)
as
    delete a from oxal1.tbl_int_flags as a
    inner join (select cast([value] as uniqueidentifier) as uuid from openjson(@uuidArray)
                where [value] is not null) as b
    on a.uuid = b.uuid;
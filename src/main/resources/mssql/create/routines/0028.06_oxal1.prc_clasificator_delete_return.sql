create or alter procedure oxal1.prc_clasificator_delete_return
    @id uniqueidentifier
as
    begin
        set nocount on;
        delete from oxal1.tbl_int_clasificator_log where id = @id;

        if exists (select * from oxal1.tbl_int_clasificator_log where id = @id)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;
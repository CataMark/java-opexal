create or alter procedure oxal1.prc_mrecs_col_delete_return
    @id uniqueidentifier
as
    begin
        set nocount on;

        delete from oxal1.tbl_int_mrecs_columns where id = @id;

        /* testare operatiunea a reusit */
        if exists (select * from oxal1.tbl_int_mrecs_columns where id = @id)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;
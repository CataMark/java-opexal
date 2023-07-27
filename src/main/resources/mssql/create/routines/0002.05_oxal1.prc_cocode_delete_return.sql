create or alter procedure oxal1.prc_cocode_delete_return
    @cod char(4)
as
    begin
        set nocount on;

        delete from oxal1.tbl_int_cocode where cod = @cod;

        /* testare operatiunea a reusit */
        if exists (select * from oxal1.tbl_int_cocode where cod = @cod)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;
create or alter procedure oxal1.prc_segment_delete_return
    @cod varchar(30)
as
    begin
        set nocount on;

        delete from oxal1.tbl_int_segment
        where cod = @cod;

        /* testare operatiunea a reusit */
        if exists (select * from oxal1.tbl_int_segment where cod = @cod)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;
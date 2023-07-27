create or alter procedure oxal1.prc_profit_center_delete_return
    @cod char(10)
as
    begin
        set nocount on;

        delete from oxal1.tbl_int_profit_center
        where cod = @cod;

        /* testare operatiunea a reusit */
        if exists (select * from oxal1.tbl_int_profit_center where cod = @cod)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;
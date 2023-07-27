create or alter procedure oxal1.prc_co_order_delete_return
    @cod char(9)
as
    begin
        set nocount on;

        delete from oxal1.tbl_int_co_order where cod = @cod;

        /* testare operatiune reusita */
        if exists (select * from oxal1.tbl_int_co_order as a where a.cod = @cod)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;
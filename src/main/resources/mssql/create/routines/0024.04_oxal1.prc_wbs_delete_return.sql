create or alter procedure oxal1.prc_wbs_delete_return
    @cod varchar(35)
as
    begin
        set nocount on;

        delete from oxal1.tbl_int_wbs where cod = @cod;

        /* testare operatiune reusita */
        if exists (select * from oxal1.tbl_int_wbs as a where a.cod = @cod)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;
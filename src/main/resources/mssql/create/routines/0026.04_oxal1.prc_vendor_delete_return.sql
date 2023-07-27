create or alter procedure oxal1.prc_vendor_delete_return
    @cod char(7)
as
    begin
        set nocount on;

        delete from oxal1.tbl_int_vendor where cod = @cod;

        if exists (select * from oxal1.tbl_int_vendor as a where a.cod = @cod)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;
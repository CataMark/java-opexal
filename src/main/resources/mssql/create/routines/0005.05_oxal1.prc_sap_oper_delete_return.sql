create or alter procedure oxal1.prc_sap_oper_delete_return
    @cod varchar(5)
as
    begin
        set nocount on;

        delete from oxal1.tbl_int_sap_oper where cod = @cod;

        /* testare operatiune a reusit */
        if exists (select * from oxal1.tbl_int_sap_oper where cod = @cod)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;
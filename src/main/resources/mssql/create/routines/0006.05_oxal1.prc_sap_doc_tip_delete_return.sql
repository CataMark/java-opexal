create or alter procedure oxal1.prc_sap_doc_tip_delete_return
    @cod char(2)
as
    begin
        set nocount on;

        delete from oxal1.tbl_int_sap_doc_tip where cod = @cod;
        /* testare operatiune reusita */
        if exists (select * from oxal1.tbl_int_sap_doc_tip where cod = @cod)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;
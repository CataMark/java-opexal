create or alter procedure oxal1.prc_sap_doc_tip_update_return
    @cod varchar(2),
    @nume nvarchar(100),
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxal1.tbl_int_sap_doc_tip
        set nume = @nume, mod_de = @kid, mod_timp = current_timestamp
        where cod = @cod;

        select * from oxal1.fnc_sap_doc_tip_get_by_cod(@cod);
    end;
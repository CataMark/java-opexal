create or alter procedure oxal1.prc_sap_oper_update_return
    @cod varchar(5),
    @nume nvarchar(100),
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxal1.tbl_int_sap_oper
        set nume = @nume, mod_de = @kid, mod_timp = current_timestamp
        where cod = @cod;

        select * from oxal1.fnc_sap_oper_get_by_cod(@cod);
    end;
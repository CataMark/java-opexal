create or alter function oxal1.fnc_sap_oper_get_by_cod(
    @cod varchar(5)
)
returns table
as
return
    select * from oxal1.tbl_int_sap_oper
    where cod = @cod;
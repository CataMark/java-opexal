create or alter function oxal1.fnc_sap_tranz_get_by_cod(
    @cod varchar(10)
)
returns table
as
return
    select * from oxal1.tbl_int_sap_tranz as a
    where a.cod = @cod;
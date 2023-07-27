create or alter function oxal1.fnc_sap_doc_tip_get_by_cod(
    @cod char(2)
)
returns table
as
return
    select * from oxal1.tbl_int_sap_doc_tip
    where cod = @cod;
create or alter procedure oxal1.prc_sap_tranz_get_all
as
    select a.*
    from oxal1.tbl_int_sap_tranz as a
    order by a.tip desc, a.cod asc;
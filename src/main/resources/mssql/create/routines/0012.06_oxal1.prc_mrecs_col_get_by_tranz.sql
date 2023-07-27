create or alter procedure oxal1.prc_mrecs_col_get_by_tranz
    @sap_tranz varchar(10)
as
    select * from oxal1.tbl_int_mrecs_columns
    where sap_tranz = @sap_tranz;
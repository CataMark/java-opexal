create or alter procedure oxal1.prc_upload_matrix_get_all
as
    select * from oxal1.tbl_int_upload_matrix
    order by cocode asc, sap_tranz asc;
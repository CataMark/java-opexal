create or alter procedure oxal1.prc_upload_matrix_update_return
    @id uniqueidentifier,
    @blocat bit,
    @kid varchar(20)
as
    begin
        set nocount on;
        
        update oxal1.tbl_int_upload_matrix
        set blocat = @blocat, mod_de = @kid, mod_timp = current_timestamp;

        select * from oxal1.fnc_upload_matrix_get_by_id(@id);
    end;
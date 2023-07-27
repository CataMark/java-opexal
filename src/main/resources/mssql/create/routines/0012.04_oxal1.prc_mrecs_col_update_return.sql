create or alter procedure oxal1.prc_mrecs_col_update_return
    @id uniqueidentifier,
    @nume nvarchar(50),
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxal1.tbl_int_mrecs_columns
        set nume = @nume, mod_de = @kid, mod_timp = current_timestamp
        where id = @id;

        select * from oxal1.fnc_mrecs_col_get_by_id(@id);
    end;
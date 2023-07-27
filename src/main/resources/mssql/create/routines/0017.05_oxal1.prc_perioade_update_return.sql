create or alter procedure oxal1.prc_perioade_update_return
    @id uniqueidentifier,
    @inchis bit,
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxal1.tbl_int_perioade
        set inchis = @inchis, mod_de = @kid, mod_timp = current_timestamp
        where id =@id;

        select * from oxal1.fnc_perioade_get_by_id(@id);
    end;
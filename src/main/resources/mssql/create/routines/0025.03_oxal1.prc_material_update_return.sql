create or alter procedure oxal1.prc_material_update_return
    @cod char(10),
    @nume nvarchar(100),
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxal1.tbl_int_material
        set nume = @nume, mod_de = @kid, mod_timp = current_timestamp
        where cod = @cod;

        select * from oxal1.fnc_material_get_by_cod(@cod);
    end;
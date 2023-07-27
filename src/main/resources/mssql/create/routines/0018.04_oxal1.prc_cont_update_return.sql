create or alter procedure oxal1.prc_cont_update_return
    @cod char(10),
    @alternativ char(10),
    @nume nvarchar(100),
    @grup char(10),
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxal1.tbl_int_cont
        set alternativ = @alternativ, nume = @nume, grup = @grup, mod_de = @kid, mod_timp = current_timestamp
        where cod = @cod;

        select * from oxal1.fnc_cont_get_by_cod(@cod);
    end;
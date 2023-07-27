create or alter procedure oxal1.prc_profit_center_update_return
    @cod char(10),
    @segment varchar(30),
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxal1.tbl_int_profit_center
        set segment = @segment, mod_de = @kid, mod_timp = current_timestamp
        where cod = @cod;

        select * from oxal1.fnc_profit_center_get_by_cod(@cod);
    end;
create or alter procedure oxal1.prc_cost_center_update_return
    @cod char(10),
    @coarea char(4),
    @nume nvarchar(100),
    @profit_center char(10),
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxal1.tbl_int_cost_center
        set nume = @nume, profit_center = @profit_center, mod_de = @kid, mod_timp = current_timestamp
        where cod = @cod and coarea = @coarea;

        select * from oxal1.fnc_cost_center_get_by_cod(@coarea, @cod);
    end;
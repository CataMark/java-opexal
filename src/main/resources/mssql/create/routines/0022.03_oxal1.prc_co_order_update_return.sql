create or alter procedure oxal1.prc_co_order_update_return
    @cod char(9),
    @nume nvarchar(100),
    @profit_center char(10),
    @cost_center_resp char(10),
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxal1.tbl_int_co_order
        set nume = @nume, profit_center = @profit_center, cost_center_resp = @cost_center_resp, mod_de = @kid, mod_timp = current_timestamp
        where cod = @cod;

        select * from oxal1.fnc_co_order_get_by_cod(@cod);
    end;
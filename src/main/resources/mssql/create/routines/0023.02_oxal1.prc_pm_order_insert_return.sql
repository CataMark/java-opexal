create or alter procedure oxal1.prc_pm_order_insert_return
    @cod char(10),
    @coarea char(4),
    @cocode char(4),
    @nume nvarchar(100),
    @profit_center char(10),
    @cost_center_resp char(10),
    @kid varchar(20)
as
    begin
        set nocount on;

        insert into oxal1.tbl_int_pm_order(cod, coarea, cocode, nume, profit_center, cost_center_resp, mod_de, mod_timp)
        values (@cod, @coarea, @cocode, @nume, @profit_center, @cost_center_resp, @kid, current_timestamp);

        select * from oxal1.fnc_pm_order_get_by_cod(@cod);
    end;
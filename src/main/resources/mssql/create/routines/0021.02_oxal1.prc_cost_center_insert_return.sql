create or alter procedure oxal1.prc_cost_center_insert_return
    @cod char(10),
    @coarea char(4),
    @cocode char(4),
    @nume nvarchar(100),
    @profit_center char(10),
    @kid varchar(20)
as
    begin
        set nocount on;

        insert into oxal1.tbl_int_cost_center (cod, coarea, cocode, nume, profit_center, mod_de, mod_timp)
        values (@cod, @coarea, @cocode, @nume, @profit_center, @kid, current_timestamp);

        select * from oxal1.fnc_cost_center_get_by_cod(@coarea, @cod);
    end;
create or alter procedure oxal1.prc_profit_center_insert_return
    @cod char(10),
    @segment varchar(30),
    @kid varchar(20)
as
    begin
        set nocount on;

        insert into oxal1.tbl_int_profit_center(cod, segment, mod_de, mod_timp)
        values (@cod, @segment, @kid, current_timestamp);

        select * from oxal1.fnc_profit_center_get_by_cod(@cod);
    end;
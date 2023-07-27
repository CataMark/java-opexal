create or alter procedure oxal1.prc_cocode_insert_return
    @cod char(4),
    @nume nvarchar(100),
    @coarea char(4),
    @kid varchar(20)
as
    begin
        set nocount on;

        insert into oxal1.tbl_int_cocode(cod, nume, coarea, mod_de, mod_timp)
        values (@cod, @nume, @coarea, @kid, current_timestamp);

        select * from oxal1.fnc_cocode_get_by_cod(@cod);
    end;
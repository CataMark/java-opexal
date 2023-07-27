create or alter procedure oxal1.prc_coarea_insert_return
    @cod char(4),
    @nume nvarchar(100),
    @kid varchar(20)
as
    begin
        set nocount on;

        insert into oxal1.tbl_int_coarea (cod, nume, mod_de, mod_timp)
        values (@cod, @nume, @kid, current_timestamp);

        select * from oxal1.fnc_coarea_get_by_cod(@cod);
    end;
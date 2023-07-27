create or alter procedure oxal1.prc_vendor_insert_return
    @cod char(7),
    @nume nvarchar(100),
    @kid varchar(20)
as
    begin
        set nocount on;

        insert into oxal1.tbl_int_vendor(cod, nume, mod_de, mod_timp)
        values (@cod, @nume, @kid, current_timestamp);

        select * from oxal1.fnc_vendor_get_by_cod(@cod);
    end;
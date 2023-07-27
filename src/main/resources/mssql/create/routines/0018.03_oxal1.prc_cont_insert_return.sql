create or alter procedure oxal1.prc_cont_insert_return
    @cod char(10),
    @alternativ char(10),
    @nume nvarchar(100),
    @grup char(10),
    @kid varchar(20)
as
    begin
        set nocount on;

        insert into oxal1.tbl_int_cont (cod, alternativ, nume, grup, mod_de, mod_timp)
        values (@cod, @alternativ, @nume, @grup, @kid, current_timestamp);

        select * from oxal1.fnc_cont_get_by_cod(@cod);
    end;
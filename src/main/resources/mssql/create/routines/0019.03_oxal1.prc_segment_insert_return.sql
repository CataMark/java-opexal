create or alter procedure oxal1.prc_segment_insert_return
    @cod varchar(30),
    @kid varchar(20)
as
    begin
        set nocount on;

        insert into oxal1.tbl_int_segment (cod, mod_de, mod_timp)
        values (@cod, @kid, current_timestamp);

        select * from oxal1.fnc_segment_get_by_cod(@cod);
    end;
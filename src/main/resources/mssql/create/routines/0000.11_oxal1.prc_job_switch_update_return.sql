create or alter procedure oxal1.prc_job_switch_update_return
    @cod varchar(30),
    @blocat bit,
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxal1.tbl_int_job_switch
        set blocat = @blocat, mod_de = @kid, mod_timp = current_timestamp
        where cod = @cod;

        select * from oxal1.fnc_job_switch_get_by_cod(@cod);
    end;
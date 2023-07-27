create or alter function oxal1.fnc_job_switch_get_by_cod(
    @cod varchar(30)
)
returns table
as
return
    select * from oxal1.tbl_int_job_switch as a
    where a.cod = @cod;
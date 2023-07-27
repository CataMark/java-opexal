create or alter function oxal1.fnc_acc_inteval_get_by_id(
    @id uniqueidentifier
)
returns table
as
return
    select
        a.*,
        b.nume as acc_start_nume,
        c.nume as acc_end_nume
    from oxal1.tbl_int_acc_interval as a

    left join oxal1.tbl_int_cont as b
    on a.acc_start = b.cod

    left join oxal1.tbl_int_cont as c
    on a.acc_end = c.cod

    where a.id = @id;
create or alter procedure oxal1.prc_acc_interval_get_all
as
    select
        a.*,
        b.nume as acc_start_nume,
        c.nume as acc_end_nume
    from oxal1.tbl_int_acc_interval as a

    left join oxal1.tbl_int_cont as b
    on a.acc_start = b.cod

    left join oxal1.tbl_int_cont as c
    on a.acc_end = c.cod;
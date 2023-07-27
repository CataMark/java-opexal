create or alter procedure oxal1.prc_acc_interval_get_just_intervals
as
    select a.acc_start as [start], a.acc_end as [end]
    from oxal1.tbl_int_acc_interval as a
    order by a.acc_start asc;
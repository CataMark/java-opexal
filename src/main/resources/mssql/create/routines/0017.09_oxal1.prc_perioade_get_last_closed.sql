create or alter procedure oxal1.prc_perioade_get_last_closed
as
    select top 1
        *
    from oxal1.tbl_int_perioade as a
    where a.inchis = 1
    order by a.an desc, a.luna desc;
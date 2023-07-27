create or alter procedure oxal1.prc_perioade_get_opened
as
    select
        *
    from oxal1.tbl_int_perioade as a
    where a.inchis = 0;
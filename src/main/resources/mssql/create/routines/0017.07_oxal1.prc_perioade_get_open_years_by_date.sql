create or alter procedure oxal1.prc_perioade_get_open_years_by_date
    @laData date
as
    select a.an from
       (select distinct m.an from oxal1.tbl_int_perioade as m
        where m.inchis = 0 and datefromparts(m.an, case when m.luna > 12 then 12 else m.luna end, 1) <= @laData
        union
        select year(current_timestamp) as an) as a
    order by a.an asc;
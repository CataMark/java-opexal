create or alter procedure oxal1.prc_clasificator_get_last
    @coarea char(4)
as
    begin
        select top 1 *
        from oxal1.tbl_int_clasificator_log as a
        where a.coarea = @coarea
        order by datefromparts(a.an, iif(a.luna > 12, 12, a.luna), 1) desc, a.mod_timp desc;
    end;
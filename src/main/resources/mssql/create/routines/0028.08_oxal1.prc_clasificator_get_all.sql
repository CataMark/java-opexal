create or alter procedure oxal1.prc_clasificator_get_all
    @coarea char(4)
as
    begin
        select * from oxal1.tbl_int_clasificator_log as a
        where a.coarea = @coarea
        order by (cast(a.an as char(4)) + case when a.luna < 10 then '.0' else '.' end + cast(a.luna as varchar(2))) desc, a.mod_timp desc;
    end;
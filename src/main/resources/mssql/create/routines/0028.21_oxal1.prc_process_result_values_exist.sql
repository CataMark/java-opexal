create or alter procedure oxal1.prc_process_result_values_exist
    @an smallint,
    @luna tinyint,
    @coarea char(4)
as
    if exists (select * from oxal1.tbl_int_process_result as a
                where a.gjahr = @an and a.perio = @luna and a.kokrs = @coarea)
        select cast(1 as bit) as rezultat;
    else
        select cast(0 as bit) as rezultat;
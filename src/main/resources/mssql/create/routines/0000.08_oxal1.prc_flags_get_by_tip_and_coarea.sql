create or alter procedure oxal1.prc_flags_get_by_tip_and_coarea
    @tip varchar(30),
    @coarea char(4)
as
    select * from oxal1.tbl_int_flags
    where tip = @tip and coarea = @coarea;
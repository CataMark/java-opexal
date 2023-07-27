create or alter procedure oxal1.prc_flags_get_by_tip
    @tip varchar(30)
as
    select * from oxal1.tbl_int_flags
    where tip = @tip;
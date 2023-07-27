create or alter procedure oxal1.prc_flags_get_by_tip_and_kid
    @tip varchar(30),
    @kid varchar(20)
as
    select * from oxal1.tbl_int_flags
    where tip = @tip and mod_de = @kid;
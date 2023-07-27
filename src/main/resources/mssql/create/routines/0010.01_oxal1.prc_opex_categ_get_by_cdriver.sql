create or alter procedure oxal1.prc_opex_categ_get_by_cdriver
    @cdriver char(5)
as
    select * from oxal1.vw_opex_categ where cost_driver = @cdriver;
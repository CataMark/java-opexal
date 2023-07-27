create or alter procedure oxal1.prc_cocode_get_by_coarea
    @coarea char(4)
as
    select
        a.cod,
        a.nume,
        a.mod_de,
        a.mod_timp,
        b.coarea
    from oxal1.tbl_int_cocode as a
    outer apply (select
                    cast((select top 1 m.* from oxal1.tbl_int_coarea as m
                        where a.coarea = m.cod for json path, without_array_wrapper)
                    as nvarchar(max)) as coarea) as b
    where a.coarea = @coarea;
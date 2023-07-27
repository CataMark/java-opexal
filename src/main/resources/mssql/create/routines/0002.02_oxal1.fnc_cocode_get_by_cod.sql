create or alter function oxal1.fnc_cocode_get_by_cod(
    @cod char(4)
)
returns table
as
return
    select
        a.cod,
        a.nume,
        a.mod_de,
        a.mod_timp,
        b.coarea
    from oxal1.tbl_int_cocode as a
    outer apply (select
                    cast((select top 1 m.cod, m.nume from oxal1.tbl_int_coarea as m
                        where a.coarea = m.cod for json path, without_array_wrapper)
                    as nvarchar(max)) as coarea) as b
    where a.cod = @cod;
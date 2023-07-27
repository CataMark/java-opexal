create or alter function oxal1.fnc_cost_center_get_by_cod(
    @coarea char(4),
    @cod char(10)
)
returns table
as
return
    select
        a.*,
        b.nume as cocode_nume,
        c.segment
    from oxal1.tbl_int_cost_center as a

    inner join oxal1.tbl_int_cocode as b
    on a.coarea = b.coarea and a.cocode = b.cod

    left join oxal1.tbl_int_profit_center as c
    on a.profit_center = c.cod

    where a.coarea = @coarea and a.cod = @cod;
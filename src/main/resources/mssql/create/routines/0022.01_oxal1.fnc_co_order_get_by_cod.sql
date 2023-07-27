create or alter function oxal1.fnc_co_order_get_by_cod(
    @cod char(9)
)
returns table
as
return
    select
        a.*,
        b.nume as cocode_nume,
        c.segment,
        d.nume as cost_center_resp_nume
    from oxal1.tbl_int_co_order as a

    inner join oxal1.tbl_int_cocode as b
    on a.coarea = b.coarea and a.cocode = b.cod

    left join oxal1.tbl_int_profit_center as c
    on a.profit_center = c.cod

    left join oxal1.tbl_int_cost_center as d
    on a.coarea = d.coarea and a.cost_center_resp = d.cod

    where a.cod = @cod;
create or alter function oxal1.fnc_co_order_settle_rule_last_period(
    @coarea char(4),
    @forAn smallint,
    @forLuna tinyint,
    @optComanda varchar(10)
)
returns @rezultat table (comanda char(9), an smallint, luna tinyint)
as
    begin
        if @optComanda is null return;
        if left(@optComanda, 1) != '8' return;
        if not (@forAn between 2000 and 9999) return;
        if not (@forLuna between 1 and 16) return;

        insert into @rezultat (comanda, an, luna)
        select distinct
            a.comanda,
            first_value(a.an) over (partition by a.comanda order by datefromparts(a.an, a.luna, 1) desc
                                    rows between unbounded preceding and unbounded following) as an,
            first_value(a.luna) over (partition by a.comanda order by datefromparts(a.an, a.luna, 1) desc
                                    rows between unbounded preceding and unbounded following) as luna
        from oxal1.tbl_int_co_order_settle_rule as a
        where a.coarea = @coarea and datefromparts(a.an, a.luna, 1) <= datefromparts(@forAn, @forLuna, 1) and a.comanda = @optComanda;

        return;
    end;
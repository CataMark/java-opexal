create or alter procedure oxal1.prc_fagll03_collect_master_data
    @load_uuid uniqueidentifier,
    @coarea char(4),
    @startTime datetime,
    @kid varchar(20)
as
    begin
        set nocount on;
        begin try
            if not exists (select * from oxal1.tbl_int_fagll03 as a
                        where a.kokrs = @coarea and a.load_uuid = @load_uuid)
                return;

            /* prepare fagll03 temp table*/
            select * into #fagll03
            from oxal1.tbl_int_fagll03 as a
            where a.kokrs = @coarea and a.load_uuid = @load_uuid and
                (case when @startTime is null then 1 else iif(a.mod_timp >= @startTime, 1, 0) end) = 1;

            /* collect period status */
            merge into oxal1.tbl_int_perioade as tinta
            using (select distinct a.gjahr, a.monat
                    from #fagll03 as a) as sursa
            on (tinta.an = sursa.gjahr and tinta.luna = sursa.monat)
            when matched and tinta.inchis = 1 then
                update set
                    tinta.inchis = 0,
                    tinta.mod_de = @kid,
                    tinta.mod_timp = current_timestamp
            when not matched by target then
                insert (an, luna, inchis, mod_de, mod_timp)
                values (sursa.gjahr, sursa.monat, 0, @kid, current_timestamp);

            /* collect profit center master data */
            insert into oxal1.tbl_int_profit_center (cod, mod_de, mod_timp)
            select distinct a.prctr, @kid, current_timestamp
            from #fagll03 as a
            left join oxal1.tbl_int_profit_center as b
            on a.prctr = b.cod
            where b.cod is null;

            /* collect cost center master data*/
            merge into oxal1.tbl_int_cost_center as tinta
            using (select distinct a.kostl, a.kokrs, a.bukrs, a.prctr
                    from #fagll03 as a
                    where a.kostl is not null) as sursa
            on (tinta.coarea = sursa.kokrs and tinta.cod = sursa.kostl)
            when matched and tinta.profit_center is null then
                update set
                    tinta.profit_center = sursa.prctr,
                    tinta.mod_de = @kid,
                    tinta.mod_timp = current_timestamp
            when not matched by target then
                insert (cod, coarea, cocode, profit_center, mod_de, mod_timp)
                values (sursa.kostl, sursa.kokrs, sursa.bukrs, sursa.prctr, @kid, current_timestamp);

            /* collect co order master data */
            merge into oxal1.tbl_int_co_order as tinta
            using (select distinct a.aufnr, a.kokrs, a.bukrs, a.prctr
                    from #fagll03 as a
                    where a.aufnr like '8%') as sursa
            on (tinta.coarea = sursa.kokrs and tinta.cod = sursa.aufnr)
            when matched and tinta.profit_center is null then
                update set
                    tinta.profit_center = sursa.prctr,
                    tinta.mod_de = @kid,
                    tinta.mod_timp = current_timestamp
            when not matched by target then
                insert (cod, coarea, cocode, profit_center, mod_de, mod_timp)
                values (sursa.aufnr, sursa.kokrs, sursa.bukrs, sursa.prctr, @kid, current_timestamp);

            /* collect pm order master data */
            merge into oxal1.tbl_int_pm_order as tinta
            using (select distinct a.aufnr, a.kokrs, a.bukrs, a.prctr
                    from #fagll03 as a
                    where a.aufnr like '4%') as sursa
            on (tinta.coarea = sursa.kokrs and tinta.cod = sursa.aufnr)
            when matched and tinta.profit_center is null then
                update set
                    tinta.profit_center = sursa.prctr,
                    tinta.mod_de = @kid,
                    tinta.mod_timp = current_timestamp
            when not matched by target then
                insert (cod, coarea, cocode, profit_center, mod_de, mod_timp)
                values (sursa.aufnr, sursa.kokrs, sursa.bukrs, sursa.prctr, @kid, current_timestamp);

            /* collect wbs element master data */
            merge into oxal1.tbl_int_wbs as tinta
            using (select distinct a.projk, a.kokrs, a.bukrs, a.prctr
                    from #fagll03 as a
                    where a.projk is not null) as sursa
            on (tinta.coarea = sursa.kokrs and tinta.cod = sursa.projk)
            when matched and tinta.profit_center is null then
                update set
                    tinta.profit_center = sursa.prctr,
                    tinta.mod_de = @kid,
                    tinta.mod_timp = current_timestamp
            when not matched by target then
                insert (cod, coarea, cocode, profit_center, mod_de, mod_timp)
                values (sursa.projk, sursa.kokrs, sursa.bukrs, sursa.prctr, @kid, current_timestamp);

            /* collect account master data */
            merge into oxal1.tbl_int_cont as tinta
            using (select distinct
                        a.konto,
                        first_value(a.u_lokkt) over (partition by a.konto order by a.kokrs asc
                                                    rows between unbounded preceding and unbounded following) as u_lokkt
                    from #fagll03 as a) as sursa
            on (tinta.cod = sursa.konto)
            when matched and tinta.alternativ is null and sursa.u_lokkt is not null then
                update set
                    tinta.alternativ = sursa.u_lokkt,
                    tinta.mod_de = @kid,
                    tinta.mod_timp = current_timestamp
            when not matched by target and sursa.u_lokkt is not null then
                insert (cod, alternativ, mod_de, mod_timp) 
                values (sursa.konto, sursa.u_lokkt, @kid, current_timestamp);

            /* cleaning */
            drop table #fagll03;
        end try
        begin catch
            insert into oxal1.tbl_int_log (sursa, tip, mesaj, mod_de, mod_timp)
            values ('oxal1.prc_fagll03_collect_master_data', 'ERROR', error_message(), @kid, current_timestamp);
        end catch
    end;
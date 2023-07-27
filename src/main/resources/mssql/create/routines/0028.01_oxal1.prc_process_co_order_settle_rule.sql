create or alter procedure oxal1.prc_process_co_order_settle_rule
    @optUuidArray nvarchar(max),
    @optAn smallint,
    @optLuna tinyint,
    @coarea char(4),
    @kid varchar(20)
as
    begin
        set nocount on;
        exec sys.sp_set_session_context @key = N'oxal1_user_id', @value = @kid, @readonly = 0;

        declare @app_oper char(4) = 'DECO';
        declare @sap_tranz char(4) = 'KOB1';
        declare @part_obj_type char(3) = 'CTR';
        declare @load_uuids table (
            uuid uniqueidentifier,
            an smallint,
            luna tinyint,
            coarea char(4)
        );

        begin try
            /* colectare operatiuni decontare */
            select a.sap_oper, a.sap_tranz, a.doc_tip, a.syst_logic
            into #deco_process_matrix
            from oxal1.tbl_int_process_matrix as a
            where a.app_oper = @app_oper and a.blocat = 0;

            /* colectare uuid */
            if @optUuidArray is null
                insert into @load_uuids (an, luna, coarea)
                values (@optAn, @optLuna, @coarea);
            else
                insert into @load_uuids (uuid, coarea)
                select distinct b.uuid, b.coarea
                from (select cast([value] as uniqueidentifier) as uuid from openjson(@optUuidArray) where [value] is not null) as a
                inner join oxal1.tbl_int_flags as b
                on a.uuid = b.uuid
                where b.coarea = @coarea and b.sap_tranz = @sap_tranz;

            /* verificare existenta valori decontate */
            if not exists (select * from oxal1.tbl_int_kob1 as a
                        inner join @load_uuids as b
                        on (case when b.uuid is null then iif(a.gjahr = b.an and a.perio = b.luna, 1, 0)
                            else iif(a.load_uuid = b.uuid, 1, 0) end) = 1 and a.kokrs = b.coarea
                        inner join #deco_process_matrix as c
                        on a.vrgng = c.sap_oper and
                            iif(c.sap_tranz is null, 1, iif(a.orgvg = c.sap_tranz, 1, 0)) = 1 and
                            iif(c.doc_tip is null, 1, iif(a.blart = c.doc_tip, 1, 0)) = 1 and
                            iif(c.syst_logic is null, 1, iif(a.logsystem = c.syst_logic, 1, 0)) = 1)
                return;
            
            /* calcul procente */
            with p as (
                select distinct q.kokrs, q.gjahr, q.perio
                from oxal1.tbl_int_kob1 as q
                inner join @load_uuids as r
                on (case when r.uuid is null then iif(q.gjahr = r.an and q.perio = r.luna, 1, 0)
                    else iif(q.load_uuid = r.uuid, 1, 0) end) = 1 and q.kokrs = r.coarea
            ),
            s as (
                select m.kokrs, m.bukrs, m.gjahr, m.perio, m.aufnr, m.pobid, m.wrgbtr
                from oxal1.tbl_int_kob1 as m
                inner join p
                on m.kokrs = p.kokrs and m.gjahr = p.gjahr and m.perio = p.perio
                inner join #deco_process_matrix as n
                on m.vrgng = n.sap_oper and
                    iif(n.sap_tranz is null, 1, iif(m.orgvg = n.sap_tranz, 1, 0)) = 1 and
                    iif(n.doc_tip is null, 1, iif(m.blart = n.doc_tip, 1, 0)) = 1 and
                    iif(n.syst_logic is null, 1, iif(m.logsystem = n.syst_logic, 1, 0)) = 1
                where m.pobart = @part_obj_type and m.pobid is not null
            )
            select
                a.kokrs, a.bukrs, a.gjahr, a.perio, a.aufnr, a.pobid, round(a.valoare / b.suma, 4) as procent
            into #deco_procente
            from
                (select s.kokrs, s.bukrs, s.gjahr, s.perio, s.aufnr, s.pobid, sum(s.wrgbtr) as valoare
                from s
                group by s.kokrs, s.bukrs, s.gjahr, s.perio, s.aufnr, s.pobid) as a
            inner join
                (select s.kokrs, s.bukrs, s.gjahr, s.perio, s.aufnr, sum(s.wrgbtr) as suma
                from s
                group by s.kokrs, s.bukrs, s.gjahr, s.perio, s.aufnr) as b
            on a.kokrs = b.kokrs and a.bukrs = b.bukrs and a.gjahr = b.gjahr and a.perio = b.perio and a.aufnr = b.aufnr
            where b.suma != 0;

            /* actualizeaza tabela reguli de decontare */
            begin transaction
                merge into oxal1.tbl_int_co_order_settle_rule as t
                using (select * from #deco_procente where procent != 0) as s
                on (t.coarea = s.kokrs and t.cocode = s.bukrs and t.an = s.gjahr and t.luna = s.perio and
                    t.comanda = s.aufnr and t.cost_center = s.pobid)
                when matched then
                    update set
                        t.procent = s.procent,
                        t.mod_de = @kid,
                        t.mod_timp = current_timestamp
                when not matched by target then
                    insert (coarea, cocode, an, luna, comanda, cost_center, procent, mod_de, mod_timp)
                    values (s.kokrs, s.bukrs, s.gjahr, s.perio, s.aufnr, s.pobid, s.procent, @kid, current_timestamp);
            commit transaction

            /* curatare tabele temporare */
            drop table #deco_procente;
            drop table #deco_process_matrix;
        end try
        begin catch
            if @@trancount > 0  rollback transaction;
            throw;
        end catch
    end;
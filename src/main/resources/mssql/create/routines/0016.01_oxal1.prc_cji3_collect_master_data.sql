create or alter procedure oxal1.prc_cji3_collect_master_data
    @load_uuid uniqueidentifier,
    @coarea char(4),
    @startTime datetime,
    @kid varchar(20)
as
    begin
        set nocount on;
        begin try
            if not exists (select * from oxal1.tbl_int_cji3 as a
                        where a.kokrs = @coarea and a.load_uuid = @load_uuid)
                return;

            /* prepare cji3 temp table */
            select * into #cji3
            from oxal1.tbl_int_cji3 as a
            where a.kokrs = @coarea and a.load_uuid = @load_uuid and
                (case when @startTime is null then 1 else iif(a.mod_timp >= @startTime, 1, 0) end) = 1;

            /* collect period status */
            merge into oxal1.tbl_int_perioade as tinta
            using (select distinct a.gjahr, a.perio
                    from #cji3 as a) as sursa
            on (tinta.an = sursa.gjahr and tinta.luna = sursa.perio)
            when matched and tinta.inchis = 1 then
                update set
                    tinta.inchis = 0,
                    tinta.mod_de = @kid,
                    tinta.mod_timp = current_timestamp
            when not matched by target then
                insert (an, luna, inchis, mod_de, mod_timp)
                values (sursa.gjahr, sursa.perio, 0, @kid, current_timestamp);

            /* collect wbs element master data */
            merge into oxal1.tbl_int_wbs as tinta
            using (select distinct a.pspid, a.posid, a.obj_txt, a.kokrs, a.bukrs
                    from #cji3 as a
                    where a.pspid is not null or a.obj_txt is not null) as sursa
            on (tinta.coarea = sursa.kokrs and tinta.cod = sursa.posid)
            when matched then
                update set
                    tinta.definitie = sursa.pspid,
                    tinta.nume = sursa.obj_txt,
                    tinta.mod_de = @kid,
                    tinta.mod_timp = current_timestamp
            when not matched by target then
                insert (cod, definitie, coarea, cocode, nume, mod_de, mod_timp)
                values (sursa.posid, sursa.pspid, sursa.kokrs, sursa.bukrs, sursa.obj_txt, @kid, current_timestamp);

            /* collect account master data */
            merge into oxal1.tbl_int_cont as tinta
            using (select distinct a.kstar, a.cel_ktxt
                    from #cji3 as a
                    where a.cel_ktxt is not null) as sursa
            on (tinta.cod = sursa.kstar)
            when matched then
                update set
                    tinta.nume = sursa.cel_ktxt,
                    tinta.mod_de = @kid,
                    tinta.mod_timp = current_timestamp
            when not matched by target then
                insert (cod, nume, mod_de, mod_timp)
                values (sursa.kstar, sursa.cel_ktxt, @kid, current_timestamp);
            
            /* collect material master data */
            merge into oxal1.tbl_int_material as tinta
            using (select distinct a.matnr, a.mat_txt
                    from #cji3 as a
                    where a.matnr is not null and a.mat_txt is not null) as sursa
            on (tinta.cod = sursa.matnr)
            when matched then
                update set
                    tinta.nume = sursa.mat_txt,
                    tinta.mod_de = @kid,
                    tinta.mod_timp = current_timestamp
            when not matched by target then
                insert (cod, nume, mod_de, mod_timp)
                values (sursa.matnr, sursa.mat_txt, @kid, current_timestamp);

            /* collect vendor master data */
            declare @vendor_cod_start_regex varchar(20) = '%[1-9]%';
            declare @vendor_nume_start_regex varchar(20) = '% %';

            merge into oxal1.tbl_int_vendor as tinta
            using (select distinct
                        substring(a.zz_lif, b.cod_start, c.nume_start - b.cod_start) as cod,
                        substring(a.zz_lif, c.nume_start + 1, len(a.zz_lif) - c.nume_start) as nume
                    from #cji3 as a
                    cross apply (select cod_start = patindex(@vendor_cod_start_regex, a.zz_lif)) as b
                    cross apply (select nume_start = patindex(@vendor_nume_start_regex, a.zz_lif)) as c
                    where a.zz_lif is not null and b.cod_start >= 0 and c.nume_start > b.cod_start) as sursa
            on (tinta.cod = sursa.cod)
            when matched then
                update set
                    tinta.nume = sursa.nume,
                    tinta.mod_de = @kid,
                    tinta.mod_timp = current_timestamp
            when not matched by target then
                insert (cod, nume, mod_de, mod_timp)
                values (sursa.cod, sursa.nume, @kid, current_timestamp);
            
            /* cleaning */
            drop table #cji3;
        end try
        begin catch
            insert into oxal1.tbl_int_log (sursa, tip, mesaj, mod_de, mod_timp)
            values ('oxal1.prc_cji3_collect_master_data', 'ERROR', error_message(), @kid, current_timestamp);
        end catch
    end;
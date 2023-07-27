create or alter procedure oxal1.prc_process_reconcile_co_fi
    @optUuidArray nvarchar(max),
    @optAn smallint,
    @optLuna tinyint,
    @coarea char(4),
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @vendor_cod_start_regex varchar(20) = '%[1-9]%';
        declare @vendor_nume_start_regex varchar(20) = '% %';
        declare @load_uuids table (
            uuid uniqueidentifier,
            an smallint,
            luna tinyint,
            coarea char(4),
            sap_tranz varchar(10)
        );

        begin try
            begin transaction
                /* colectare uuid */
                if @optUuidArray is null
                    insert into @load_uuids (an, luna, coarea, sap_tranz)
                    select @optAn as an, @optLuna as luna, @coarea, a.cod as sap_tranz
                    from oxal1.tbl_int_sap_tranz as a; 
                else
                    insert into @load_uuids (uuid, coarea, sap_tranz)
                    select distinct b.uuid, b.coarea, b.sap_tranz
                    from (select cast([value] as uniqueidentifier) as uuid from openjson(@optUuidArray) where [value] is not null) as a
                    inner join oxal1.tbl_int_flags as b
                    on a.uuid = b.uuid
                    where b.coarea = @coarea;                  

                /* procesare inregistrari comune */
                /* ********************************* */
                declare @comm_app_oper char(4) = 'COMM';
                declare @comm_ledger_group char(2) = 'G1';

                /* colectare operatiuni comune */
                select a.sap_oper, a.sap_tranz, a.doc_tip, a.syst_logic
                into #comm_process_matrix
                from oxal1.tbl_int_process_matrix as a
                where a.app_oper = @comm_app_oper and a.blocat = 0;

                /* colectare inregistrari comune */
                insert into oxal1.tbl_int_process_result(source_id, sap_tranz, u_ldgrp, kokrs, bukrs, gjahr, perio, obart, kostl, aufnr, posid, vbund, pobart, pobid, pob_txt, objnr_n1, vrgng, orgvg, u_tcode, logsystem, blart, beknz,
                                                        kstar, sgtxt, bltxt, belnr, buzei, zzco_belnr, refbz_fi, werks, ebeln, ebelp, ebtxt, matnr, gbeextwg_ebx, refbn, stokz, awref_rev, gkoar, gkont, gkont_ltxt, u_lifnr,
                                                        wrgbtr, kwaer, wtgbtr, twaer, mbgbtr, meinb, usnam, wsdat, budat, bldat, cpudt, load_file, load_uuid, mod_de, mod_timp)
                select distinct a.id, 'KSB1', @comm_ledger_group, a.kokrs, a.bukrs, a.gjahr, a.perio, a.obart, a.kostl, null, null, a.vbund, a.pobart, a.pobid, a.pob_txt, a.objnr_n1, a.vrgng, a.orgvg, null, a.logsystem, a.blart, a.beknz,
                        a.kstar, a.sgtxt, a.bltxt, a.belnr, a.buzei, a.zzco_belnr, a.refbz_fi, a.werks, a.ebeln, a.ebelp, a.ebtxt, a.matnr, a.gbeextwg_ebx, a.refbn, a.stokz, a.awref_rev, a.gkoar, a.gkont, a.gkont_ltxt,
                        (case when d.vendor_cod_start < 0 then null else substring(a.zz_lif, d.vendor_cod_start, d.vendor_nume_start - d.vendor_cod_start) end) as u_lifnr,
                        a.wrgbtr, a.kwaer, a.wtgbtr, a.twaer, a.mbgbtr, a.meinb, a.usnam, a.wsdat, a.budat, a.bldat, a.cpudt, a.load_file, a.load_uuid, @kid, current_timestamp
                from oxal1.tbl_int_ksb1 as a

                inner join @load_uuids as b
                on (case when b.uuid is null then iif(a.gjahr = b.an and a.perio = b.luna, 1, 0)
                    else iif(a.load_uuid = b.uuid, 1, 0) end) = 1 and a.kokrs = b.coarea

                inner join #comm_process_matrix as c
                on a.vrgng = c.sap_oper and
                    iif(c.sap_tranz is null, 1, iif(a.orgvg = c.sap_tranz, 1, 0)) = 1 and
                    iif(c.doc_tip is null, 1, iif(a.blart = c.doc_tip, 1, 0)) = 1 and
                    iif(c.syst_logic is null, 1, iif(a.logsystem = c.syst_logic, 1, 0)) = 1

                cross apply (select (case when a.zz_lif is null then -1 else patindex(@vendor_cod_start_regex, a.zz_lif) end) as vendor_cod_start,
                                    (case when a.zz_lif is null then -1 else patindex(@vendor_nume_start_regex, a.zz_lif) end) as vendor_nume_start) as d

                inner join (select * from oxal1.tbl_int_acc_interval as m where m.process = 1)  as f
                on a.kstar between f.acc_start and f.acc_end

                where b.sap_tranz = 'KSB1'

                union all

                select a.id, 'KOB1', @comm_ledger_group, a.kokrs, a.bukrs, a.gjahr, a.perio, a.obart, null, a.aufnr, null, a.vbund, a.pobart, a.pobid, a.pob_txt, a.objnr_n1, a.vrgng, a.orgvg, null, a.logsystem, a.blart, a.beknz,
                        a.kstar, a.sgtxt, a.bltxt, a.belnr, a.buzei, a.zzco_belnr, a.refbz_fi, a.werks, a.ebeln, a.ebelp, a.ebtxt, a.matnr, a.gbeextwg_ebx, a.refbn, a.stokz, a.awref_rev, a.gkoar, a.gkont, a.gkont_ktxt,
                        (case when d.vendor_cod_start < 0 then null else substring(a.zz_lif, d.vendor_cod_start, d.vendor_nume_start - d.vendor_cod_start) end) as u_lifnr,
                        a.wrgbtr, a.kwaer, a.wtgbtr, a.twaer, a.mbgbtr, a.meinb, a.usnam, a.wsdat, a.budat, a.bldat, a.cpudt, a.load_file, a.load_uuid, @kid, current_timestamp
                from oxal1.tbl_int_kob1 as a

                inner join @load_uuids as b
                on (case when b.uuid is null then iif(a.gjahr = b.an and a.perio = b.luna, 1, 0)
                    else iif(a.load_uuid = b.uuid, 1, 0) end) = 1 and a.kokrs = b.coarea

                inner join #comm_process_matrix as c
                on a.vrgng = c.sap_oper and
                    iif(c.sap_tranz is null, 1, iif(a.orgvg = c.sap_tranz, 1, 0)) = 1 and
                    iif(c.doc_tip is null, 1, iif(a.blart = c.doc_tip, 1, 0)) = 1 and
                    iif(c.syst_logic is null, 1, iif(a.logsystem = c.syst_logic, 1, 0)) = 1

                cross apply (select (case when a.zz_lif is null then -1 else patindex(@vendor_cod_start_regex, a.zz_lif) end) as vendor_cod_start,
                                    (case when a.zz_lif is null then -1 else patindex(@vendor_nume_start_regex, a.zz_lif) end) as vendor_nume_start) as d

                inner join (select * from oxal1.tbl_int_acc_interval as m where m.process = 1)  as f
                on a.kstar between f.acc_start and f.acc_end

                where a.objnr_n1 is null and b.sap_tranz = 'KOB1'

                union all

                select a.id, 'CJI3', @comm_ledger_group, a.kokrs, a.bukrs, a.gjahr, a.perio, null, null, null, a.posid, a.vbund, a.pobart, a.pobid, a.pob_txt, a.objnr_n1, a.vrgng, a.orgvg, null, a.logsystem, a.blart, a.beknz,
                        a.kstar, a.sgtxt, a.bltxt, a.belnr, a.buzei, a.zzco_belnr, a.refbz_fi, a.werks, a.ebeln, a.ebelp, a.ebtxt, a.matnr, a.gbeextwg_ebx, a.refbn, a.stokz, a.awref_rev, a.gkoar, a.gkont, a.gkont_ktxt,
                        (case when d.vendor_cod_start < 0 then null else substring(a.zz_lif, d.vendor_cod_start, d.vendor_nume_start - d.vendor_cod_start) end) as u_lifnr,
                        a.wrvbtr, a.kwaer, a.wtgbtr, a.twaer, a.mbgbtr, a.meinb, a.usnam, a.wsdat, a.budat, a.bldat, a.cpudt, a.load_file, a.load_uuid, @kid, current_timestamp

                from oxal1.tbl_int_cji3 as a

                inner join @load_uuids as b
                on (case when b.uuid is null then iif(a.gjahr = b.an and a.perio = b.luna, 1, 0)
                    else iif(a.load_uuid = b.uuid, 1, 0) end) = 1 and a.kokrs = b.coarea

                inner join #comm_process_matrix as c
                on a.vrgng = c.sap_oper and
                    iif(c.sap_tranz is null, 1, iif(a.orgvg = c.sap_tranz, 1, 0)) = 1 and
                    iif(c.doc_tip is null, 1, iif(a.blart = c.doc_tip, 1, 0)) = 1 and
                    iif(c.syst_logic is null, 1, iif(a.logsystem = c.syst_logic, 1, 0)) = 1

                cross apply (select (case when a.zz_lif is null then -1 else patindex(@vendor_cod_start_regex, a.zz_lif) end) as vendor_cod_start,
                                    (case when a.zz_lif is null then -1 else patindex(@vendor_nume_start_regex, a.zz_lif) end) as vendor_nume_start) as d

                inner join (select * from oxal1.tbl_int_acc_interval as m where m.process = 1)  as f
                on a.kstar between f.acc_start and f.acc_end
                                    
                where b.sap_tranz = 'CJI3';

                /* curatare */
                drop table #comm_process_matrix;

                /* procesare inregistrari de reconciliat */
                /* ********************************* */
                declare @reco_app_oper char(4) = 'RECO';
                declare @ifrs_ledger_group char(2) = '0L';
                declare @ras_ledger_group char(2) = 'L1';

                /* colectare operatiuni reconciliere */
                select a.sap_oper, a.sap_tranz, a.doc_tip, a.syst_logic
                into #reco_process_matrix
                from oxal1.tbl_int_process_matrix as a
                where a.app_oper = @reco_app_oper and a.blocat = 0;

                /* verifca existenta transactiilor sap aplicabile in FAGLL03 */
                if exists (select * from #reco_process_matrix
                            where sap_tranz is null)
                    raiserror('NO SAP TRANSACTION FOR FI-CO RECONCILIATION!', 16, 1);

                if exists (select * from #reco_process_matrix
                        where syst_logic is not null)
                    raiserror('FI-CO RECONCILIATION SHOULD NOT BE BASED ON logsystem!', 16, 1);
                    
                /* colectare inregistrari reconciliate KSB1*/
                with f as (
                    select f1.* from oxal1.tbl_int_fagll03 as f1

                    inner join @load_uuids as f2
                    on (case when f2.uuid is null then iif(f1.gjahr = f2.an and f1.monat = f2.luna, 1, 0)
                        else iif(f1.load_uuid = f2.uuid, 1, 0) end) = 1 and f1.kokrs = f2.coarea

                    inner join #reco_process_matrix as f3
                    on f1.glvor = f3.sap_tranz and
                        iif(f3.doc_tip is null, 1, iif(f1.blart = f3.doc_tip, 1, 0)) = 1

                    where f1.kostl is not null and f2.sap_tranz = 'FAGLL03'
                ),
                k as (
                    select k1.* from oxal1.tbl_int_ksb1 as k1
                    
                    inner join @load_uuids as k2
                    on (case when k2.uuid is null then iif(k1.gjahr = k2.an and k1.perio = k2.luna, 1, 0)
                        else iif(k1.load_uuid = k2.uuid, 1, 0) end) = 1 and k1.kokrs = k2.coarea

                    inner join #reco_process_matrix as k3
                    on k1.vrgng = k3.sap_oper and k1.orgvg = k3.sap_tranz and
                        iif(k3.doc_tip is null, 1, iif(k1.blart = k3.doc_tip, 1, 0)) = 1

                    inner join (select * from oxal1.tbl_int_acc_interval as m where m.process = 1)  as k4
                    on k1.kstar between k4.acc_start and k4.acc_end

                    where k2.sap_tranz = 'KSB1'
                )
                insert into oxal1.tbl_int_process_result(source_id, sap_tranz, u_ldgrp, kokrs, bukrs, gjahr, perio, obart, kostl, aufnr, posid, vbund, pobart, pobid, pob_txt, objnr_n1, vrgng, orgvg, u_tcode, logsystem, blart, beknz,
                                                        kstar, sgtxt, bltxt, belnr, buzei, zzco_belnr, refbz_fi, werks, ebeln, ebelp, ebtxt, matnr, gbeextwg_ebx, refbn, stokz, awref_rev, gkoar, gkont, gkont_ltxt, u_lifnr,
                                                        wrgbtr, kwaer, wtgbtr, twaer, mbgbtr, meinb, usnam, wsdat, budat, bldat, cpudt, load_file, load_uuid, mod_de, mod_timp)
                select k.id, 'KSB1', iif(f.belnr is null, @ifrs_ledger_group, @comm_ledger_group), k.kokrs, k.bukrs, k.gjahr, k.perio, k.obart, k.kostl, null, null, k.vbund, k.pobart, k.pobid, k.pob_txt, k.objnr_n1, k.vrgng, k.orgvg, null, k.logsystem, k.blart, k.beknz,
                        k.kstar, k.sgtxt, k.bltxt, k.belnr, k.buzei, k.zzco_belnr, k.refbz_fi, k.werks, k.ebeln, k.ebelp, k.ebtxt, k.matnr, k.gbeextwg_ebx, k.refbn, k.stokz, k.awref_rev, k.gkoar, k.gkont, k.gkont_ltxt,
                        (case when b.vendor_cod_start < 0 then null else substring(k.zz_lif, b.vendor_cod_start, b.vendor_nume_start - b.vendor_cod_start) end) as u_lifnr,
                        k.wrgbtr, k.kwaer, k.wtgbtr, k.twaer, k.mbgbtr, k.meinb, k.usnam, k.wsdat, k.budat, k.bldat, k.cpudt, k.load_file, k.load_uuid, @kid, current_timestamp
                from k

                left join f
                on k.kokrs = f.kokrs and k.bukrs = f.bukrs and k.gjahr = f.gjahr and k.perio = f.monat and k.zzco_belnr = f.belnr and k.refbz_fi = f.buzei

                cross apply (select (case when k.zz_lif is null then -1 else patindex(@vendor_cod_start_regex, k.zz_lif) end) as vendor_cod_start,
                                    (case when k.zz_lif is null then -1 else patindex(@vendor_nume_start_regex, k.zz_lif) end) as vendor_nume_start) as b

                union all

                select f.id, 'FAGLL03', @ras_ledger_group, f.kokrs, f.bukrs, f.gjahr, f.monat, null, f.kostl, null, null, f.vbund, null, null, null, isnull(f.aufnr, f.projk), null, f.glvor, f.u_tcode, null, f.blart, f.shkzg,
                        f.konto, f.sgtxt, f.u_bktxt, null, null, f.belnr, f.buzei, f.werks, f.u_ebeln, f.ebelp, null, f.u_matnr, f.gbeextwg_ebx, null, null, null, f.gkart, f.gkont, null, f.u_lifnr,
                        f.dmshb, f.hwaer, f.wrshb, f.waers, f.menge, null, f.u_usnam, f.valut, f.budat, f.bldat, f.u_cpudt, f.load_file, f.load_uuid, @kid, current_timestamp
                from f

                left join k
                on k.kokrs = f.kokrs and k.bukrs = f.bukrs and k.gjahr = f.gjahr and k.perio = f.monat and k.zzco_belnr = f.belnr and k.refbz_fi = f.buzei

                where k.zzco_belnr is null;

                /* colectare inregistrari reconciliate KOB1*/
                with f as (
                    select f1.* from oxal1.tbl_int_fagll03 as f1

                    inner join @load_uuids as f2
                    on (case when f2.uuid is null then iif(f1.gjahr = f2.an and f1.monat = f2.luna, 1, 0)
                        else iif(f1.load_uuid = f2.uuid, 1, 0) end) = 1 and f1.kokrs = f2.coarea

                    inner join #reco_process_matrix as f3
                    on f1.glvor = f3.sap_tranz and
                        iif(f3.doc_tip is null, 1, iif(f1.blart = f3.doc_tip, 1, 0)) = 1

                    where f1.aufnr is not null and f1.kostl is null and f1.projk is null and f2.sap_tranz = 'FAGLL03'
                ),
                k as (
                    select k1.* from oxal1.tbl_int_kob1 as k1

                    inner join @load_uuids as k2
                    on (case when k2.uuid is null then iif(k1.gjahr = k2.an and k1.perio = k2.luna, 1, 0)
                        else iif(k1.load_uuid = k2.uuid, 1, 0) end) = 1 and k1.kokrs = k2.coarea

                    inner join #reco_process_matrix as k3
                    on k1.vrgng = k3.sap_oper and k1.orgvg = k3.sap_tranz and
                        iif(k3.doc_tip is null, 1, iif(k1.blart = k3.doc_tip, 1, 0)) = 1
                    
                    inner join (select * from oxal1.tbl_int_acc_interval as m where m.process = 1)  as k4
                    on k1.kstar between k4.acc_start and k4.acc_end

                    where k1.objnr_n1 is null and k2.sap_tranz = 'KOB1'
                )
                insert into oxal1.tbl_int_process_result(source_id, sap_tranz, u_ldgrp, kokrs, bukrs, gjahr, perio, obart, kostl, aufnr, posid, vbund, pobart, pobid, pob_txt, objnr_n1, vrgng, orgvg, u_tcode, logsystem, blart, beknz,
                                                        kstar, sgtxt, bltxt, belnr, buzei, zzco_belnr, refbz_fi, werks, ebeln, ebelp, ebtxt, matnr, gbeextwg_ebx, refbn, stokz, awref_rev, gkoar, gkont, gkont_ltxt, u_lifnr,
                                                        wrgbtr, kwaer, wtgbtr, twaer, mbgbtr, meinb, usnam, wsdat, budat, bldat, cpudt, load_file, load_uuid, mod_de, mod_timp)
                select k.id, 'KOB1', iif(f.belnr is null, @ifrs_ledger_group, @comm_ledger_group), k.kokrs, k.bukrs, k.gjahr, k.perio, k.obart, null, k.aufnr, null, k.vbund, k.pobart, k.pobid, k.pob_txt, k.objnr_n1, k.vrgng, k.orgvg, null, k.logsystem, k.blart, k.beknz,
                        k.kstar, k.sgtxt, k.bltxt, k.belnr, k.buzei, k.zzco_belnr, k.refbz_fi, k.werks, k.ebeln, k.ebelp, k.ebtxt, k.matnr, k.gbeextwg_ebx, k.refbn, k.stokz, k.awref_rev, k.gkoar, k.gkont, k.gkont_ktxt,
                        (case when b.vendor_cod_start < 0 then null else substring(k.zz_lif, b.vendor_cod_start, b.vendor_nume_start - b.vendor_cod_start) end) as u_lifnr,
                        k.wrgbtr, k.kwaer, k.wtgbtr, k.twaer, k.mbgbtr, k.meinb, k.usnam, k.wsdat, k.budat, k.bldat, k.cpudt, k.load_file, k.load_uuid, @kid, current_timestamp
                from k

                left join f
                on k.kokrs = f.kokrs and k.bukrs = f.bukrs and k.gjahr = f.gjahr and k.perio = f.monat and k.zzco_belnr = f.belnr and k.refbz_fi = f.buzei

                cross apply (select (case when k.zz_lif is null then -1 else patindex(@vendor_cod_start_regex, k.zz_lif) end) as vendor_cod_start,
                                    (case when k.zz_lif is null then -1 else patindex(@vendor_nume_start_regex, k.zz_lif) end) as vendor_nume_start) as b

                union all

                select f.id, 'FAGLL03', @ras_ledger_group, f.kokrs, f.bukrs, f.gjahr, f.monat, null, null, f.aufnr, null, f.vbund, null, null, null, null, null, f.glvor, f.u_tcode, null, f.blart, f.shkzg,
                        f.konto, f.sgtxt, f.u_bktxt, null, null, f.belnr, f.buzei, f.werks, f.u_ebeln, f.ebelp, null, f.u_matnr, f.gbeextwg_ebx, null, null, null, f.gkart, f.gkont, null, f.u_lifnr,
                        f.dmshb, f.hwaer, f.wrshb, f.waers, f.menge, null, f.u_usnam, f.valut, f.budat, f.bldat, f.u_cpudt, f.load_file, f.load_uuid, @kid, current_timestamp
                from f

                left join k
                on k.kokrs = f.kokrs and k.bukrs = f.bukrs and k.gjahr = f.gjahr and k.perio = f.monat and k.zzco_belnr = f.belnr and k.refbz_fi = f.buzei

                where k.zzco_belnr is null;

                /* colectare inregistrari reconciliate CJI3 */
                with f as (
                    select f1.* from oxal1.tbl_int_fagll03 as f1

                    inner join @load_uuids as f2
                    on (case when f2.uuid is null then iif(f1.gjahr = f2.an and f1.monat = f2.luna, 1, 0)
                        else iif(f1.load_uuid = f2.uuid, 1, 0) end) = 1 and f1.kokrs = f2.coarea

                    inner join #reco_process_matrix as f3
                    on f1.glvor = f3.sap_tranz and
                        iif(f3.doc_tip is null, 1, iif(f1.blart = f3.doc_tip, 1, 0)) = 1

                    where f1.projk is not null and f1.kostl is null and f2.sap_tranz = 'FAGLL03'
                ),
                k as (
                    select k1.* from oxal1.tbl_int_cji3 as k1

                    inner join @load_uuids as k2
                    on (case when k2.uuid is null then iif(k1.gjahr = k2.an and k1.perio = k2.luna, 1, 0)
                        else iif(k1.load_uuid = k2.uuid, 1, 0) end) = 1 and k1.kokrs = k2.coarea

                    inner join #reco_process_matrix as k3
                    on k1.vrgng = k3.sap_oper and k1.orgvg = k3.sap_tranz and
                        iif(k3.doc_tip is null, 1, iif(k1.blart = k3.doc_tip, 1, 0)) = 1

                    inner join (select * from oxal1.tbl_int_acc_interval as m where m.process = 1)  as k4
                    on k1.kstar between k4.acc_start and k4.acc_end

                    where iif(k1.objnr_n1 is null, 1, iif(left(k1.objnr_n1, 3) = 'CTR', 0, 1)) = 1 and k2.sap_tranz = 'CJI3'
                )
                insert into oxal1.tbl_int_process_result(source_id, sap_tranz, u_ldgrp, kokrs, bukrs, gjahr, perio, obart, kostl, aufnr, posid, vbund, pobart, pobid, pob_txt, objnr_n1, vrgng, orgvg, u_tcode, logsystem, blart, beknz,
                                                        kstar, sgtxt, bltxt, belnr, buzei, zzco_belnr, refbz_fi, werks, ebeln, ebelp, ebtxt, matnr, gbeextwg_ebx, refbn, stokz, awref_rev, gkoar, gkont, gkont_ltxt, u_lifnr,
                                                        wrgbtr, kwaer, wtgbtr, twaer, mbgbtr, meinb, usnam, wsdat, budat, bldat, cpudt, load_file, load_uuid, mod_de, mod_timp)
                select k.id, 'CJI3', iif(f.belnr is null, @ifrs_ledger_group, @comm_ledger_group), k.kokrs, k.bukrs, k.gjahr, k.perio, null, null, null, k.posid, k.vbund, k.pobart, k.pobid, k.pob_txt, k.objnr_n1, k.vrgng, k.orgvg, null, k.logsystem, k.blart, k.beknz,
                        k.kstar, k.sgtxt, k.bltxt, k.belnr, k.buzei, k.zzco_belnr, k.refbz_fi, k.werks, k.ebeln, k.ebelp, k.ebtxt, k.matnr, k.gbeextwg_ebx, k.refbn, k.stokz, k.awref_rev, k.gkoar, k.gkont, k.gkont_ktxt,
                        (case when b.vendor_cod_start < 0 then null else substring(k.zz_lif, b.vendor_cod_start, b.vendor_nume_start - b.vendor_cod_start) end) as u_lifnr,
                        k.wrvbtr, k.kwaer, k.wtgbtr, k.twaer, k.mbgbtr, k.meinb, k.usnam, k.wsdat, k.budat, k.bldat, k.cpudt, k.load_file, k.load_uuid, @kid, current_timestamp
                from k

                left join f
                on k.kokrs = f.kokrs and k.bukrs = f.bukrs and k.gjahr = f.gjahr and k.perio = f.monat and k.zzco_belnr = f.belnr and k.refbz_fi = f.buzei

                cross apply (select (case when k.zz_lif is null then -1 else patindex(@vendor_cod_start_regex, k.zz_lif) end) as vendor_cod_start,
                                    (case when k.zz_lif is null then -1 else patindex(@vendor_nume_start_regex, k.zz_lif) end) as vendor_nume_start) as b

                union all

                select f.id, 'FAGLL03', @ras_ledger_group, f.kokrs, f.bukrs, f.gjahr, f.monat, null, null, null, f.projk, f.vbund, null, null, null, f.aufnr, null, f.glvor, f.u_tcode, null, f.blart, f.shkzg,
                        f.konto, f.sgtxt, f.u_bktxt, null, null, f.belnr, f.buzei, f.werks, f.u_ebeln, f.ebelp, null, f.u_matnr, f.gbeextwg_ebx, null, null, null, f.gkart, f.gkont, null, f.u_lifnr,
                        f.dmshb, f.hwaer, f.wrshb, f.waers, f.menge, null, f.u_usnam, f.valut, f.budat, f.bldat, f.u_cpudt, f.load_file, f.load_uuid, @kid, current_timestamp
                from f

                left join k
                on k.kokrs = f.kokrs and k.bukrs = f.bukrs and k.gjahr = f.gjahr and k.perio = f.monat and k.zzco_belnr = f.belnr and k.refbz_fi = f.buzei

                where k.zzco_belnr is null;

                /* curatare */
                drop table #reco_process_matrix;
            commit transaction
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;
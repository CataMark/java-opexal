create or alter procedure oxal1.prc_clasificator_get_classify_data
    @optUuidArray nvarchar(max),
    @optAn smallint,
    @optLuna tinyint,
    @coarea char(4),
    @kid varchar(20)
as
    begin
        /* colectare uuid */
        declare @load_uuids table(
            uuid uniqueidentifier,
            an smallint,
            luna tinyint,
            coarea char(4)
        );

        if @optUuidArray is null
            insert into @load_uuids (an, luna, coarea)
            values(@optAn, @optLuna, @coarea);
        else
            insert into @load_uuids (uuid, coarea)
            select distinct b.uuid, b.coarea
            from (select cast([value] as uniqueidentifier) as uuid from openjson(@optUuidArray) where [value] is not null) as a
            inner join oxal1.tbl_int_flags as b
            on a.uuid = b.uuid
            where b.coarea = @coarea;
            

        /* get data */
        select
            a.id,
            isnull(cc_sg.segment, isnull(io_sg.segment, isnull(pm_sg.segment, ws_sg.segment))) as segment,
            a.bukrs,
            isnull(a.kostl, isnull(io_md.cost_center_resp, isnull(pm_md.cost_center_resp, ws_md.cost_center_resp))) as kostl,
            (case when a.aufnr like '4%' then left(a.aufnr, 2) else a.aufnr end) as aufnr,
            a.posid,
            (case when a.aufnr like '4%' then lower(left(pm_md.nume, patindex('%[^a-zA-Z]%', pm_md.nume) - 1)) else null end) as obj_txt,
            a.vbund,
            a.pobid,
            a.objnr_n1,
            a.vrgng,
            a.orgvg,
            a.logsystem,
            a.blart,
            a.beknz,
            a.kstar,
            oxal1.fnc_get_key_word_match(a.sgtxt) as sgtxt,
            oxal1.fnc_get_key_word_match(a.bltxt) as bltxt,
            a.werks,
            a.matnr,
            a.gkont,
            a.u_lifnr,
            a.usnam
        from oxal1.tbl_int_process_result as a

        inner join @load_uuids as b
        on (case when b.uuid is null then iif(a.gjahr = b.an and a.perio = b.luna, 1, 0)
            else iif(a.load_uuid = b.uuid, 1, 0) end) = 1 and a.kokrs = b.coarea

        left join oxal1.tbl_int_cost_center as cc_md
        on a.kokrs = cc_md.coarea and a.bukrs = cc_md.cocode and a.kostl = cc_md.cod
        left join oxal1.tbl_int_profit_center as cc_sg
        on cc_md.profit_center = cc_sg.cod

        left join oxal1.tbl_int_co_order as io_md
        on a.kokrs = io_md.coarea and a.bukrs = io_md.cocode and a.aufnr = io_md.cod
        left join oxal1.tbl_int_profit_center as io_sg
        on io_md.profit_center = io_sg.cod

        left join oxal1.tbl_int_pm_order as pm_md
        on a.kokrs = pm_md.coarea and a.bukrs = pm_md.cocode and a.aufnr = pm_md.cod
        left join oxal1.tbl_int_profit_center as pm_sg
        on pm_md.profit_center = pm_sg.cod

        left join oxal1.tbl_int_wbs as ws_md
        on a.kokrs = ws_md.coarea and a.bukrs = ws_md.cocode and a.posid = ws_md.cod
        left join oxal1.tbl_int_profit_center as ws_sg
        on ws_md.profit_center = ws_sg.cod;
    end;
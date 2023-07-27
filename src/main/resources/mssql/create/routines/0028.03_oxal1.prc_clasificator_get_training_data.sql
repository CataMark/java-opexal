create or alter procedure oxal1.prc_clasificator_get_training_data
    @startAn smallint,
    @startLuna tinyint,
    @coarea char(4)
as
    begin
        /* coloana cu clasele de clasificat (ex.: ocateg) trebuie sa fie intotdeauna ultima */
        select top 1000000
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
            a.usnam,
            a.ocateg
        from oxal1.tbl_int_process_result as a

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
        on ws_md.profit_center = ws_sg.cod

        where a.kokrs = @coarea and a.ocateg is not null and
            datefromparts(a.gjahr, iif(a.perio > 12, 12, a.perio), 1) <= datefromparts(@startAn, iif(@startLuna > 12, 12, @startLuna), 1);
    end;
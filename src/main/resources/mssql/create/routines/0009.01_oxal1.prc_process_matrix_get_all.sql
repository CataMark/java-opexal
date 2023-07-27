create or alter procedure oxal1.prc_process_matrix_get_all
as
    select a.*,
        b.nume as sap_oper_nume,
        c.nume as sap_tranz_nume,
        d.nume as doc_tip_nume,
        e.nume as app_oper_nume,
        e.ordine
    from oxal1.tbl_int_process_matrix as a

    inner join oxal1.tbl_int_sap_oper as b
    on a.sap_oper = b.cod

    left join oxal1.tbl_int_sap_oper as c
    on a.sap_tranz = c.cod

    left join oxal1.tbl_int_sap_doc_tip as d
    on a.doc_tip = d.cod

    inner join oxal1.tbl_int_operations as e
    on a.app_oper = e.cod

    order by e.ordine asc, e.cod asc, a.sap_oper asc, a.sap_tranz asc, a.doc_tip asc, a.syst_logic asc;
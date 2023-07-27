create table oxal1.tbl_int_process_matrix(
    id uniqueidentifier not null constraint tbl_int_process_matrix_df1 default newsequentialid(),
    sap_oper varchar(5) not null,
    sap_tranz varchar(5),
    doc_tip varchar(2),
    syst_logic char(6),
    app_oper char(4) not null,
    blocat bit not null constraint tbl_int_process_matrix_df2 default 0,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_process_matrix_df3 default current_timestamp,
    constraint tbl_int_process_matrix_pk primary key (id),
    constraint tbl_int_process_matrix_fk1 foreign key (sap_oper) references oxal1.tbl_int_sap_oper(cod),
    constraint tbl_int_process_matrix_fk2 foreign key (sap_tranz) references oxal1.tbl_int_sap_oper(cod),
    constraint tbl_int_process_matrix_fk3 foreign key (doc_tip) references oxal1.tbl_int_sap_doc_tip(cod),
    constraint tbl_int_process_matrix_fk4 foreign key (app_oper) references oxal1.tbl_int_operations(cod)
)
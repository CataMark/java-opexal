create table oxal1.tbl_int_sap_doc_tip(
    cod varchar(2) not null,
    nume nvarchar(100) not null,
    mod_de varchar(20) not null,
    mod_timp datetime constraint tbl_int_sap_doc_tip_df1 default current_timestamp,
    constraint tbl_int_sap_doc_tip_pk primary key (cod)
);
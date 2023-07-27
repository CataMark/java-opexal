create table oxal1.tbl_int_upload_matrix(
    id uniqueidentifier not null constraint tbl_int_upload_matrix_df1 default newsequentialid(),
    cocode char(4) not null,
    sap_tranz varchar(10) not null,
    blocat bit not null constraint tbl_int_upload_matrix_df2 default 0,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_upload_matrix_df3 default current_timestamp,
    constraint tbl_int_upload_matrix_pk primary key (id),
    constraint tbl_int_upload_matrix_fk1 foreign key (cocode) references oxal1.tbl_int_cocode(cod),
    constraint tbl_int_upload_matrix_fk2 foreign key (sap_tranz) references oxal1.tbl_int_sap_tranz(cod),
    constraint tbl_int_upload_matrix_uq1 unique (cocode, sap_tranz)
);
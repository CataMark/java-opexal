create table oxal1.tbl_int_flags(
    id uniqueidentifier not null constraint tbl_int_flags_df1 default newsequentialid(),
    uuid uniqueidentifier not null,
    tip varchar(30) not null,
    coarea char(4) not null,
    sap_tranz varchar(10) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_flags_df2 default current_timestamp,
    constraint tbl_int_flags_pk primary key (uuid, coarea, sap_tranz),
    constraint tbl_int_flags_uq1 unique(id)
);
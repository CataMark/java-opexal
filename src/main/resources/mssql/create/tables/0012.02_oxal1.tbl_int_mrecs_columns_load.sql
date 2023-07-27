create table oxal1.tbl_int_mrecs_columns_load(
    load_id uniqueidentifier not null,
    sap_tranz varchar(10) not null,
    lang char(2) not null,
    cod varchar(15) not null,
    nume nvarchar(50) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_mrecs_columns_load_df1 default current_timestamp,
    constraint tbl_int_mrecs_columns_load_pk primary key (sap_tranz, lang, cod),
    constraint tbl_int_mrecs_columns_load_fk1 foreign key (sap_tranz) references oxal1.tbl_int_sap_tranz(cod)
);
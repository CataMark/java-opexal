create table oxal1.tbl_int_sap_tranz(
    cod varchar(10) not null,
    nume nvarchar(100) not null,
    tip char(2) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_sap_tranz_df1 default current_timestamp,
    constraint tbl_int_sap_tranz_pk primary key (cod)
)
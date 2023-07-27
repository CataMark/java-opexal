create table oxal1.tbl_int_operations(
    cod char(4) not null,
    nume nvarchar(100) not null,
    ordine tinyint not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_operations_df1 default current_timestamp,
    constraint tbl_int_operations_pk primary key (cod),
    constraint tbl_int_operations_uq1 unique (ordine)
);
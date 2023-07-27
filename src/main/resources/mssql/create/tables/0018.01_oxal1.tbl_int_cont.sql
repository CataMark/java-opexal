create table oxal1.tbl_int_cont(
    cod char(10) not null,
    alternativ char(10),
    nume nvarchar(100),
    grup char(10),
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_cont_df1 default current_timestamp,
    constraint tbl_int_cont_pk primary key (cod)
);
create table oxal1.tbl_int_vendor(
    cod char(7) not null,
    nume nvarchar(100),
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_vendor_df1 default current_timestamp,
    constraint tbl_int_vendor_pk primary key (cod)
);
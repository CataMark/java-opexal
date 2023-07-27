create table oxal1.tbl_int_profit_center_load(
    load_id uniqueidentifier not null,
    cod char(10) not null,
    segment varchar(30),
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_profit_center_load_df1 default current_timestamp,
    constraint tbl_int_profit_center_load_pk primary key (cod),
    constraint tbl_int_profit_center_load_fk2 foreign key (segment) references oxal1.tbl_int_segment(cod)
);
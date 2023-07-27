create table oxal1.tbl_int_segment(
    cod varchar(30) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_segment_df1 default current_timestamp,
    constraint tbl_int_segment_pk primary key (cod)
);
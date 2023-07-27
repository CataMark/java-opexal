create table oxal1.tbl_int_clasificator_log(
    id uniqueidentifier not null constraint tbl_int_clasificator_log_df1 default newsequentialid(),
    an smallint not null,
    luna tinyint not null,
    coarea char(4) not null,
    file_path varchar(4000) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_clasificator_log_df2 default current_timestamp,
    constraint tbl_int_clasificator_log_pk primary key (an, luna, coarea),
    constraint tbl_int_clasificator_log_uq1 unique (id)
);
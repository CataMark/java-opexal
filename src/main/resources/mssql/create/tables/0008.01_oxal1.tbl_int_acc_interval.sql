create table oxal1.tbl_int_acc_interval(
    id uniqueidentifier not null constraint tbl_int_acc_interval_df1 default newsequentialid(),
    acc_start char(10) not null,
    acc_end char(10) not null,
    process bit not null constraint tbl_int_acc_interval_df2 default 0,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_acc_interval_df3 default current_timestamp,
    constraint tbl_int_acc_interval_pk primary key (id),
    constraint tbl_int_acc_interval_uq1 unique (acc_start, acc_end),
    constraint tbl_int_acc_interval_ck1 check (cast(acc_end as bigint) >= cast(acc_start as bigint))
);
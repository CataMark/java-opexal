create table oxal1.tbl_int_acc_ledgers(
    id uniqueidentifier not null constraint tbl_int_acc_ledgers_df1 default newsequentialid(),
    ledger char(2) not null,
    grup char(2) not null,
    stndrd varchar(4) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_acc_ledgers_df2 default current_timestamp,
    constraint tbl_int_acc_ledgers_pk primary key (id),
    constraint tbl_int_acc_ledgers_uq1 unique (ledger, grup)
);
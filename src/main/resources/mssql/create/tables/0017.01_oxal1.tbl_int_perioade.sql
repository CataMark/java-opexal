create table oxal1.tbl_int_perioade(
    id uniqueidentifier not null constraint tbl_int_perioade_df1 default newsequentialid(),
    an smallint not null,
    luna tinyint not null,
    inchis bit not null constraint tbl_int_perioade_df2 default 0,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_perioade_df3 default current_timestamp,
    constraint tbl_int_perioade_pk primary key (id),
    constraint tbl_int_perioade_uq1 unique (an, luna),
    constraint tbl_int_perioade_ck1 check (an between 2000 and 9999),
    constraint tbl_int_perioade_ck2 check (luna between 1 and 16)
);
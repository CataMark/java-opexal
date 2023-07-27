create table oxal1.tbl_int_log(
    id uniqueidentifier not null constraint tbl_int_log_df1 default newsequentialid(),
    sursa varchar(255) not null,
    tip varchar(5) not null,
    mesaj nvarchar(4000) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_log_df2 default current_timestamp,
    constraint tbl_int_log_pk primary key (id)
);
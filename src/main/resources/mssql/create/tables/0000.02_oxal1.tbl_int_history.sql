create table oxal1.tbl_int_history(
    id uniqueidentifier not null constraint tbl_int_history_df1 default newsequentialid(),
    tabela varchar(100) not null,
    an smallint not null,
    perio tinyint not null,
    bukrs char(4) not null,
    row_id uniqueidentifier not null,
    json_record nvarchar(max) not null,
    modifier char(1) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_history_df2 default current_timestamp,
    constraint tbl_int_history_pk primary key (id),
    constraint tbl_int_history_ck1 check (modifier in ('U','D'))
);
go

create index tbl_int_history_ix1 on oxal1.tbl_int_history(tabela asc, an asc, perio asc, bukrs asc);
go
create table oxal1.tbl_int_cocode(
    cod char(4) not null,
    nume nvarchar(100) not null,
    coarea char(4) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_cocode_df1 default current_timestamp,
    constraint tbl_int_cocode_pk primary key (cod),
    constraint tbl_int_cocode_fk1 foreign key (coarea) references oxal1.tbl_int_coarea(cod),
    constraint tbl_int_cocode_uq1 unique (coarea, cod)
)
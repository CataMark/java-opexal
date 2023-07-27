create table oxal1.tbl_int_wbs(
    cod varchar(35) not null,
    definitie char(11),
    coarea char(4) not null,
    cocode char(4) not null,
    nume nvarchar(100),
    profit_center char(10),
    cost_center_resp char(10),
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_wbs_df1 default current_timestamp,
    constraint tbl_int_wbs_pk primary key (cod),
    constraint tbl_int_wbs_fk1 foreign key (coarea, cocode) references oxal1.tbl_int_cocode(coarea, cod),
    constraint tbl_int_wbs_fk2 foreign key (profit_center) references oxal1.tbl_int_profit_center(cod)
);
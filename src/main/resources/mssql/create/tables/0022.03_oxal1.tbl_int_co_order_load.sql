create table oxal1.tbl_int_co_order_load(
    load_id uniqueidentifier not null,
    cod char(9) not null,
    coarea char(4) not null,
    cocode char(4) not null,
    nume nvarchar(100),
    profit_center char(10),
    cost_center_resp char(10),
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_co_order_load_df1 default current_timestamp,
    constraint tbl_int_co_order_load_pk primary key (cod),
    constraint tbl_int_co_order_load_fk1 foreign key (coarea, cocode) references oxal1.tbl_int_cocode(coarea, cod),
    constraint tbl_int_co_order_load_fk2 foreign key (profit_center) references oxal1.tbl_int_profit_center(cod)
);
create table oxal1.tbl_int_co_order_settle_rule_load(
    load_id uniqueidentifier not null,
    coarea char(4) not null,
    cocode char(4) not null,
    an smallint not null,
    luna tinyint not null,
    comanda char(9) not null,
    cost_center char(10) not null,
    procent decimal(5,4) not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_co_order_settle_rule_load_df1 default current_timestamp,
    constraint tbl_int_co_order_settle_rule_load_pk primary key (coarea, an, luna, comanda, cost_center),
    constraint tbl_int_co_order_settle_rule_load_fk1 foreign key (coarea, cocode) references oxal1.tbl_int_cocode(coarea, cod),
    constraint tbl_int_co_order_settle_rule_load_ck1 check (procent != 0),
    constraint tbl_int_co_order_settle_rule_load_ck2 check (an between 2000 and 9999),
    constraint tbl_int_co_order_settle_rule_load_ck3 check (luna between 1 and 16)
);
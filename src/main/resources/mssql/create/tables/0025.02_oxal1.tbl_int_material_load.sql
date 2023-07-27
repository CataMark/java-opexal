create table oxal1.tbl_int_material_load(
    load_id uniqueidentifier not null,
    cod char(10) not null,
    nume nvarchar(100),
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_material_load_df1 default current_timestamp,
    constraint tbl_int_material_load_pk primary key (cod)
);
create table oxal1.tbl_int_process_result_load(
    load_id uniqueidentifier not null,
    id uniqueidentifier not null,
    ocateg int not null,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_process_result_load_df1 default current_timestamp
);
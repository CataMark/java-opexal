create table oxal1.tbl_int_key_words(
    id int identity(1,1) not null,
    key_word nvarchar(400) not null,
    acronim bit not null constraint tbl_int_key_word_df1 default 0,
    mod_de varchar(20),
    mod_timp datetime not null constraint tbl_int_key_words_df2 default current_timestamp,
    constraint tbl_int_key_words_pk primary key (key_word)
);
create or alter procedure oxal1.prc_key_word_get_all
as
    select * from oxal1.tbl_int_key_words as a
    order by a.key_word asc;
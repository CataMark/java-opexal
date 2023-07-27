create or alter function oxal1.fnc_key_word_get_by_id(
    @id int
)
returns table
as
return
    select * from oxal1.tbl_int_key_words where id = @id;
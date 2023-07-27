create or alter procedure oxal1.prc_key_word_delete_return
    @id int
as
    begin
        set nocount on;

        delete from oxal1.tbl_int_key_words where id = @id;

        if exists (select * from oxal1.tbl_int_key_words as a where a.id = @id)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;
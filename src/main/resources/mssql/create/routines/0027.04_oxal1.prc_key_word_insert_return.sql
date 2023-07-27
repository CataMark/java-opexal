create or alter procedure oxal1.prc_key_word_insert_return
    @key_word nvarchar(400),
    @acronim bit,
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @id table (id int);

        insert into oxal1.tbl_int_key_words (key_word, acronim, mod_de, mod_timp)
        output inserted.id into @id(id)
        values (@key_word, @acronim, @kid, current_timestamp);

        select top 1 b.*
        from @id as a
        cross apply (select * from oxal1.fnc_key_word_get_by_id(a.id)) as b;
    end;
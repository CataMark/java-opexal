create or alter function oxal1.fnc_get_key_word_match(
    @string nvarchar(2000)
)
returns int
as
    begin
        declare @rezultat int;
        if @string is null or len(@string) = 0 return @rezultat;

        select top 1 @rezultat = b.id
        from oxal1.fnc_get_words_from_string(@string) as a, oxal1.tbl_int_key_words as b
        where (case
            when a.string = b.key_word then 1
            when iif(b.acronim = 0 and len(a.string) >= 4, charindex(a.string, b.key_word, 0) + charindex(b.key_word, a.string, 0), 0) = 1 then 1
            when iif(b.acronim = 0 and len(a.string) >= 5, charindex(substring(a.string, 1, len(a.string) - 1), b.key_word, 0), 0) = 1 then 1
            when iif(b.acronim = 0 and len(a.string) >= 6, charindex(substring(a.string, 1, len(a.string) - 2), b.key_word, 0), 0) = 1 then 1
            when iif(b.acronim = 0 and len(a.string) >= 7, charindex(substring(a.string, 1, len(a.string) - 3), b.key_word, 0), 0) = 1 then 1
            when iif(b.acronim = 0 and len(a.string) >= 8, charindex(substring(a.string, 1, len(a.string) - 4), b.key_word, 0), 0) = 1 then 1
            else 0 end) = 1
        order by (case when len(a.string) >= 3 then 0 else 1 end) asc;

        return @rezultat;
    end;
create or alter function oxal1.fnc_get_words_from_string(
    @string nvarchar(2000)
)
returns @tbl_strings table (string varchar(200))
as
    begin
        if len(@string) = 0 return;

        /* inlocuire diacritice ă, â, î, ș, ț */
        declare @regex varchar(10) = '%[^a-z]%';
        declare @lstring varchar(2000);
        select @lstring = cast(replace(replace(lower(trim(@string collate Romanian_100_BIN)), N'ș' collate Romanian_100_BIN, N's'), N'ț' collate Romanian_100_BIN, N't') as varchar(2000))
            collate SQL_Latin1_General_CP1253_CI_AI;
        
        with cte as (
            select
                a.pend,
                (case a.pend
                    when 0 then @lstring
                    when 1 then null
                    else substring(@lstring, 1, a.pend - 1)
                end) as word,
                (case a.pend
                    when 0 then null
                    when len(@lstring) then null
                    else substring(@lstring, a.pend + 1, len(@lstring) - a.pend)
                end) as rest
            from (select patindex(@regex, @lstring) as pend) as a

            union all

            select
                a.pend,
                (case a.pend
                    when 0 then cte.rest
                    when 1 then null
                    else substring(cte.rest, 1, a.pend - 1)
                end) as word,
                (case a.pend
                    when 0 then null
                    when len(@lstring) then null
                    else substring(cte.rest, a.pend + 1, len(cte.rest) - a.pend)
                end) as rest
            from cte
            cross apply (select patindex(@regex, cte.rest) as pend) as a
            where cte.pend > 0 and cte.rest is not null
        )
        insert into @tbl_strings(string)
        select cte.word from cte
        where len(cte.word) >= 2
        option (maxrecursion 100);

        return;
    end;
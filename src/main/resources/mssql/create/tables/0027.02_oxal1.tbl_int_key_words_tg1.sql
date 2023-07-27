create or alter trigger oxal1.tbl_int_key_words_tg1
on oxal1.tbl_int_key_words
after update, delete
as
    begin
        set nocount on;
        begin try
            /* history */
            declare @not_record_history bit = cast(session_context(N'oxal1_not_history') as bit);
            if @not_record_history is null or @not_record_history != 1
                insert into oxal1.tbl_int_history (tabela, an, perio, bukrs, row_id, json_record, modifier, mod_de, mod_timp)
                select 'tbl_int_key_words' as tabela,
                    2000 as gjahr,
                    1 as perio,
                    '9999' as bukrs,
                    newid() as row_id,
                    (select * from deleted as a where a.id = d.id for json auto, without_array_wrapper) as json_record,
                    (case when i.id is not null then 'U' else 'D' end) as modifier,
                    cast(session_context(N'oxal1_user_id') as varchar(20)) as mod_de,
                    current_timestamp as mod_timp
                from deleted as d
                left join inserted as i
                on d.id = i.id;
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;
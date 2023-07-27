create or alter trigger oxal1.tbl_int_process_result_tg1
on oxal1.tbl_int_process_result
after insert, update, delete
as
    begin
        set nocount on;
        begin try
            /* history */
            declare @not_record_history bit = cast(session_context(N'oxal1_not_history') as bit);
            if @not_record_history is null or @not_record_history != 1
                insert into oxal1.tbl_int_history (tabela, an, perio, bukrs, row_id, json_record, mod_de, mod_timp)
                select 'tbl_int_process_result' as tabela,
                    d.gjahr as an,
                    d.perio,
                    d.bukrs,
                    d.id as row_id,
                    (select * from deleted as a where a.id = d.id for json auto, without_array_wrapper) as json_record,
                    cast(session_context(N'oxal1_user_id') as varchar(20)) as mod_de,
                    current_timestamp as mod_timp
                from deleted as d;
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;
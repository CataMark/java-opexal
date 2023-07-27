create or alter trigger oxal1.tbl_int_acc_interval_tg1
on oxal1.tbl_int_acc_interval
after insert, update
as
    begin
        begin try
            set nocount on;
            if exists (
                    /* verificare ca nu exista suprapuneri fata de datele existente deja in tabela */
                    select * from inserted as i, oxal1.tbl_int_acc_interval as a
                        where i.id != a.id and ((cast(i.acc_start as bigint) between cast(a.acc_start as bigint) and cast(a.acc_end as bigint)) or (cast(i.acc_end as bigint) between cast(a.acc_start as bigint) and cast(a.acc_end as bigint)) or
                                (cast(a.acc_start as bigint) between cast(i.acc_start as bigint) and cast(i.acc_end as bigint)) or (cast(a.acc_end as bigint) between cast(i.acc_start as bigint) and cast(i.acc_end as bigint)))
                    union all
                    /* verificare ca nu exista suprapuneri in datele incarcate */
                    select * from inserted as i, inserted as a
                        where i.id != a.id and ((cast(i.acc_start as bigint) between cast(a.acc_start as bigint) and cast(a.acc_end as bigint)) or (cast(i.acc_end as bigint) between cast(a.acc_start as bigint) and cast(a.acc_end as bigint)) or
                                (cast(a.acc_start as bigint) between cast(i.acc_start as bigint) and cast(i.acc_end as bigint)) or (cast(a.acc_end as bigint) between cast(i.acc_start as bigint) and cast(i.acc_end as bigint)))
                )
                raiserror('ACCOUNT INTERVAL OVERLAPPING', 16, 1);
        end try
        begin catch
            if @@trancount > 0 rollback transaction;
            throw;
        end catch
    end;

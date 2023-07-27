create or alter procedure oxal1.prc_perioade_delete_return
    @id uniqueidentifier
as
    begin
        set nocount on;
        begin try
            /* verificare daca exista inregistrari */
            if exists (select *
                from oxal1.tbl_int_fagll03 as a
                inner join oxal1.tbl_int_perioade as b
                on a.gjahr = b.an and a.monat = b.luna
                where b.id = @id)
                raiserror('RECORDS_EXIST', 16, 1);

            if exists (select *
                from oxal1.tbl_int_ksb1 as a
                inner join oxal1.tbl_int_perioade as b
                on a.gjahr = b.an and a.perio = b.luna
                where b.id = @id)
                raiserror('RECORDS_EXIST', 16, 1);

            if exists (select *
                from oxal1.tbl_int_kob1 as a
                inner join oxal1.tbl_int_perioade as b
                on a.gjahr = b.an and a.perio = b.luna
                where b.id = @id)
                raiserror('RECORDS_EXIST', 16, 1);

            if exists (select *
                from oxal1.tbl_int_cji3 as a
                inner join oxal1.tbl_int_perioade as b
                on a.gjahr = b.an and a.perio = b.luna
                where b.id = @id)
                raiserror('RECORDS_EXIST', 16, 1);

            /* efactuare stergere */
            delete from oxal1.tbl_int_perioade where id = @id;

            /* testare operatiune reusita */
            if exists (select * from oxal1.tbl_int_perioade where id = @id)
                select cast(0 as bit) as rezultat;
            else
                select cast(1 as bit) as rezultat;
        end try
        begin catch
            throw;
        end catch
    end;
create or alter procedure oxal1.prc_acc_interval_insert_return
    @acc_start char(10),
    @acc_end char(10),
    @proces bit,
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @id table (id uniqueidentifier);

        insert into oxal1.tbl_int_acc_interval (acc_start, acc_end, process, mod_de, mod_timp)
        output inserted.id into @id
        values (@acc_start, @acc_end, @proces, @kid, current_timestamp);

        select top 1 b.*
        from @id as a
        cross apply (select * from oxal1.fnc_acc_inteval_get_by_id(a.id)) as b;
    end;
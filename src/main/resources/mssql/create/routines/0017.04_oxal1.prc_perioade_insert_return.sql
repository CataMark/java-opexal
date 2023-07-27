create or alter procedure oxal1.prc_perioade_insert_return
    @an smallint,
    @luna tinyint,
    @inchis bit,
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @id table (id uniqueidentifier);

        insert into oxal1.tbl_int_perioade(an, luna, inchis, mod_de, mod_timp)
        output inserted.id into @id
        values (@an, @luna, @inchis, @kid, current_timestamp);

        select top 1 b.*
        from @id as a
        cross apply (select * from oxal1.fnc_perioade_get_by_id(a.id)) as b;
    end;
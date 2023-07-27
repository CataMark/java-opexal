create or alter procedure oxal1.prc_clasificator_insert_return
    @an smallint,
    @luna tinyint,
    @coarea char(4),
    @file_path varchar(4000),
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @id as table (id uniqueidentifier);

        insert into oxal1.tbl_int_clasificator_log (an, luna, coarea, file_path, mod_de, mod_timp)
        output inserted.id into @id(id)
        values (@an, @luna, @coarea, @file_path, @kid, current_timestamp);

        select top 1 * 
        from oxal1.tbl_int_clasificator_log as a
        inner join @id as b
        on a.id = b.id;
    end;
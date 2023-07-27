create or alter procedure oxal1.prc_flags_insert_return
    @uuid uniqueidentifier,
    @tip varchar(30),
    @coarea char(4),
    @sap_tranz varchar(10),
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @id table (id uniqueidentifier);

        insert into oxal1.tbl_int_flags(uuid, tip, coarea, sap_tranz, mod_de, mod_timp)
        output inserted.id into @id(id)
        values (@uuid, @tip, @coarea, @sap_tranz, @kid, current_timestamp);

        select top 1 b.*
        from @id as a
        cross apply (select * from oxal1.fnc_flags_get_by_id(a.id)) as b;
    end;
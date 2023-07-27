create or alter procedure oxal1.prc_upload_matrix_insert_return
    @cocode char(4),
    @sap_tranz varchar(10),
    @blocat bit,
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @id table(id uniqueidentifier);

        insert into oxal1.tbl_int_upload_matrix (cocode, sap_tranz, blocat, mod_de, mod_timp)
        output inserted.id into @id
        values(@cocode, @sap_tranz, @blocat, @kid, current_timestamp);

        select top 1 b.*
        from @id as a
        cross apply(select * from oxal1.fnc_upload_matrix_get_by_id(a.id)) as b;
    end;
create or alter procedure oxal1.prc_mrecs_col_insert_return
    @sap_tranz varchar(10),
    @lang char(2),
    @cod varchar(15),
    @nume nvarchar(50),
    @kid varchar(20)
as
    begin
        set nocount on;
        declare @id table(id uniqueidentifier);

        insert into oxal1.tbl_int_mrecs_columns (sap_tranz, lang, cod, nume, mod_de, mod_timp)
        output inserted.id into @id(id)
        values (@sap_tranz, @lang, @cod, @nume, @kid, current_timestamp);

        select top 1 b.* from @id as a
        cross apply (select * from oxal1.fnc_mrecs_col_get_by_id(a.id)) as b;
    end;
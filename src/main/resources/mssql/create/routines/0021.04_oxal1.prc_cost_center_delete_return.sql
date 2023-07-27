create or alter procedure oxal1.prc_cost_center_delete_return
    @cod char(10),
    @coarea char(4)
as
    begin
        set nocount on;

        delete from oxal1.tbl_int_cost_center
        where cod = @cod and coarea = @coarea;

        /* testare operatiune a reusit */
        if exists (select * from oxal1.tbl_int_cost_center as a where a.cod = @cod and a.coarea = @coarea)
            select cast(0 as bit) as rezultat;
        else
            select cast(1 as bit) as rezultat;
    end;
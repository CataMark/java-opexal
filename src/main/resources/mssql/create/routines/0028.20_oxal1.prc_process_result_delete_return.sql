create or alter procedure oxal1.prc_process_result_delete_return
    @an smallint,
    @luna tinyint,
    @coarea char(4),
    @kid varchar(20)
as
    begin
        set nocount on;
        exec sys.sp_set_session_context @key = N'oxal1_user_id', @value = @kid, @readonly = 0;

        delete from oxal1.tbl_int_process_result
        where gjahr = @an and perio = @luna and kokrs = @coarea;

        if exists (select * from oxal1.tbl_int_process_result where gjahr = @an and perio = @luna and kokrs = @coarea)
            select cast(0 as bit) as result;
        else
            select cast(1 as bit) as result;
    end;
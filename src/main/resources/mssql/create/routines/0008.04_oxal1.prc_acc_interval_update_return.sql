create or alter procedure oxal1.prc_acc_interval_update_return
    @id uniqueidentifier,
    @acc_start char(10),
    @acc_end char(10),
    @proces bit, 
    @kid varchar(20)
as
    begin
        set nocount on;

        update oxal1.tbl_int_acc_interval
        set acc_start = @acc_start, acc_end = @acc_end, process = @proces, mod_de = @kid, mod_timp = current_timestamp
        where id = @id;

        select * from oxal1.fnc_acc_inteval_get_by_id(@id);
    end;
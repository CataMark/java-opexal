create or alter procedure oxal1.prc_acc_ledgers_get_list
as
    select distinct
        ledger,
        first_value(stndrd) over (partition by ledger order by id asc
                                rows between unbounded preceding and unbounded following) as stndrd
    from oxal1.tbl_int_acc_ledgers;
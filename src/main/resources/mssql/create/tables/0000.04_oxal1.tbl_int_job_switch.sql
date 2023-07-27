create table oxal1.tbl_int_job_switch(
    cod varchar(30) not null,
    nume nvarchar(200) not null,
    blocat bit not null constraint tbl_int_job_switch_df1 default 1,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_job_switch_df2 default current_timestamp,
    constraint tbl_int_job_switch_pk primary key (cod)
);
go

insert into oxal1.tbl_int_job_switch (cod, nume, blocat, mod_de, mod_timp) values
    ('CREATE_TRAINING_MODEL', N'Job for running classification model. Scheduled on every Sunday starting with 12:00 PM UTC. Running for aprox. 2 hours per Controlling Area.', 0, 'SYSTEM', current_timestamp),
    ('DELETE_OLD_RPA_FILES', N'Job for deleting RPA files older than 90 days. Scheduled on every working day starting with 20:00 UTC.', 0, 'SYSTEM', current_timestamp),
    ('OPEN_CURRENT_PERIOD', N'Job for opening the period for the current month. Scheduled on every day starting with 00:00 UTC.', 0, 'SYSTEM', current_timestamp),
    ('CHECK_OPENED_PERIODS', N'Job for sending notifications if a period is opened for more than 63 days. Scheduled on every working day starting with 23:00 UTC.', 0, 'SYSTEM', current_timestamp),
    ('PROCESS_RPA_FILES', N'Job for uploading and processing RPA file from shared folder, on request.', 0, 'SYSTEM', current_timestamp);
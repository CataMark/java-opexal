create table oxal1.tbl_int_process_result(
    id uniqueidentifier not null constraint tbl_int_process_result_df1 default newsequentialid(),
    source_id uniqueidentifier not null,
    sap_tranz varchar(10) not null,
    u_ldgrp char(2) not null,
    kokrs char(4) not null,
    bukrs char(4) not null,
    gjahr smallint not null,
    perio tinyint not null,
    obart char(3),
    kostl char(10),
    aufnr varchar(10),
    posid varchar(35),
    vbund varchar(5),
    pobart char(3),
    pobid varchar(35),
    pob_txt nvarchar(100),
    objnr_n1 varchar(35),
    vrgng char(4),
    orgvg char(4),
    u_tcode varchar(10),
    logsystem char(10),
    blart char(2),
    beknz char(1) not null,
    kstar char(10) not null,
    sgtxt nvarchar(100),
    bltxt nvarchar(100),
    belnr char(10),
    buzei smallint,
    zzco_belnr char(10),
    refbz_fi smallint,
    werks char(4),
    ebeln char(10),
    ebelp smallint,
    ebtxt nvarchar(100),
    matnr char(10),
    gbeextwg_ebx varchar(20),
    refbt char(1),
    refgj smallint,
    refbn varchar(10),
    stokz char(1),
    awref_rev varchar(10),
    gkoar char(1),
    gkont varchar(10),
    gkont_ltxt nvarchar(100),
    u_lifnr char(7),
    wrgbtr decimal(12, 2) not null,
    kwaer char(3) not null,
    wtgbtr decimal(12, 2) not null,
    twaer char(3) not null,
    mbgbtr decimal(11, 4),
    meinb char(5),
    usnam varchar(20) not null,
    wsdat date,
    budat date not null,
    bldat date not null,
    cpudt date not null,
    ocateg int,
    acuratete decimal(5, 4),
    load_file varchar(100),
    load_uuid uniqueidentifier,
    mod_de varchar(20) not null,
    mod_timp datetime not null constraint tbl_int_process_result_df2 default current_timestamp,
    constraint tbl_int_process_result_pk primary key(id),
    constraint tbl_int_process_result_uq1 unique(source_id, sap_tranz)
);
go

create index tbl_int_process_result_ix1 on oxal1.tbl_int_process_result(gjahr asc, perio asc, kokrs asc);
go

create index tbl_int_process_result_ix2 on oxal1.tbl_int_process_result(kokrs asc, load_uuid asc);
go
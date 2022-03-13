----------------------------------------------------------------
-- Generated by eu.giulioquaresima.unicam.turns.domain.ddl.DDL --
-- at 2022-03-13T15:08:53.326523131Z                          --
----------------------------------------------------------------

    create table owner (
       id bigint generated by default as identity,
        version bigint,
        name varchar(255) not null,
        primary key (id)
    );

    create table session (
       id bigint generated by default as identity,
        version bigint,
        current_ticket_index integer not null,
        end_time timestamp,
        maximum_number integer not null,
        start_time timestamp,
        ticket_dispenser_id bigint not null,
        primary key (id)
    );

    create table session_tickets (
       session_id bigint not null,
        number integer not null,
        unique_identifier binary not null,
        withraw_time timestamp not null,
        tickets_order integer not null,
        primary key (session_id, tickets_order)
    );

    create table ticket_dispenser (
       id bigint generated by default as identity,
        version bigint,
        label varchar(255) not null,
        owner_id bigint not null,
        primary key (id)
    );

    alter table session_tickets 
       add constraint UK_f2lu52v9etv714v4sp2pge2bl unique (unique_identifier);

    alter table session 
       add constraint FKm5msfuh6n6c0lbuinymkqf16s 
       foreign key (ticket_dispenser_id) 
       references ticket_dispenser;

    alter table session_tickets 
       add constraint FKbuiqi6yv9vcm70hgsm46mi678 
       foreign key (session_id) 
       references session;

    alter table ticket_dispenser 
       add constraint FKrsixq2cv6d0bjlkcfwkxet2j7 
       foreign key (owner_id) 
       references owner;

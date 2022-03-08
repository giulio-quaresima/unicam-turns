----------------------------------------------------------------
-- Generated by eu.giulioquaresima.unicam.turns.domain.ddl.DDL --
-- at 2022-03-08T22:37:10.645438101Z                          --
----------------------------------------------------------------

    create table session (
       id int8 generated by default as identity,
        version int8,
        current_ticket int4,
        end_time timestamp,
        start_time timestamp,
        ticket_dispenser_id int8 not null,
        primary key (id)
    );

    create table session_tickets (
       session_id int8 not null,
        unique_identifier uuid not null,
        tickets_order int4 not null,
        primary key (session_id, tickets_order)
    );

    create table ticket_dispenser (
       id int8 generated by default as identity,
        version int8,
        primary key (id)
    );

    alter table if exists session 
       add constraint FKm5msfuh6n6c0lbuinymkqf16s 
       foreign key (ticket_dispenser_id) 
       references ticket_dispenser;

    alter table if exists session_tickets 
       add constraint FKbuiqi6yv9vcm70hgsm46mi678 
       foreign key (session_id) 
       references session;

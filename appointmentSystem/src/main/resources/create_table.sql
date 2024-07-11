create table userinfo
(
    id       varchar(255) not null comment 'Primary Key'
        primary key,
    email    varchar(255) not null comment 'Email',
    username varchar(255) not null comment 'Username',
    phone    varchar(255) not null comment 'Phone'
);

create table classroom
(
    id             int auto_increment comment 'Primary Key'
        primary key,
    classroom_name varchar(255) not null comment 'Classroom Name'
)
    comment 'Classroom';

create table classroomtimetable
(
    id              int auto_increment comment 'Primary Key'
        primary key,
    appointmentDate date     not null comment 'Appointment Date',
    startTime       datetime not null comment 'Start Time',
    endTime         datetime not null comment 'End Time',
    classroom       int      not null comment 'Classroom ID'
)
    comment 'Classroom Time Table';

create table classroomclassification
(
    id int auto_increment comment 'Primary Key' primary key ,
    name varchar(255)
)
comment 'the classification of classroom';

create table classroomToClassification
(
    classroom int,
    classification int,
    foreign key (classroom) references classroom(id),
    foreign key (classification) references classroomclassification(id)
)
comment 'classification to classroom';

create table credit
(
    id          int auto_increment comment 'Primary Key'
        primary key,
    credit_name varchar(255) not null comment 'Credit Name'
)
    comment 'Credit';

create table officehourevent
(
    id              int auto_increment comment 'Primary Key'
        primary key,
    appointmentDate date         not null comment 'Appointment Date',
    startTime       datetime     not null comment 'Start Time',
    endTime         datetime     not null comment 'End Time',
    student         varchar(255) not null comment 'Student ID',
    teacher         varchar(255) not null comment 'Teacher ID',
    note            text         null comment 'Note',
    question        text         null comment 'Question',
    refuseResult    text         null comment 'Refuse Result',
    workSummary     text         null comment 'Work Summary',
    state           int          not null comment 'State',
    foreign key (student) references userinfo(id),
    foreign key (teacher) references userinfo(id)
)
    comment 'Office Hour Event';

create table role
(
    id        int auto_increment comment 'Primary Key'
        primary key,
    role_name varchar(255) not null comment 'Role Name'
)
    comment 'Role';

create table rolecredit
(
    role_id   int not null comment 'Role ID',
    credit_id int not null comment 'Credit ID',
    primary key (role_id, credit_id),
    constraint rolecredit_ibfk_1
        foreign key (role_id) references role (id),
    constraint rolecredit_ibfk_2
        foreign key (credit_id) references credit (id)
)
    comment 'Role Credit';

create index credit_id
    on rolecredit (credit_id);

create table teacherclassification
(
    id   int auto_increment comment 'Primary Key'
        primary key,
    name varchar(255) not null comment 'Name'
)
    comment 'Teacher Classification';

create table classroomevent
(
    id              int auto_increment comment 'Primary Key'
        primary key,
    appointmentDate date                                          not null comment 'Appointment Date',
    startTime       datetime                                      not null comment 'Start Time',
    endTime         datetime                                      not null comment 'End Time',
    applicant       varchar(255)                                  not null comment 'Applicant ID',
    classroom       int                                           not null comment 'Classroom ID',
    isMedia         tinyint(1)                                    not null comment 'Is Media',
    isComputer      tinyint(1)                                    not null comment 'Is Computer',
    isSound         tinyint(1)                                    not null comment 'Is Sound',
    aim             enum ('会议', '研讨', '团建', '讲座', '其他') not null comment 'Aim',
    events          text                                          null comment 'Events',
    approve        varchar(255)                                  null comment 'Approver ID',
    state           int                                           null comment 'State',
    constraint classroomevent_ibfk_1
        foreign key (classroom) references classroom (id),
    constraint classroomevent_ibfk_2
        foreign key (applicant) references userinfo (id)
)
    comment 'Classroom Event';

create index applicant
    on classroomevent (applicant);

create index approver
    on classroomevent (approver);

create index classroom
    on classroomevent (classroom);

create table classroomeventpresent
(
    event_id int          not null comment 'Event ID',
    user_id  varchar(255) not null comment 'User ID',
    primary key (event_id, user_id),
    constraint classroomeventpresent_ibfk_1
        foreign key (event_id) references classroomevent (id),
    constraint classroomeventpresent_ibfk_2
        foreign key (user_id) references userinfo (id)
)
    comment 'Classroom Event Present';

create index user_id
    on classroomeventpresent (user_id);

create table officehoureventpresent
(
    event_id int          not null comment 'Event ID',
    user_id  varchar(255) not null comment 'User ID',
    primary key (event_id, user_id),
    constraint officehoureventpresent_ibfk_1
        foreign key (event_id) references officehourevent (id),
    constraint officehoureventpresent_ibfk_2
        foreign key (user_id) references userinfo (id)
)
    comment 'Office Hour Event Present';

create index user_id
    on officehoureventpresent (user_id);

create table teachertimetable
(
    id              int auto_increment comment 'Primary Key'
        primary key,
    appointmentDate date         not null comment 'Appointment Date',
    startTime       datetime     not null comment 'Start Time',
    endTime         datetime     not null comment 'End Time',
    teacherid       varchar(255) not null comment 'Teacher ID',
    constraint teachertimetable_ibfk_1
        foreign key (teacherid) references userinfo (id)
)
    comment 'Teacher Time Table';

create index teacherid
    on teachertimetable (teacherid);

create table userclassification
(
    classification int comment 'Classification',
    user_id        varchar(255) not null comment 'User ID',
    constraint userclassification_ibfk_1
        foreign key (classification) references teacherclassification (id),
    constraint userclassification_ibfk_2
        foreign key (user_id) references userinfo (id),
    primary key (classification,user_id)
)
    comment 'User Classification';

create index user_id
    on userclassification (user_id);

create table userrole
(
    user_id varchar(255) not null comment 'User ID',
    role_id int          not null comment 'Role ID',
    primary key (user_id, role_id),
    constraint userrole_ibfk_1
        foreign key (user_id) references userinfo (id),
    constraint userrole_ibfk_2
        foreign key (role_id) references role (id)
)
    comment 'User Role';

create index role_id
    on userrole (role_id);


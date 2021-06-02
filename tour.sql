drop table if exists tourlog;
drop table if exists tour;


create table tour
(
    id          serial primary key,
    name        varchar(255)     not null,
    description varchar(10000)   not null,
    distance    double precision not null,
    start       varchar(255),
    "end"       varchar(255),
    imagePath   varchar(255)
);

create table tourlog
(
    id               serial primary key,
    tour             integer,
    datetime         timestamp,
    report           varchar(255),
    distance         double precision,
    /* save in seconds */
    duration         integer,
    rating           integer,
    /* 5 extra attributes */
    max_incline      double precision,
    average_speed    double precision,
    top_speed        double precision,
    weather          varchar(100),
    number_of_breaks integer,

    foreign key (tour) references tour (id) on delete cascade,
    check ( rating <= 10 and rating >= 1 )
);

insert into tour (name, description, distance, start, "end", imagePath)
VALUES ('Tour 1: France',
        'Die Tour de France [ˌtuʀdəˈfʀɑ̃ːs], auch Grande Boucle [gʀɑ̃dˈbukl] (französisch für Große Schleife) genannt, ist das bekannteste und wohl bedeutendste Straßenradrennen der Welt. Sie zählt neben dem Giro d’Italia und der Vuelta a España zu den Grand Tours.',
        75.0,
        'Mâcon, 71000, France',
        'Lyon, France',
        'https://zenduwork.com/wp-content/uploads/2017/04/routing-pointa-ppointb.png'),
       ('Tour 2: Germany',
        'Eine schöne längere Fahrradstrecke',
        53.5,
        'Kulmbach, Germany',
        'Bamberg, Germany',
        'https://zenduwork.com/wp-content/uploads/2017/04/routing-pointa-ppointb.png'),
       ('Tour 3: Austria',
        'Eine Strecke zum Entspannen 😌',
        20,
        'Kleingartenverein Unterer Prater, 1020 Wien, Österreich',
        '2401 Fischamend, Niederösterreich, Österreich',
        'https://zenduwork.com/wp-content/uploads/2017/04/routing-pointa-ppointb.png');

insert into tourlog (tour, datetime, report, distance, duration, rating, max_incline, average_speed, top_speed,
                     weather, number_of_breaks)
values (1, '2021-05-05 08:20:00', 'was a bit weary', 62.0, 60 * 60 * 10, 7, 25, 17.0, 24.0, 'overcast', 5),
       (3, '2021-04-24 10:00:00', 'great ride', 20.2, 60 * 60 * 2.5, 9, 7, 15.0, 20.0, 'sunny', 2);


select * from tour;
select * from tourlog;
select coalesce(null, null);

/*update tour set  name = COALESCE(NULL, name),
  description = COALESCE(NULL, description),
  distance = COALESCE(NULL, distance),
  start = COALESCE(NULL, start),
  "end" = COALESCE(NULL, "end"),
  imagePath = COALESCE('images/Tour-10-V9DXBXOBOVX2BPRPBFQ7.jpg', imagePath) where id = 10;*/
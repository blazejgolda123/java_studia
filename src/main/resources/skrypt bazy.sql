
CREATE TABLE zdarzenie (
    id CHAR(36) PRIMARY KEY,
    liczba_maszyn INT,
    data_eksperymentu TIMESTAMP
);

CREATE TABLE zadanie (
    id CHAR(36) PRIMARY KEY,
    waga_zadania INT,
    id_zdarzenia CHAR(36),
    FOREIGN KEY (id_zdarzenia) REFERENCES zdarzenie(id)
);


CREATE TABLE wykonanie (
    id CHAR(36) PRIMARY KEY,
    numer_maszyny INT,
    lp INT,
    id_zadania CHAR(36),
    FOREIGN KEY (id_zadania) REFERENCES zadanie(id)
);

select
	wyk.numer_maszyny,
	wyk.lp,
	zad.waga_zadania,
	zdarz.data_eksperymentu,
	zdarz.liczba_maszyn
from wykonanie wyk
join zadanie zad on zad.id = wyk.id_zadania
join zdarzenie zdarz on zdarz.id = zad.id_zdarzenia
order by zdarz.id, wyk.numer_maszyny, wyk.lp
;

delete from wykonanie;
delete from zadanie;
delete from zdarzenie;

--//https://localhost/phpmyadmin/
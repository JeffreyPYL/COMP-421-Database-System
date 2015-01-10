raw = LOAD '/user/hadoop/data2.csv' USING PigStorage(',') AS (date, type:chararray, parl:int, prov:chararray, riding:chararray, lastname:chararray, firstname:chararray, gender:chararray, occupation:chararray, party:chararray, votes:int, percent:double, elected:int);

fltrd = FILTER raw by votes >= 100;

winners = FILTER fltrd by elected == 1;

losers = FILTER fltrd by elected == 0;

elections = JOIN winners BY riding, losers by riding;

same = FILTER elections by winners::parl==losers::parl and winners::type == losers::type and winners::date==losers::date and winners::prov==losers::prov;

close = FILTER same by (winners::votes-losers::votes) <=10;

gen = foreach close generate winners::date, winners::parl, winners::riding,winners::lastname, winners::firstname, losers::lastname, losers::firstname,winners::votes-losers::votes;

dump gen;

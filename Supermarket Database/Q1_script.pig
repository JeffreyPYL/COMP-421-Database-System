raw = LOAD '/user/hadoop/data2.csv' USING PigStorage(',') AS (date, type:chararray, parl:int, prov:chararray, riding:chararray, lastname:chararray, firstname:chararray, gender:chararray, occupation:chararray, party:chararray, votes:int, percent:double, elected:int);
fltrd = FILTER raw by percent >= 60;
gen = foreach fltrd generate CONCAT(firstname, CONCAT(' ', lastname));
odred = DISTINCT gen;
dump results;

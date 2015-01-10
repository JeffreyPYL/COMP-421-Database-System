raw = LOAD '/user/hadoop/ngrams421.csv' USING PigStorage(',') AS (word:chararray, year:int, occurences:int);

slct = FILTER raw BY year==1990;

fltrd = GROUP slct BY year;

count = FOREACH fltrd GENERATE SUM(slct.occurences);

dump count;

raw = LOAD '/user/hadoop/data2.csv' USING PigStorage(',') AS (date, type:chararray, parl:int, prov:chararray, riding:chararray, lastname:chararray, firstname:chararray, gender:chararray, occupation:chararray, party:chararray, votes:int, percent:double, elected:int);		

parliament = GROUP raw BY parl;		
numberMPsParl = foreach parliament{
		generate group, COUNT(raw) AS number;
};	
parlCount = FOREACH numberMPsParl GENERATE group as parl, number;

party = GROUP raw BY (parl, party);
numberMPsParty = foreach party{
		generate group, COUNT(raw) AS number;
};
partyCount = FOREACH numberMPsParty GENERATE group.parl, group.party, number;

joinParlParty = JOIN parlCount BY parl, partyCount by parl;
answer = FOREACH joinParlParty GENERATE parlCount::parl, partyCount::party, partyCount::number, parlCount::number;

STORE answer INTO  'question4.csv' USING PigStorage(',');
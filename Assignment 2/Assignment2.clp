//1
SELECT Series.id\
FROM Series, Games\
WHERE (Series.id = Games.id) AND (((Games.goals1-Games.goals2)>=4) OR (Games.goals2-Games.goals1)<=-4)\
ORDER BY Series.id

//2
SELECT Series.round\
FROM Series, Games\
WHERE (Series.id = Games.id) AND Games.OT >= 1

//3
SELECT Series.id\
FROM Series, Games\
WHERE (Games.id = Series.id) NOT IN(SELECT Games.id
						FROM Games
						WHERE OT = 1)

//4
SELECT Series.id\
FROM Series, Games\
WHERE (Games.id = Series.id) \
AND ((Games.game != Games.game)\ 
AND ((Games.goals1 = Games.goals1)\ 
OR (Games.gaols2 = Games.goals2)))
NOT IN(SELECT Games.id
		FROM Games
		WHERE ((Games.game != Games.game)
		AND (Games.id = Games.id)
		AND((Games.goals1 != Games.goals1) OR (Games.goals2 != Games.goals2)))

//5
SELECT Series.id, Games.game, (Games.goals1 + Game.goals2)\
FROM Series, Games\
WHERE (Games.id = Series.id)\
IN (SELECT MIN(Games.goals1 + Games.goals2)\
FROM Games)




//8
CREATE VIEW Score(goals)\
AS SELECT avg(Games.gaols1+Games.goals2) AS goals\
FROM Games


//9
SELECT Series.id, Count(Games.game)\
FROM Series, Games\
WHERE Series.id = Games.id

//10
SELECT Series.id\
FROM Series\
WHERE (Series.games_won1 + Series.games_won_2)<7

//11
SELECT Series.id\
FROM Series\
WHERE ((Series.games_won1 + Series.games_won_2)<7)\
AND ((Series.team1 = "Montreal Canadiens") OR (Series.team2 = "Montreal Canadiens"))

//12
SELECT Series.id\
FROM Series, Games\
WHERE (Games.id = Series.id) AND Count(Games.OT) >=2


//13
SELECT *\
FROM Series\
WHERE Count(Series.game_won1 + Series.gmae_won2) >= IN (SELECT Count(game_won1 + game_won2)\
														FROM Series)
//14
CREATE VIEW Temp(id, team, games_won)\
AS SELECT Series.id AS id, Series.team1 AS team, Series.games_won1 AS games_won\
FROM Series

INSERT INTO Temp\
AS SELECT Series.id AS id, Series.team2 AS team, Series.games_won2 AS games_won\
FROM Series

//15


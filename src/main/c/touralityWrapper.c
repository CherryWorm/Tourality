/*
 * touralityWrapper.c
 *
 * Copyright (C) 2016 Pixelgaffer
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#include "catchsig.h"
#include "output.h"
#include "touralityWrapper.h"

#include <stdlib.h>
#include <string.h>
#include <unistd.h>

Player* parsePlayer (char *s)
{
	char *p = s;
	while (*p && (*p != ':'))
		p++;
	*p = 0;
	p++;
	
	Player *player = malloc(sizeof(Player));
	player->x = atoi(s);
	player->y = atoi(p);
	return player;
}

char* touralityMainLoop (Wrapper *w, TOURALITY_CALLBACK(callback))
{
	registerSignalCatcher(w);
	
	Grid grid;
	for (int i = 0; i < TOURALITY_COINS; i++)
		grid.coinsAvail[i] = 0;
	char *l = 0;
	while (strlen(l = readLine(w)) > 0)
	{
		char *line = trim(l);
		printf("received line: %s\n", line);
		// if the first char in the line is a 1, there will be a field
		char *p = line;
		int isField = *line == '1';
		*p++;
		if (isField)
		{
			int coinCount = 0;
			for (int x = 0; x < TOURALITY_GRID_SIZE; x++)
			{
				for (int y = 0; y < TOURALITY_GRID_SIZE; y++)
				{
					grid.fields[x][y] = *p++;
					if (grid.fields[x][y] == COIN)
					{
						grid.coinsX[coinCount] = x;
						grid.coinsY[coinCount] = y;
						grid.coinsAvail[coinCount] = 1;
						coinCount++;
					}
				}
			}
// 			printf("received field:\n");
// 			for (int y = 0; y < TOURALITY_GRID_SIZE; y++)
// 			{
// 				for (int x = 0; x < TOURALITY_GRID_SIZE; x++)
// 					printf("%c", (char)grid.fields[x][y]);
// 				printf("\n");
// 			}
		}
		else
			for (int i = 0; i < TOURALITY_COINS; i++)
				grid.coinsAvail[i] = 0;
		// split the remaining at ;
		Player *me = 0, *enemy = 0;
		int in=0;
		char *s = p, *t = p;
		while (*s)
		{
			while (*s && (*s != ';'))
				s++;
			*s = 0;
			
			Player *pl = parsePlayer(t);
			if (!me)
				me = pl;
			else if (!enemy)
				enemy = pl;
			else
			{
				grid.coinsX[in] = me->x;
				grid.coinsY[in] = me->y;
				grid.coinsAvail[in] = 1;
				in++;
				free(me);
				me = enemy;
				enemy = pl;
			}
			
			s++;
			t = s;
		}
		// if the input didn't contain a field, apply the coins
		if (!isField)
		{
			for (int x = 0; x < TOURALITY_GRID_SIZE; x++)
				for (int y = 0; y < TOURALITY_GRID_SIZE; y++)
					if (grid.fields[x][y] == COIN)
						grid.fields[x][y] = EMPTY;
			for (int i = 0; i < TOURALITY_COINS; i++)
				if (grid.coinsAvail[i])
					grid.fields[grid.coinsX[i]][grid.coinsY[i]] = COIN;
		}
		
		printf("players: me(%d|%d), enemy(%d|%d)\n", me->x, me->y, enemy->x, enemy->y);
		// call the ai
		Result *result = callback(me, enemy, &grid);
		printf("answer from the ai: %d\n", result->dir);
		// send the result
		char *answer = itos(result->dir);
		write(w->socketfd, answer, strlen(answer));
		free(answer);
		write(w->socketfd, ":", 1);
		if (result->output)
		{
			answer = escape(result->output);
			write(w->socketfd, answer, strlen(answer));
			free(answer);
		}
		write(w->socketfd, "\n", 1);
		
		// cleanup
		free(result);
		free(me);
		free(enemy);
		free(l);
	}
}

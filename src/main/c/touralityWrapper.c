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
	Grid grid;
	for (int i = 0; i < TOURALITY_COINS; i++)
		grid.coinsAvail[i] = 1;
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
			for (int x = 0; x < TOURALITY_GRID_SIZE; x++)
				for (int y = 0; y < TOURALITY_GRID_SIZE; y++)
					grid.fields[x][y] = *p++;
			printf("received field:\n");
			for (int x = 0; x < TOURALITY_GRID_SIZE; x++)
			{
				for (int y = 0; y < TOURALITY_GRID_SIZE; y++)
					printf("%c", (char)grid.fields[x][y]);
				printf("\n");
			}
		}
		// split the remaining at ;
		char *s = p;
		while (*s && (*s != ';'))
			s++;
		*s = 0;
		s++;
		// parse the players
		Player *me = parsePlayer(p);
		Player *enemy = parsePlayer(s);
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

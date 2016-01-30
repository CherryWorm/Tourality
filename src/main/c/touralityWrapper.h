/*
 * touralityWrapper.h
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
#ifndef TOURALITY_WRAPPER_H
#define TOURALITY_WRAPPER_H

#include "wrapper.h"

#include <stdlib.h>
#include <string.h>

/// The default tourality grid size.
#define TOURALITY_GRID_SIZE 20
/// The initial amount of coins.
#define TOURALITY_COINS 40

#ifdef __cplusplus
extern "C" {
#endif

enum _fieldType
{
	EMPTY=' ', COIN='+', WALL='#'
};
typedef enum _fieldType FieldType;

/**
 * Diese Klasse speichert ein Tourality-Grid.
 */
struct _grid
{
	FieldType fields[TOURALITY_GRID_SIZE][TOURALITY_GRID_SIZE];
	int coinsX[TOURALITY_COINS];
	int coinsY[TOURALITY_COINS];
	int coinsAvail[TOURALITY_COINS];
};
typedef struct _grid Grid;

/**
 * Diese Klasse speichert die für die KI sichtbaren Daten einer KI. Dies ist der Einsatz der KI der letzten Runde
 * sowie die Anzahl der von dieser KI gewonnenen Chips.
 */
struct _player
{
	/** Die Koordinaten des Spielers. */
	int x, y;
};
typedef struct _player Player;
typedef struct _player Spieler; // für die die lieber deutsch proggen

/**
 * Diese Methode parst den String `s` zu einem pointer auf eine `Player`-Struktur.
 */
Player* parsePlayer (char *s);

/**
 * Dieses Enum gibt die Richtung an, in die sich die KI bewegen soll.
 */
enum _direction
{
	UP=0, DOWN=2, LEFT=3, RIGHT=1, STAY=4 // nico benutzt java.lang.Enum.ordinal()
};
typedef enum _direction Direction;

/**
 * Diese Klasse repräsentiert das Ergebnis einer Runde. Dabei wird die Bewegungsrichtung der KI und die
 * Ausgabe der KI gespeichert.
 */
struct _result
{
	Direction dir;
	const char *output;
};
typedef struct _result Result;

#define TOURALITY_CALLBACK(name) Result* (* name ) (Player*, Player*, Grid*)

/**
 * Dies ist die MainLoop von Grooker. Sie wartet auf Daten vom Server, leitet diese an die eigentliche KI weiter, und
 * sendet diese Daten zurück.
 */
char* touralityMainLoop (Wrapper *w, TOURALITY_CALLBACK(callback));

#ifdef __cplusplus
}
#endif

#ifdef __cplusplus

#include <sstream>
#include <string>

/**
 * Dies ist die Mutter-Klasse von jeder Tourality-KI. Sie muss im Konstuktor genau ein Argument, `Wrapper*`,
 * entgegennehmen, und an diese Klasse weitergeben. Zudem muss die pure virtual Methode `move` überschrieben
 * werden. Diese Methode wird jede Runde aufgerufen. Zudem enthält diese Klasse eine Methode names `out`, die
 * ein `std::stringstream` zurückgibt, an das die KI ihre Ausgabe schicken kann. <b>Alles, was die KI an
 * `std::cout` sendet, geht verloren!!!</b>. Der Name dieser Klasse gehört als Argument an das `TOURALITY_MAIN`
 * Makro.
 */
class TouralityAi
{
public:
	explicit TouralityAi (Wrapper *wrapper)
		: w(wrapper)
	{
		if (!w)
			fprintf(stderr, "Warning: Wrapper is null!\n");
	}
	
	/**
	 * Wird jede Runde aufgerufen. Gibt die Richtung (`Direction`), in die sich die KI bewegt, zurück.
	 * @param me Die eigene KI.
	 * @param enemy Die gegnerische KI.
	 * @param grid Das Spielfeld.
	 * @return Die Richtung (`Direction`), in die sich die KI bewegt.
	 */
	virtual Direction move (Player *me, Player *enemy, Grid *grid) = 0;
	
	/**
	 * Gibt den Inhalt des aktuellen Output Buffers zurück und leert diesen.
	 */
	const char* readOutput ()
	{
		std::string outstr = _out.str();
		char *outbuf = (char*) malloc(outstr.length() + 1);
		strcpy(outbuf, outstr.data());
		outbuf[outstr.length()] = 0;
		_out.str("");
		return outbuf;
	}
	
protected:
	/**
	 * Lässt die KI mit Angabe eines Grundes abstürtzen.
	 * @param reason Der Grund, aus dem die KI abstürtzt.
	 */
	void crash (const char *reason) { __c_crash(w, reason); }
	/**
	 * Lässt die KI ohne Angabe eines Grundes aufgeben.
	 */
	void surrender () { __c_surrender(w); }
	
	/**
	 * Gibt ein `std::stringstream` zurück, an das die KI ihre Ausgaben schicken kann.
	 */
	std::stringstream& out () { return _out; }
	
private:
	Wrapper *w;
	std::stringstream _out;
};

#define TOURALITY_MAIN(clazzname) \
	clazzname *__ai = 0; \
	\
	Result* __callback (Player *me, Player *enemy, Grid *grid) \
	{ \
		Result *r = (Result*) malloc(sizeof(Result)); \
		r->dir = __ai->move(me, enemy, grid); \
		r->output = __ai->readOutput(); \
		return r; \
	} \
	\
	int main (int argc, char **argv) \
	{ \
		Wrapper *w = globalInit(argc, argv); \
		__ai = new clazzname (w); \
		touralityMainLoop(w, __callback); \
		globalCleanup(&w); \
		delete __ai; \
		return 0; \
	}

#else

#include "output.h"

#define TOURALITY_MAIN(callback) \
	Result* __callback (Player *me, Player *enemy, Grid *grid) \
	{ \
		Result *r = malloc(sizeof(Result)); \
		OutputBuffer *out = createBuffer(); \
		r->dir = callback(me, enemy, grid, out); \
		r->output = readBuffer(out, OB_RETURN_COPY); \
		destroyBuffer(out); \
		return r; \
	} \
	\
	int main (int argc, char **argv) \
	{ \
		Wrapper *w = globalInit(argc, argv); \
		touralityMainLoop(w, __callback); \
		globalCleanup(&w); \
		return 0; \
	}

#endif

#endif

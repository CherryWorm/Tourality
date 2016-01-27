// C Version

#include "touralityWrapper.h"

#include <time.h>
#include <unistd.h>

long long int seed = -1;

/// Diese Methode wird jede Runde aufgerufen. Player ist ein struct mit zwei int-Werten x und y.
/// Der return-Wert ist die Richtung, in die sich die KI bewegt. Zur Ausgabe stehen die folgenden
/// Funktionen zur Verfügung:
///  - append (out, "text"): Gibt "text" aus
///  - appendi(out, 123456): Gibt 123465 aus
///  - appendd(out, 123.45): Gibt 123.45 aus
Direction move (Player *me, Player *enemy, Grid *grid, OutputBuffer *out)
{
	// eine zufällige Bewegung machen
	if (seed == -1)
		seed = time(NULL) ^ getpid();
	srand(seed++);
	int r = rand() % 5;
	if (r == 0)
	{
		append(out, "going UP\n");
		return UP;
	}
	if (r == 1)
	{
		append(out, "going DOWN\n");
		return DOWN;
	}
	if (r == 2)
	{
		append(out, "going LEFT\n");
		return LEFT;
	}
	if (r == 3)
	{
		append(out, "going RIGHT\n");
		return RIGHT;
	}
	append(out, "STAYing here\n");
	return STAY;
}

TOURALITY_MAIN(move)

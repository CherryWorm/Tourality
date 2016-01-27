// C Version

#include "touralityWrapper.h"

/// Diese Methode wird jede Runde aufgerufen. Player ist ein struct mit zwei int-Werten x und y.
/// Der return-Wert ist die Richtung, in die sich die KI bewegt. Zur Ausgabe stehen die folgenden
/// Funktionen zur Verfügung:
///  - append (out, "text"): Gibt "text" aus
///  - appendi(out, 123456): Gibt 123465 aus
///  - appendd(out, 123.45): Gibt 123.45 aus
Direction move (Player *me, Player *enemy, Grid *grid, OutputBuffer *out)
{
	return STAY;
}

TOURALITY_MAIN(move)

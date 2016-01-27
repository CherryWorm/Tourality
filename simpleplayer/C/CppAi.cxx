// zum Nuzten von C++ bitte CAi.c löschen!!!
// C++ version:

/*

#include "touralityWrapper.h"

#include <ctime>

class Ai : public TouralityAi
{
public:
	explicit Ai (Wrapper *w) : TouralityAi(w) {}
	
	/// Diese Methode wird jede Runde aufgerufen. Player ist ein struct mit zwei int-Werten x und y.
	/// Der return-Wert ist die Richtung, in die sich die KI bewegt. Bitte out() statt cout/cerr zur
	/// Ausgabe benutzen.
	Direction move (Player *me, Player *enemy, Grid *grid)
	{
		// eine zufällige Bewegung machen
		srand(time(NULL));
		int r = rand() % 5;
		if (r == 0)
		{
			out() << "going UP" << std::endl;
			return UP;
		}
		if (r == 1)
		{
			out() << "going DOWN" << std::endl;
			return DOWN;
		}
		if (r == 2)
		{
			out() << "going LEFT" << std::endl;
			return LEFT;
		}
		if (r == 3)
		{
			out() << "going RIGHT" << std::endl;
			return RIGHT;
		}
		out() << "STAYing here" << std::endl;
		return STAY;
	}
};

TOURALITY_MAIN(Ai)

*/

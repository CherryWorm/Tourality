package org.pixelgaffer.turnierserver.tourality;

public enum Feld
{
	FREI(' '), COIN('+'), STEIN('#');
	
	public static Feld fromRepr (char repr)
	{
		switch (repr) {
			case ' ':
				return FREI;
			case '+':
				return COIN;
			case '#':
				return STEIN;
			default:
				return null;
		}
	}
	
	private Feld (char repr)
	{
		_repr = repr;
	}
	
	private char _repr;
	
	public char repr ()
	{
		return _repr;
	}
}

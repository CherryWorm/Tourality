/*
 * TouralityAi.java
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
package org.pixelgaffer.turnierserver.tourality.ai;

import java.awt.Point;

import org.pixelgaffer.turnierserver.ailibrary.Ai;
import org.pixelgaffer.turnierserver.tourality.Feld;
import org.pixelgaffer.turnierserver.tourality.TouralityResponse;
import org.pixelgaffer.turnierserver.tourality.TouralityUpdate;

/**
 * Dies ist die Mutterklasse aller Tourality KIs.
 * 
 * Jede KI muss diese Klasse erweitern. Nach der Instantiierung muss die start() methode aufgerufen werden, damit die KI anfängt, auf Nachrichten von der Spiellogik zu hören.
 * 
 * Rufe surrender() auf um ihne Grund aufzugeben
 * Rufe crash(Throwable t) oder crash(String reason) auf, um zu signalisieren, dass es einen crash gab. Dies hat den gleichen Effekt wie surrender(), nur dass du die Fehlermeldung auf der Webseite ausgegeben bekommst.
 */
public abstract class TouralityAi extends Ai {
	
	/**
	 * Dies instantiiert die KI. Vergiss nicht start() aufzurufen!
	 * 
	 * @param args Die in main(String[] args) übergebenen Kommandzeilenargumente
	 */
	public TouralityAi(String[] args) {
		super(args);
	}
	
	private Feld[][] field = new Feld[20][20];
	
	@Override
	protected String update(String answer) {
		TouralityUpdate update = new TouralityUpdate(answer);
		
		if(update.field != null)
			for(int i = 0; i < update.field.length; i++)
				for(int j = 0; j < update.field.length; j++)
					field[i][j] = update.field[i][j];
		else  {
			for(int i = 0; i < field.length; i++)
				for(int j = 0; j < field.length; j++)
					if(field[i][j] == Feld.COIN)
						field[i][j] = Feld.FREI;
			
			for(Point coin : update.coins)
				field[coin.x][coin.y] = Feld.COIN;
		}
		
		
		TouralityResponse response = new TouralityResponse();
		response.dir = bewegen(update.position, update.enemyPosition, field).ordinal();
		return response.toString();
	}
	
	/**
	 * In dieser Methode führst du deinen Zug aus.
	 * 
	 * @param du Deine Position
	 * @param gegner Die Position des Gegners
	 * @param spielfeld Das Spielfeld (20x20)
	 * @return Deinen Zug
	 */
	public abstract Richtung bewegen(Point du, Point gegner, Feld[][] spielfeld);
	
}

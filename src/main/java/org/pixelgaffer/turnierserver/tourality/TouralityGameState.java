/*
 * TouralityGameState.java
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
package org.pixelgaffer.turnierserver.tourality;

import java.awt.Point;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.GameState;

public class TouralityGameState implements GameState<TouralityUpdate, TouralityResponse> {
	
	private static Random random = new Random();
	
	public Point[] pos;
	public Feld[][] field;
	public int[] score;
	
	public List<Point> coins = new LinkedList<>();
	public String[] output;
	private boolean firstUpdate = true;
	private int fieldId;
	
	public TouralityGameState() {
		fieldId = random.nextInt(2) + 1;
		reset();
		output = new String[2];
		score = new int[2];
	}
	
	@Override
	public TouralityUpdate getChanges(Ai ai) {
		int otherIndex = ai.getIndex() == 0 ? 1 : 0;
		TouralityUpdate update = new TouralityUpdate();
		
		if (firstUpdate)
		{
			update.field = field;
			firstUpdate = false;
		}
		
		update.coins = coins;
		update.position = pos[ai.getIndex()];
		update.enemyPosition = pos[otherIndex];
		
		return update;
	}
	
	@Override
	public void clearChanges(Ai ai) {
		firstUpdate = false;
		output[0] = "";
		output[1] = "";
	}
	
	@Override
	public void applyChanges(TouralityResponse response, Ai ai) {
		output[ai.getIndex()] = response.output;
		
		int newX = pos[ai.getIndex()].x, newY = pos[ai.getIndex()].y;
		
		switch (response.dir) {
			case 0:
				newY--;
				break;
			case 1:
				newX++;
				break;
			case 2:
				newY++;
				break;
			case 3:
				newX--;
				break;
		}
		
		if (newX >= 0 && newY >= 0 && newX < 20 && newY < 20 && field[newX][newY] != Feld.STEIN) {
			pos[ai.getIndex()].x = newX;
			pos[ai.getIndex()].y = newY;
			if (field[newX][newY] == Feld.COIN) {
				field[newX][newY] = Feld.FREI;
				coins.remove(new Point(newX, newY));
				score[ai.getIndex()]++;
			}
		}
		
	}
	
	@Override
	public void applyChanges(TouralityUpdate changes) {
		pos[0] = changes.position;
		pos[1] = changes.enemyPosition;
		if (changes.field != null) {
			field = changes.field;
		}
		coins = changes.coins;
	}

	public void reset() {
		pos = new Point[2];
		pos[0] = new Point();
		pos[1] = new Point();
		field = new Feld[20][20];
		
		try(InputStreamReader in = new InputStreamReader(getClass().getResourceAsStream(fieldId + ".trlt"))) {
			for(int y = 0; y < 20; y++) {
				for(int x = 0; x < 20; x++) {
					char c = (char) in.read();
					if(Character.isDigit(c)) {
						pos[Character.getNumericValue(c) - 1] = new Point(x, y);
						field[x][y] = Feld.FREI;
					}
					else
						field[x][y] = Feld.fromRepr(c);
					if (c == Feld.COIN.repr())
						coins.add(new Point(x, y));
				}
				in.skip(1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

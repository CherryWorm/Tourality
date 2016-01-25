/*
 * GrokerGameState.java
 *
 * Copyright (C) 2015 Pixelgaffer
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

import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.GameState;

public class TouralityGameState implements GameState<TouralityUpdate, TouralityResponse> {
	
	public Point[] pos;
	public int[][] field;
	public int[] score;
	
	public List<Point> coins = new LinkedList<>();
	public String[] output;
	private boolean firstUpdate;
	
	public TouralityGameState() {
		pos = new Point[2];
		pos[0] = new Point();
		pos[1] = new Point();
		field = new int[20][20];
		output = new String[2];
		score = new int[2];
		
		try(InputStreamReader in = new InputStreamReader(getClass().getResourceAsStream("1.trlt"))) {
			for(int i = 0; i < 20; i++) {
				for(int j = 0; j < 20; j++) {
					char c = (char) in.read();
					if(Character.isDigit(c)) {
						pos[Character.getNumericValue(c) - 1] = new Point(i, j);
					}
					else if(c == '#') {
						field[i][j] = 2;
					}
					else if(c == '+') {
						field[i][j] = 1;
					}
					else {
						field[i][j] = 0;
					}
				}
				in.skip(1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public TouralityUpdate getChanges(Ai ai) {
		int otherIndex = ai.getIndex() == 0 ? 1 : 0;
		TouralityUpdate update = new TouralityUpdate();
		
		if (firstUpdate) update.field = field;
		
		update.coins = coins;
		update.position = pos[ai.getIndex()];
		update.enemyPosition = pos[otherIndex];
		
		return update;
	}
	
	@Override
	public void clearChanges(Ai ai) {
		coins.clear();
		firstUpdate = false;
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
		
		if (newX >= 0 && newY >= 0 && newX < 20 && newY < 20 && field[newX][newY] != 2) {
			pos[ai.getIndex()].x = newX;
			pos[ai.getIndex()].y = newY;
			if (field[newX][newY] == 1) {
				field[newX][newY] = 0;
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
	
}

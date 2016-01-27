/*
 * GrokerRenderData.java
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
package org.pixelgaffer.turnierserver.tourality.logic;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;

import org.pixelgaffer.turnierserver.tourality.Feld;
import org.pixelgaffer.turnierserver.tourality.TouralityGameState;

public class TouralityRenderData {
	
	
	public TouralityRenderData(TouralityGameState state, String name1, String name2) {
		this(state, name1, name2, false);
	}
	
	public TouralityRenderData(TouralityGameState state, String name1, String name2, boolean first) {
		score = new HashMap<>();
		score.put(name1, state.score[0]);
		score.put(name2, state.score[1]);
		
		position = new HashMap<>();
		position.put(name1, state.pos[0]);
		position.put(name2, state.pos[1]);
		
		output = new HashMap<>();
		output.put(name1, state.output[0]);
		output.put(name2, state.output[1]);
		
		if (first) {
			field = state.field;
		}
		coins = state.coins;
	}
	
	
	public HashMap<String, Integer> score;
	public HashMap<String, Point> position;
	public HashMap<String, String> output;
	public List<Point> coins;
	public Feld[][] field;
	
}

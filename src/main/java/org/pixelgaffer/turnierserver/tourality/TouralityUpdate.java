/*
 * TouralityUpdate.java
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
import java.util.LinkedList;
import java.util.List;

public class TouralityUpdate {
	
	public Feld[][] field;
	public List<Point> coins;
	public Point position;
	public Point enemyPosition;
	
	
	public TouralityUpdate(String s) {
		boolean isField = s.charAt(0) == '1';
		int pos = 1;
		
		if(isField) {
			field = new Feld[20][20];
			for(int i = 0; i < 20; i++)
				for(int j = 0; j < 20; j++)
					field[i][j] = Feld.fromRepr(s.charAt(pos++));
		}
		
		String[] pointStrings = s.substring(pos).split(";");
		List<Point> points = new LinkedList<>();
		
		for(String point : pointStrings) {
			Point p = new Point();
			String[] split = point.split(":");
			p.x = Integer.parseInt(split[0]);
			p.y = Integer.parseInt(split[1]);
			points.add(p);
		}
		
		for(int i = 0; i < points.size() - 2; i++) {
			coins.add(points.get(i));
		}
		position = coins.get(coins.size() - 2);
		enemyPosition = coins.get(coins.size() - 1);
	}
	
	
	public TouralityUpdate() {}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		System.out.println("TouralityUpdate.toString(): field is " + (field!=null ? "not " : "") + "null; coins: " + coins.size()
			+ "; position: " + position + "; enemy: " + enemyPosition);
		builder.append(field == null ? 0 : 1);
		if (field != null) {
			for(Feld[] row : field)
			{
				for(Feld i : row)
				{
					if (i == null)
						System.err.println("achtung NULL !!!");
					builder.append(i.repr());
				}
			}
		} else {		
			for (Point coin : coins) {
				append(builder, coin);
				builder.append(';');
			}
		}
		
		append(builder, position);
		builder.append(";");
		append(builder, enemyPosition);
		return builder.toString();
	}
	
	private void append(StringBuilder b, Point p) {
		b.append(p.x);
		b.append(':');
		b.append(p.y);
	}
	
}

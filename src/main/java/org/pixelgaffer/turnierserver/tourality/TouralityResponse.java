/*
 * TouralityResponse.java
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

public class TouralityResponse {
	
	public int dir;
	public String output;

	public TouralityResponse(String s) {
		int split = s.indexOf(':');
		dir = Integer.parseInt(s.substring(0, split));
		output = s.substring(split + 1).replace("\\\\", "\\").replace("\\n", "\n");
	}
	
	
	public TouralityResponse() {
	}
	
	@Override
	public String toString() {
		return Integer.toString(dir);
	}
	
}

/*
 * TouralityLogic.java
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
package org.pixelgaffer.turnierserver.tourality.logic;

import java.text.ParseException;

import org.pixelgaffer.turnierserver.gamelogic.AlternatingTurnBasedGameLogic;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.GameState;
import org.pixelgaffer.turnierserver.tourality.TouralityGameState;
import org.pixelgaffer.turnierserver.tourality.TouralityResponse;

public class TouralityLogic extends AlternatingTurnBasedGameLogic<TouralityAiObject, TouralityResponse> {
	
	private boolean first = true;
	
	@Override
	protected Object update() {
		TouralityGameState state = (TouralityGameState) gamestate;
		
		progress = (80 - state.coins.size()) / 80.0;
		display = "Es wurden schon " + (80 - state.coins.size()) + " von 80 coins aufgesammelt";
		
		for (Ai ai : game.getAis())
			if(!getUserObject(ai).lost)
				getUserObject(ai).score = state.score[ai.getIndex()];
		
		if(state.coins.isEmpty()) {
			sendRenderData(new TouralityRenderData(state, game.getAis().get(0).getId(), game.getAis().get(1).getId()));
			if(!first) {
				endGame("Alle Coins wurden aufgesammelt!");
			}
			else {
				first = false;
				state.reset();
			}
		}
		
		return new TouralityRenderData(state, game.getAis().get(0).getId(), game.getAis().get(1).getId());
	}

	@Override
	protected GameState<?, TouralityResponse> createGameState() {
		return new TouralityGameState();
	}

	@Override
	protected void setup() {
		for (Ai ai : game.getAis()) {
			getUserObject(ai).mikrosLeft = 4000000;
		}
		maxTurns = -1;
	}

	@Override
	public void lost(Ai ai) {
		getUserObject(ai).score = ((TouralityGameState) gamestate).score[ai.getIndex()];
		if(game.getAis().stream().allMatch((Ai a) -> a.getObject().lost)) {
			endGame("Alles KIs sind abgestürzt/haben aufgegeben!");
		}
	}

	@Override
	protected TouralityAiObject createUserObject(Ai ai) {
		return new TouralityAiObject();
	}

	@Override
	protected void gameFinished() {

	}
	
	@Override
	protected void sendFirstRenderData() {
		progress = 0;
		display = "Spiel gestartet";
		sendRenderData(new TouralityRenderData((TouralityGameState) gamestate, game.getAis().get(0).getId(), game.getAis().get(1).getId(), true));
	}

	@Override
	public float aiTimeout() {
		return 20;
	}

	@Override
	protected TouralityResponse parse(String string) throws ParseException {
		try {
			return new TouralityResponse(string);
		} catch(Exception e) {
			e.printStackTrace();
			throw new ParseException(string, -1);
		}
	}

}

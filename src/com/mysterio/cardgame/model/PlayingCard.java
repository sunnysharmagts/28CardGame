package com.mysterio.cardgame.model;

import java.util.Random;

import com.mysterio.cardgame.CardGameApplication;
import com.mysterio.cardgame.model.Card.Numbers;
import com.mysterio.cardgame.model.Card.Player;

public class PlayingCard {	
	
	private boolean mIsVisible;
	
	/**
	 *@param visibility boolean
	 */
	public PlayingCard(final boolean visibility){
		mIsVisible = visibility;
	}

	/**
	 * This returns the whether the cards will be visible to you or not
	 * @return mIsVisible
	 */
	public boolean isVisible() {
		return mIsVisible;
	}
	
	/**
	 * 
	 * @return cardList ArrayList<Card>
	 */
	public static final void shuffle(Level level){
		
		/*
		 * return a list of card(8)
		 * check whether the pack contains less than 3 jacks
		 * Check whether the card has already been given to other player or added to the deck 
		 */
		if(level == null){
			level = Level.Easy;
		}
		int counter = 0;
		final Card.Player[] players = Card.Player.values();
		final Random random = new Random();
		Card.Player player = Card.Player.FIRST;
		while(CardGameApplication.deckSize() != 32){
			final int randomValue = random.nextInt(33);
			
			if(randomValue != 0){
				final Card card = CardGameApplication.DECK.get(randomValue);
				if(card != null && !card.getCardStatus()){
					int size = CardGameApplication.deckSize();
					if(size > 0 && (size % 8) == 0){
						player = players[++counter];
					}
					card.setPlayer(player);
					card.setCardStatus(true);
					CardGameApplication.add(card, player);
				}	
			}
		}
		switch (level) {
		case Easy:
			CardGameApplication.sort(Player.FOURTH);
			break;
		
		case Hard:
			CardGameApplication.sort(null);
			break;

		default:
			break;
		}
	}
}

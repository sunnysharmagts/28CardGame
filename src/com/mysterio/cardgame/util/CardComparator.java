package com.mysterio.cardgame.util;

import java.util.Comparator;

import com.mysterio.cardgame.model.Card;

public class CardComparator implements Comparator<Card> {

	private String key;
	
	public CardComparator() {
		this(null);
	}
	
	public CardComparator(final String map){
		this.key = map;
	}
	
	@Override
	public int compare(final Card rightHandCard, final Card leftHandCard) {
		if (key == null) {
			if (rightHandCard.getSuit().getID() > leftHandCard.getSuit()
					.getID()) {
				return -1;
			} else if (rightHandCard.getSuit().getID() < leftHandCard.getSuit()
					.getID()) {
				return 1;
			}
			return 0;
		}
		else{
			return rightHandCard.getPlayer().name().compareTo(leftHandCard.getPlayer().name());
		}
	}
}
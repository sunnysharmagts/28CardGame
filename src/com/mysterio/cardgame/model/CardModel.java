package com.mysterio.cardgame.model;

import java.util.List;

public class CardModel {

	final List<Card> mCardList;
	
	public CardModel(final List<Card> cardList){
		mCardList = cardList;
	}
	
	public List<Card> getList(){
		return mCardList;
	}
}

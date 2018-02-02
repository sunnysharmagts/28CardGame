package com.mysterio.cardgame.model;

import java.util.ArrayList;
import java.util.List;

import com.mysterio.cardgame.util.CardUtils;

public class PlayedCard {

	private final List<Card> mTeam1List;
	private final List<Card> mTeam2List;
	private int m1stTeamScore;
	private int m2ndTeamScore;
	
	public PlayedCard(){
		mTeam1List = new ArrayList<Card>();
		mTeam2List = new ArrayList<Card>();
	}
	
	public void add(final Card card){
		//Check the player and add it to the team
		if(card != null){
			switch (card.getPlayer()) {
			case FIRST:
			case THIRD:
				mTeam1List.add(card);
				m1stTeamScore = CardUtils.getRate(card);
				break;
				
			case SECOND:
			case FOURTH:
				mTeam2List.add(card);
				m1stTeamScore = CardUtils.getRate(card);
				break;

			default:
				break;
			}
		}		
	}
	
	public int get1stTeamScore(){
		return m1stTeamScore;
	}
	
	public int get2ndTeamScore() {
		return m2ndTeamScore;
	}	
}

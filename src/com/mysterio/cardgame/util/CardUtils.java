package com.mysterio.cardgame.util;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.mysterio.cardgame.CardGameApplication;
import com.mysterio.cardgame.model.Card;
import com.mysterio.cardgame.model.Card.Numbers;
import com.mysterio.cardgame.model.Card.Player;
import com.mysterio.cardgame.model.Card.Suit;
import com.mysterio.cardgame.model.Card.TEAM;

public class CardUtils {
	
	private static final String LOG_TAG = "CardUtils";
	
	public static final Player getPlayerWithKQ(final Player player, final Suit suit){
		final List<Card> list = CardGameApplication.getDeckCards(player);
		for(Card card : list){
			if(card.getNumber().equals(Numbers.KING) && card.getSuit().equals(suit)){
				if(card.getNumber().equals(Numbers.QUEEN) && card.getSuit().equals(suit)){
					return player;
				}
			}
		}
		return null;
	}

	public static final int getDrawable(final Context context, final Card card){
		int drawable = 0;
		if(card != null){
			final StringBuilder stringBuilder = new StringBuilder(card.getSuit().name().toLowerCase());
			stringBuilder.append(card.getNumber().getID());
			drawable = context.getResources().getIdentifier(stringBuilder.toString(), "drawable", context.getPackageName());
		}
		return drawable;
	}
	
	public static final int getRate(final Card card){
		int value = 0;
		if(card != null){
			final Numbers number = card.getNumber();
			switch (number) {
			case ACE:
			case TEN:
				value = 1;
				break;
			case JACK:	
				value = 3;
				break;
			case NINE:
				value = 2;
				break;
			case KING:
				value = -1;
				break;
			case QUEEN:
				value = -2;
				break;
			case EIGHT:
				value = -3;				
				break;
			case SEVEN:
				value = -4;				
				break;				
			default:
				value = 0;
				break;
			}
		}
		return value;
	}
	
	public static final int getPoints(final List<Card> cardList){
		int points = 0;
		for(Card card : cardList){
			final int point = getRate(card);
			if(point > 0){
				points += point; 
			}		
		}
		return points;
	}
	
	public static int getBiddingRate(final int weight, final int points){
		int value = 0;
		switch (weight) {
		case 2: {
			switch (points) {
			case 2:
				value = 16;
				break;
				
			case 3:
				value = 17;
				break;
				
			case 4:
				value = 18;
				break;
				
			case 5:
				value = 19;
				break;

			default:
				value = 15;
				break;
			}
		}
			break;
			
		case 3:
			switch (points) {
			case 1:
				value = 16;
				break;
				
			case 2:
				value = 17;
				break;
				
			case 3:
				value = 18;
				break;
				
			case 4:
				value = 18;
				break;
				
			case 5:
				value = 19;
				break;
				
			case 6:
				value = 20;
				break;

			default:
				value = 15;
				break;
			}			
			break;
			
		case 4:
			switch (points) {
			case 2:
				value = 17;
				break;
				
			case 3:
				value = 19;
				break;
				
			case 4:
				value = 20;
				break;
				
			case 5:
				value = 21;
				break;
			
			case 6:
				value = 21;
				break;
				
			case 7:
				value = 22;
				break;				

			default:
				value = 18;
				break;
			}			
			break;
			
		default:
			value = 15;	//Means pass
			break;
		}
		return value;
	}
	
	public static Card.Numbers getHighestAvailCard(final List<Card> cardList){
		final Card.Numbers[] numbers = {Numbers.JACK, Numbers.NINE, Numbers.ACE, Numbers.TEN, Numbers.KING, Numbers.QUEEN, Numbers.EIGHT, Numbers.SEVEN};
		for (Numbers number : numbers) {
			if(!cardList.contains(number)){
				return number;
			}
		}
		return null;
	}
	
	/**
	 * Check who won in this hand
	 * The checking will be done based on the played suit. points and also based on color(NOTE : only if the color is open)
	 * 
	 * @param SuitHand The suit for which the players are playing
	 * @return highestPointCard Card 
	 */	
	public static final Card checkWhoIsWinning(final List<Card> cardList, final Suit suitHand, final boolean isTrumpRevealed, final Suit trumpSuit){
		Card highestPointCard = null;
		int previousRate = -4;
		Log.d(LOG_TAG, "The suitHand is : "+suitHand);
		boolean flag = true;
		Log.d(LOG_TAG, "Is trump card revealed : "+isTrumpRevealed + " the trump card is : "+trumpSuit);
		for(final Card card : cardList){
			Log.d(LOG_TAG, "The played card is : "+card);
			final int currentRate = getRate(card);
			if(isTrumpRevealed && trumpSuit.equals(card.getSuit())){
				if(highestPointCard != null && highestPointCard.getSuit().equals(trumpSuit)){
					if(currentRate >= previousRate){
						previousRate = currentRate;
						highestPointCard = card;
					}
				}
				else{
					highestPointCard = card;
					previousRate = currentRate;
				}	
				flag = false;
			}
			else if(flag && suitHand.equals(card.getSuit())){
				if(currentRate >= previousRate){
					previousRate = currentRate;
					highestPointCard = card;
				}
			}
		}
		return highestPointCard;
	}
	
	/**
	 * 
	 */
	public static final Card getHighPointCard(final List<Card> cardList, final Suit playedSuit, final Suit trumpSuit, final boolean isTrumpRevealed){
		Card highestPointCard = null;
		int previousRate = -4;
		Log.d(LOG_TAG, "High point cards : "+cardList);
		for (final Card card : cardList) {
			final int currentRate = getRate(card);

			/*
			 * In case if the current point card to be played is not JACK and is
			 * not a trump card then play it. else play some other card
			 */
			if (!isTrumpRevealed) {
				if (currentRate != 3 && currentRate >= previousRate) {
					previousRate = currentRate;
					highestPointCard = card;
				}
			}
			else{
				if((!card.getSuit().equals(trumpSuit)) && (currentRate != 3) && (currentRate >= previousRate)){
					previousRate = currentRate;
					highestPointCard = card;					
				}
			}
		}
		//Then the last card must be jack most probably
		if(highestPointCard == null){
			highestPointCard = cardList.get(0);
		}
		Log.d(LOG_TAG, "highestPointCard : "+highestPointCard);
		return highestPointCard;
	}
	
	public static final Card playTrumpCard(final List<Card> cardList, final Suit trumpSuit, final Card checkTheWinningSuit){
		Card trumpCard = null;
		int previousRate = -4;
		Card jackTrumpCard = null;
		Log.d(LOG_TAG, "The list for trump card : "+cardList);
		for(final Card card : cardList){
			if(trumpSuit.equals(card.getSuit())){
				Log.d(LOG_TAG, "The trump available is : "+card);
				final int currentRate = getRate(card);
				
				/*Check if the opponent who is winning has already opened the trump card and
				 * has played the trump. If played then find the card greater than his card
				 * else play another card
				 */
				if(trumpSuit.equals(checkTheWinningSuit.getSuit())){
					if(currentRate != 3 && currentRate > getRate(checkTheWinningSuit)){
						trumpCard = card;
						break;
					}
				}
				else if(currentRate != 3 && currentRate >= previousRate){
					previousRate = currentRate;
					trumpCard = card;
				}				
				else if(currentRate == 3){
					jackTrumpCard = card;
				}
			}
		}
		if(trumpCard == null){
			trumpCard = jackTrumpCard;
		}
		return trumpCard;
	}
	
	public static final Card getLowPointCard(final List<Card> cardList){
		Card lowestPointCard = null;
		int previousRate = 3;
		for(final Card card : cardList){
			Log.d(LOG_TAG, "lowestPointCard : "+card);
				final int currentRate = getRate(card);
				if(currentRate <= previousRate){
					previousRate = currentRate;
					lowestPointCard = card;
				}
		}
		Log.d(LOG_TAG, "lowestPointCard without suit : "+lowestPointCard);
		return lowestPointCard;
	}
	
	public static final Card getLowPointCard(final List<Card> cardList, final Suit suitHand){
		Card lowestPointCard = null;
		Card otherLowestPointCard = null;
		int previousRate = 3;
		for(final Card card : cardList){
			//if(suitHand.equals(card.getSuit())){
				final int currentRate = getRate(card);
				if(currentRate <= previousRate){
					previousRate = currentRate;
					if(suitHand.equals(card.getSuit())){					
						lowestPointCard = card;
					}
					else{
						otherLowestPointCard = card;
					}
				}
			//}
		}
		if(lowestPointCard == null){
			return otherLowestPointCard;
		}
		Log.d(LOG_TAG, "lowestPointCard : "+lowestPointCard);
		return lowestPointCard;
	}	
	
	public static final boolean isSameTeam(final Player winningPlayer, final Player player){
		TEAM teamOfWinningPlayer = null;
		TEAM playerToPlay = null;
		switch (winningPlayer) {
		case FIRST:
		case THIRD:
			teamOfWinningPlayer = TEAM.TEAM_A;
			break;
			
		case SECOND:
		case FOURTH:	
			teamOfWinningPlayer = TEAM.TEAM_B;
			break;
			
		default:
			break;
		}
		
		switch (player) {
		case FIRST:
		case THIRD:
			playerToPlay = TEAM.TEAM_A;
			break;
			
		case SECOND:
		case FOURTH:	
			playerToPlay = TEAM.TEAM_B;
			break;
			
		default:
			break;
		}
		return teamOfWinningPlayer == playerToPlay;
	}
}

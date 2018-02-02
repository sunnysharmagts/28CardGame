package com.mysterio.cardgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.util.Log;

import com.mysterio.cardgame.model.Card;
import com.mysterio.cardgame.model.Card.Numbers;
import com.mysterio.cardgame.model.Card.Player;
import com.mysterio.cardgame.model.Card.Suit;
import com.mysterio.cardgame.model.PlayedCard;
import com.mysterio.cardgame.util.CardComparator;

public class CardGameApplication extends Application {

	private static final String LOG_TAG = "CardGameApp";
	public static final Map<Integer, Card> DECK = new HashMap<Integer, Card>();
	private static List<Card> mFirstPlayerList;
	private static List<Card> mSecondPlayerList;
	private static List<Card> mThirdPlayerList;
	private static List<Card> mFourthPlayerList;
	private static PlayedCard mPlayedCard;

	@Override
	public void onCreate() {
		Log.d(LOG_TAG, "onCreate() called ");
		init();
		populateDeck();
		super.onCreate();
	}

	private void init() {
		mFirstPlayerList = new ArrayList<Card>();
		mSecondPlayerList = new ArrayList<Card>();
		mThirdPlayerList = new ArrayList<Card>();
		mFourthPlayerList = new ArrayList<Card>();
		mPlayedCard = new PlayedCard();
	}

	public PlayedCard getmPlayedCard() {
		return mPlayedCard;
	}

	public void setPlayedCard(final PlayedCard playedCard) {
		mPlayedCard = playedCard;
	}

	private static final void populateDeck() {
		DECK.clear();
		DECK.put(1, new Card(Suit.CLUB, Numbers.SEVEN));
		DECK.put(2, new Card(Suit.CLUB, Numbers.EIGHT));
		DECK.put(3, new Card(Suit.CLUB, Numbers.NINE));
		DECK.put(4, new Card(Suit.CLUB, Numbers.TEN));
		DECK.put(5, new Card(Suit.CLUB, Numbers.KING));
		DECK.put(6, new Card(Suit.CLUB, Numbers.QUEEN));
		DECK.put(7, new Card(Suit.CLUB, Numbers.ACE));
		DECK.put(8, new Card(Suit.CLUB, Numbers.JACK));

		DECK.put(9, new Card(Suit.DIAMOND, Numbers.SEVEN));
		DECK.put(10, new Card(Suit.DIAMOND, Numbers.EIGHT));
		DECK.put(11, new Card(Suit.DIAMOND, Numbers.NINE));
		DECK.put(12, new Card(Suit.DIAMOND, Numbers.TEN));
		DECK.put(13, new Card(Suit.DIAMOND, Numbers.KING));
		DECK.put(14, new Card(Suit.DIAMOND, Numbers.QUEEN));
		DECK.put(15, new Card(Suit.DIAMOND, Numbers.ACE));
		DECK.put(16, new Card(Suit.DIAMOND, Numbers.JACK));

		DECK.put(17, new Card(Suit.HEARTS, Numbers.SEVEN));
		DECK.put(18, new Card(Suit.HEARTS, Numbers.EIGHT));
		DECK.put(19, new Card(Suit.HEARTS, Numbers.NINE));
		DECK.put(20, new Card(Suit.HEARTS, Numbers.TEN));
		DECK.put(21, new Card(Suit.HEARTS, Numbers.KING));
		DECK.put(22, new Card(Suit.HEARTS, Numbers.QUEEN));
		DECK.put(23, new Card(Suit.HEARTS, Numbers.ACE));
		DECK.put(24, new Card(Suit.HEARTS, Numbers.JACK));

		DECK.put(25, new Card(Suit.SPADES, Numbers.SEVEN));
		DECK.put(26, new Card(Suit.SPADES, Numbers.EIGHT));
		DECK.put(27, new Card(Suit.SPADES, Numbers.NINE));
		DECK.put(28, new Card(Suit.SPADES, Numbers.TEN));
		DECK.put(29, new Card(Suit.SPADES, Numbers.KING));
		DECK.put(30, new Card(Suit.SPADES, Numbers.QUEEN));
		DECK.put(31, new Card(Suit.SPADES, Numbers.ACE));
		DECK.put(32, new Card(Suit.SPADES, Numbers.JACK));
	}

	/**
	 * NOTE: The player shouldn't be null nor the card
	 * 
	 * @param card
	 *            Card
	 * @param player
	 *            Player
	 */
	public static void add(final Card card, final Player player) {
		switch (player) {
		case FIRST:
			mFirstPlayerList.add(card);
			break;

		case SECOND:
			mSecondPlayerList.add(card);
			break;

		case THIRD:
			mThirdPlayerList.add(card);
			break;

		case FOURTH:
			mFourthPlayerList.add(card);
			break;

		default:
			break;
		}
	}

	public static List<Card> getDeckCards(final Player player) {
		switch (player) {
		case FIRST:
			return mFirstPlayerList;

		case SECOND:
			return mSecondPlayerList;

		case THIRD:
			return mThirdPlayerList;

		case FOURTH:
			return mFourthPlayerList;

		default:
			return null;
		}
	}
	
	public static final void remove(final Player player, final Card card){
		switch (player) {
		case FIRST:
			mFirstPlayerList.remove(card);

		case SECOND:
			mSecondPlayerList.remove(card);

		case THIRD:
			mThirdPlayerList.remove(card);

		case FOURTH:
			mFourthPlayerList.remove(card);

		default:
			break;
		}
		mPlayedCard.add(card);
	}

	public static void reset() {
		mFirstPlayerList.clear();
		mSecondPlayerList.clear();
		mThirdPlayerList.clear();
		mFourthPlayerList.clear();
		mPlayedCard = new PlayedCard();
		populateDeck();
	}
	
	public static void sort(Player player){
		final CardComparator cardComparator = new CardComparator();
		if(player == null){
			player = Player.FIRST;		
		}
		
		switch (player) {
		case FOURTH:
			Collections.sort(mFourthPlayerList, cardComparator);
			break;

		default:
			Collections.sort(mFirstPlayerList, cardComparator);
			Collections.sort(mSecondPlayerList, cardComparator);
			Collections.sort(mThirdPlayerList, cardComparator);			
			break;
		}
	}

	public static int deckSize() {
		final int size = mFirstPlayerList.size() + mSecondPlayerList.size()
				+ mThirdPlayerList.size() + mFourthPlayerList.size();
		return size;
	}
}

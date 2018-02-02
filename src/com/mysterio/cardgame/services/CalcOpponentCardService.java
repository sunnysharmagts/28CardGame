package com.mysterio.cardgame.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import com.mysterio.cardgame.CardConstants;
import com.mysterio.cardgame.CardGameApplication;
import com.mysterio.cardgame.database.CardDBHandler;
import com.mysterio.cardgame.model.Card;
import com.mysterio.cardgame.model.Card.Numbers;
import com.mysterio.cardgame.model.Card.Player;
import com.mysterio.cardgame.model.Card.Suit;
import com.mysterio.cardgame.model.CardModel;
import com.mysterio.cardgame.util.CardUtils;

public class CalcOpponentCardService extends IntentService {

	private static final String LOG_TAG = "CalcOpponentService";
	private static final String SERVICE_NAME = "SearchSameSuit";
	private CardDBHandler mCardDBHandler;
	private ResultReceiver mResultReceiver;
	private CountDownLatch mCountDownLatch;

	public CalcOpponentCardService() {
		super(SERVICE_NAME);
	}

	@Override
	public void onCreate() {
		mCardDBHandler = new CardDBHandler(this, CardConstants.DB_NAME, null,
				CardConstants.DB_VERSION);
		super.onCreate();
		mCountDownLatch = new CountDownLatch(1);
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		mResultReceiver = intent
				.getParcelableExtra(CardConstants.RESULT_RECEIVER);
		Card card = null;
		int playedCardStackSize = 0;
		final boolean isTrumpRevealed = intent.getBooleanExtra(
				CardConstants.IS_TRUMP_OPEN, false);
		final Suit trumpSuit = (Suit) intent
				.getSerializableExtra(CardConstants.TRUMP_CARD);
		List<Card> playedCardList = intent
				.getParcelableArrayListExtra(CardConstants.PLAYED_CARDS);
		boolean hasFourthPlayed = intent.getBooleanExtra(
				CardConstants.HAS_FOURTH_PLAYED, false);
		Log.d(LOG_TAG, "Card which has been played is :- " + playedCardList);
		if (playedCardList == null) {
			playedCardList = new ArrayList<Card>();
			final Player player = (Player) intent
					.getSerializableExtra(CardConstants.PLAYER);
			card = checkForFirstPersonCards(player);
		} else {
			playedCardStackSize = playedCardList.size();
			card = playedCardList.get(playedCardStackSize - 1);
		}
		/*
		 * 1) Check whether suit is present in the opponent present stack or
		 * not. 2) If present then check which player is it. If second player
		 * then check how many cards has been played of the same suit.
		 */

		/*
		 * Logic implementation for processing of cards for a single player at a
		 * time.
		 */
		Suit suitEnum = null;
		final Player player = card.getPlayer();
		if (playedCardStackSize == 0) {
			Log.d(LOG_TAG, "This is the first player for this hand");
			playedCardList.add(card);
			suitEnum = card.getSuit(); // The first card
														// player's suit is the
														// SUIT for the hand
														// being played
		} else {
			int nextPlayerID = 0;
			switch (player) {
			case FOURTH:
				nextPlayerID = 0;
				break;

			default:
				nextPlayerID = player.ordinal() + 1;
				break;
			}
			suitEnum = playedCardList.get(0).getSuit(); // The first card
														// player's suit is the
														// SUIT for the hand
														// being played
			final Player nextPlayer = Player.values()[nextPlayerID];
			Log.d(LOG_TAG, "Next Player : " + nextPlayer);
			final Card matchingCard = getMatchingCardList(suitEnum, nextPlayer,
					playedCardList, isTrumpRevealed, trumpSuit);
			Log.d(LOG_TAG, "Matching card of " + player + " " + matchingCard);
			playedCardList.add(matchingCard);
		}

		final Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(CardConstants.PLAYED_CARDS,
				(ArrayList<? extends Parcelable>) playedCardList);
		bundle.putSerializable(CardConstants.SUIT, suitEnum);
		bundle.putBoolean(CardConstants.HAS_FOURTH_PLAYED, hasFourthPlayed);
		mResultReceiver.send(CardConstants.SHOW_PLAYED_CARD, bundle);
	}

	public static final Intent getIntent(final Context context,
			final ResultReceiver resultReceiver,
			final List<Card> playedCardList, final Player player,
			final boolean hasFourthPlayed, final boolean mIsTrumpRevealed,
			final Suit trumpCard) {
		final Intent intent = new Intent(context, CalcOpponentCardService.class);
		intent.putExtra(CardConstants.RESULT_RECEIVER, resultReceiver);
		intent.putExtra(CardConstants.HAS_FOURTH_PLAYED, hasFourthPlayed);
		intent.putExtra(CardConstants.PLAYER, player);
		intent.putParcelableArrayListExtra(CardConstants.PLAYED_CARDS,
				(ArrayList<? extends Parcelable>) playedCardList);
		intent.putExtra(CardConstants.TRUMP_CARD, trumpCard);
		intent.putExtra(CardConstants.IS_TRUMP_OPEN, mIsTrumpRevealed);
		return intent;
	}

	private Card checkForFirstPersonCards(final Player player) {
		final List<Card> playerDeck = CardGameApplication.getDeckCards(player);
		final Iterator<Card> iterator = playerDeck.iterator();

		while (iterator.hasNext()) { // TODO: Play Jack based on specified
										// conditions
			final Card card = (Card) iterator.next();
			if (card.has(Numbers.JACK)) {
				return card;
			}
		}

		Log.d(LOG_TAG, "Cards that can be played : "
				+ checkDatabase(playerDeck));
		final List<Card> possibleCardToPlayList = checkDatabase(playerDeck);
		return possibleCardToPlayList.get(0);
	}

	private List<Card> checkDatabase(final List<Card> playerDeck) {

		/*
		 * Check which of the jack players has been played, based on that check
		 * whether you have the next precendence card. if you don't have the
		 * next precendence card then play smaller card.
		 * 
		 * In case you have more than one precedence card that can be played to
		 * guarantee your win then return those cards and based on other
		 * strategy play any of those cards.
		 */
		final List<Card> cardTobePlayed = new ArrayList<Card>();
		final List<CardModel> cardsList = mCardDBHandler.getCardBasedOnSuit();
		for (CardModel cardObj : cardsList) {
			final List<Card> cardWithSameSuitList = cardObj.getList();
			final Card.Suit suit = cardWithSameSuitList.get(0).getSuit();
			// check if you have the present suit or not if not then check in
			// the other list.
			if (!cardWithSameSuitList.isEmpty() && playerDeck.contains(suit)) {
				/*
				 * It means your deck still has that suit which has already been
				 * played.There is a chance that you can play the same card
				 * again.
				 */
				final Numbers number = CardUtils
						.getHighestAvailCard(cardWithSameSuitList);
				Log.d(LOG_TAG, "The highest available card for " + suit
						+ " is " + number);
				if (playerDeck.contains(number)) {
					Log.d(LOG_TAG, "Player has " + number);
					final Card card = new Card(suit, number);
					cardTobePlayed.add(card);
				}
				/*
				 * else{
				 * cardTobePlayed.add(CardUtils.getLowPointCard(playerDeck,
				 * suit)); }
				 */
			}
		}
		if (cardTobePlayed.isEmpty()) {
			cardTobePlayed.add(CardUtils.getLowPointCard(playerDeck));
		}
		return cardTobePlayed;
	}

	/**
	 * If the AI level is ROOKIE then just play the first card you get
	 * otherPlayedCardList : List<Card> cards which other player have already
	 * played
	 */
	private Card getMatchingCardList(final Suit suit, final Player player,
			final List<Card> otherPlayedCardList,
			final boolean isTrumpRevealed, final Suit trumpSuit) {
		boolean isCardSuitAvailable = false;
		List<Card> matchingSuitCardList = new ArrayList<Card>();
		Card matchCard = null;
		final List<Card> playerStackList = CardGameApplication
				.getDeckCards(player);
		// final List<Card> matchingCardList = new ArrayList<Card>();
		for (final Card card : playerStackList) {
			if (suit.equals(card.getSuit())) {
				isCardSuitAvailable = true;
				matchingSuitCardList.add(card);
				// matchCard = card;
			}
		}
		/*
		 * If there is no matching card for the particular SUIT then put another
		 * card
		 */
		if (matchingSuitCardList.size() == 1) {
			return matchingSuitCardList.get(0);
		}

		else if (matchingSuitCardList.isEmpty()) {
			matchingSuitCardList = playerStackList;
		}

		/*********************************************************************
		 * CHECK FOR THE SUITABLE CARD
		 * 
		 * If the the player doesn't have a card then check for another card
		 * from the present deck of the player. Make sure that the card pulled
		 * out from the deck needs to be point/pointless based on the other
		 * cards that has been played beforehand.(Which team is winning)
		 * 
		 * TODO: Also some of the scenarios needs to be taken care of which
		 * includes 1) check how many cards of that suit has been already played
		 * 2) Check if is there any more trump card played or not 3) Check that
		 * card being played from the another suit is not of higher rate (Don't
		 * play Jack if there is no 9 present or if there are less than four
		 * cards present)
		 * 
		 ********************************************************************/

		/***********************************************************************
		 * Checks that are implemented 1) Play JACK if present. 2) See who is
		 * winnning. If opponent is winning then play pointless card else play
		 * point card.
		 * 
		 * a)If the player doesn't have a jack and is the first player to be
		 * playing
		 * 
		 * 
		 * 3) If I am the third player to be played and JACK has not been played
		 * then play the point card.
		 ***********************************************************************/

		/* Check if jack is present. If yes then play jack
		 * Also check whether the opponent has played the trump card or not.
		 * If played then play low card .
		 */
		boolean isSameTeam = false;
		final Card checkWhoIsWinning = CardUtils.checkWhoIsWinning(
				otherPlayedCardList, suit, isTrumpRevealed, trumpSuit);
		
		if(checkWhoIsWinning != null){
			isSameTeam = CardUtils.isSameTeam(checkWhoIsWinning.getPlayer(), player);
		}
		
		if (isCardSuitAvailable) {
			Log.d(LOG_TAG, "checkWhoIsWinning : "+checkWhoIsWinning + " isSameTeam : "+isSameTeam);
			Log.d(LOG_TAG, "Available cards : "+matchingSuitCardList);
			final Iterator<Card> itr = matchingSuitCardList.iterator();
			while (itr.hasNext()) {
				final Card card = itr.next();
				
				// If jack of the same suit is available
				if (card.has(Numbers.JACK)) {
					if (!isTrumpRevealed) {
						return card;
					} else {
						if (!isSameTeam && checkWhoIsWinning.getSuit()
										.equals(trumpSuit) && card.getSuit().equals(trumpSuit)) {
							return card;
						} else if (!checkWhoIsWinning.getSuit().equals(trumpSuit)) {
							return card;
						}
					}
				}
			}
		}

		// if jack is not there check who is winning.
		if (!otherPlayedCardList.isEmpty()) {
			Log.d(LOG_TAG, "Other players played : " + otherPlayedCardList);
			Log.d(LOG_TAG, checkWhoIsWinning + " is winning");
			if (isSameTeam) {
				matchCard = CardUtils.getHighPointCard(matchingSuitCardList,
						suit, trumpSuit, isTrumpRevealed);
				Log.d(LOG_TAG, "The player " + matchCard.getPlayer()
						+ " is in the same team. " + " and played " + matchCard);
			} else {
				if (!isCardSuitAvailable) {
					if (!isTrumpRevealed) {
						new Handler(getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(
										getApplicationContext(),
										"Player " + player
												+ " wants to open trump card. ",
										Toast.LENGTH_SHORT).show();
								Log.i(LOG_TAG, "callback to show trump card");
								mResultReceiver.send(
										CardConstants.SHOW_TRUMP_CARD, null);
								try {
									mCountDownLatch.await();
								} catch (final InterruptedException e) {
									Log.e(LOG_TAG, e.getMessage());
								}
							}
						});
					}
					Log.i(LOG_TAG, "calculate trump card");
					matchCard = CardUtils.playTrumpCard(playerStackList,
							trumpSuit, checkWhoIsWinning);
				}
				mCountDownLatch.countDown();
				if (matchCard == null) {
					matchCard = CardUtils.getLowPointCard(matchingSuitCardList);
					/*Log.d(LOG_TAG, "The player " + matchCard.getPlayer()
							+ " is in the different team. ");*/
				}
			}
		}
		// if you are the first player to play and doesn't have a jack then play
		// small card
		else {
			// TODO: now play any pointless card. In case if there is no
			// pointless card then play least point card
			/*
			 * matchCard = CardUtils.getLowPointCard(matchingSuitCardList,
			 * suit);
			 */
			matchCard = CardUtils.getLowPointCard(matchingSuitCardList);
		}

		return matchCard;
	}

	@Override
	public void onDestroy() {
		Log.i(LOG_TAG, "onDestroy() called");
		super.onDestroy();
	}
}

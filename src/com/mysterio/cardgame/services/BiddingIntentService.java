package com.mysterio.cardgame.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.mysterio.cardgame.CardConstants;
import com.mysterio.cardgame.CardGameApplication;
import com.mysterio.cardgame.model.Card;
import com.mysterio.cardgame.model.Card.Player;
import com.mysterio.cardgame.model.Card.Suit;
import com.mysterio.cardgame.util.CardComparator;
import com.mysterio.cardgame.util.CardUtils;

public class BiddingIntentService extends IntentService {

	private static final String BIDDING = "bidding";
	private Map<Card, Integer> mBiddingMap;
	
	public BiddingIntentService() {
		super(BIDDING);
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		//final int presentBid = intent.getIntExtra(CardConstants.PRESENT_BIDDING_VALUE, 15);
		final ResultReceiver resultReceiver = intent.getParcelableExtra(CardConstants.RESULT_RECEIVER);
		mBiddingMap = new TreeMap<Card, Integer>(new CardComparator("map"));
		bidBasedOnCards();
		Log.d(BIDDING, "Players and their bidding extent : "+mBiddingMap);
		final Bundle bundle = new Bundle();
		bundle.putSerializable(CardConstants.BIDDING_CARDS, (Serializable) mBiddingMap);
		resultReceiver.send(CardConstants.BOTS_BIDDING_VALUE, bundle);
	}
	
	public static final Intent getIntent(final Context context, final ResultReceiver resultReceiver){
		final Intent intent = new Intent(context, BiddingIntentService.class);
		intent.putExtra(CardConstants.RESULT_RECEIVER, resultReceiver);
		return intent;
	}
	
	//calculate upto what extend each bot player can bid
	private void bidBasedOnCards(){
		final Player[] players = {Player.THIRD, Player.SECOND, Player.FIRST};
		for(int i = 0; i < 3; i++){
			calculateMaxBid(players[i]);
		}
	}
	
	private void calculateMaxBid(final Player player){
		Log.d(BIDDING, "For player : "+player);
		final List<Card> presentDeck = CardGameApplication.getDeckCards(player);
		final Map<Suit, List<Card>> weightPointsMap = new HashMap<Suit, List<Card>>();
		//This will get you the highest weight
		for(int i = 0; i < 4; i++){
			final Card card = presentDeck.get(i);
			final Suit suit = card.getSuit();
			if(weightPointsMap.containsKey(suit)){
				final List<Card> cardList = weightPointsMap.get(suit);
				cardList.add(card);
			}
			else{
				final List<Card> list = new ArrayList<Card>();
				list.add(card);
				weightPointsMap.put(suit, list);
			}
		}
		Log.d(BIDDING, "Bidding cards : "+weightPointsMap);
		int size = 0;
		List<Card> trumpList = null;
		for(Entry<Suit, List<Card>> entry : weightPointsMap.entrySet()){
			final int pSize = entry.getValue().size();
			if(pSize > size){
				trumpList = entry.getValue();
				size = pSize;
				Log.d(BIDDING, "Bigger common suit cards : "+trumpList);
			}
			else if(pSize == size){
				Log.d(BIDDING, "equal weighted suits : "+trumpList);
				if(size > 1){
					//calculate the points for both the suits if its weight is greater than 1 else not use
					int previousSuitPoints = 0;
					for(int i = 0; i < trumpList.size(); i++){
						final int point = CardUtils.getRate(trumpList.get(i));
						if(point > 0){
							previousSuitPoints += point;
						}	
					}
					
					int presentSuitPoints = 0;
					for(int i = 0; i < entry.getValue().size(); i++){
						final int point = CardUtils.getRate(entry.getValue().get(i));
						if(point > 0){
							presentSuitPoints += point;
						}
					}
					
					if(previousSuitPoints < presentSuitPoints){
						trumpList = entry.getValue();
					}
				}
			}
		}
		Log.d(BIDDING, "trumpList : "+trumpList);
		int points = 0;
		final int weight = trumpList.size();
		for(int i = 0; i < weight; i++){
			final int point = CardUtils.getRate(trumpList.get(i)); 
			if(point > 0){
				points += point;
			}
		}
		Log.d(BIDDING, "get bidding rate based on weight : "+weight + " and points : "+points);
		mBiddingMap.put(trumpList.get(0), CardUtils.getBiddingRate(weight, points));
	}
}

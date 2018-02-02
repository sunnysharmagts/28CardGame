package com.mysterio.cardgame.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mysterio.cardgame.CardConstants;
import com.mysterio.cardgame.model.Card;
import com.mysterio.cardgame.model.Card.Numbers;
import com.mysterio.cardgame.model.Card.Player;
import com.mysterio.cardgame.model.Card.Suit;
import com.mysterio.cardgame.model.CardModel;

public class CardDBHandler extends SQLiteOpenHelper {

	private static final String LOG_TAG = "CardDBHandler";
	
	private static final String CARD_28_TABLE = " create table "
			+ CardConstants.TABLE_NAME + " ( " + CardConstants.KEY_ROWID
			+ " integer primary key autoincrement, " + CardConstants.SUIT
			+ " text not null, " + CardConstants.NUMBER + " text not null, "
			+ CardConstants.PLAYER + " text not null " + " ); ";
	
	private static final String GET_ALL_CONTACTS = "select * from "+CardConstants.TABLE_NAME;
	private static final String EQUALS = " = ";

	public CardDBHandler(final Context context, String name,
			CursorFactory factory, int version) {
		super(context, CardConstants.DB_NAME, null, CardConstants.DB_VERSION);

	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.execSQL(CARD_28_TABLE);
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
			final int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+CardConstants.TABLE_NAME);
		onCreate(db);
	}
	
	public void savePlayedHand(final Card card){
		final SQLiteDatabase db = this.getWritableDatabase();
		try {
			final ContentValues contentValues = new ContentValues();
			contentValues.put(CardConstants.SUIT, card.getSuit().name());
			contentValues.put(CardConstants.NUMBER, card.getNumber().name());
			contentValues.put(CardConstants.PLAYER, card.getPlayer().name());
			
			db.insert(CardConstants.TABLE_NAME, null, contentValues);
		} catch (final Exception e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		finally{
			if(db != null){
				db.close();
			}
		}
	}
	
	public List<Card> getPlayedHand(){
		final List<Card> playedCardList = new ArrayList<Card>();
		final SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		try{
			cursor = db.rawQuery(GET_ALL_CONTACTS, null);
			if(cursor.moveToFirst()){
				do{
					final String suit = cursor.getString(cursor.getColumnIndex(CardConstants.SUIT));
					final String number = cursor.getString(cursor.getColumnIndex(CardConstants.NUMBER));
					final String player = cursor.getString(cursor.getColumnIndex(CardConstants.PLAYER));
					final Card card = new Card(Suit.valueOf(suit), Numbers.valueOf(number));
					card.setPlayer(Player.valueOf(player));
					playedCardList.add(card);
				}
				while(cursor.moveToNext());
			}
		} 
		catch(final Exception e){
			Log.e(LOG_TAG, e.getMessage());
		}
		finally{
			if(cursor != null)
				cursor.close();
			db.close();
		}
		
		return playedCardList;
	}
	
	public void clearDB(){
		final SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(CardConstants.TABLE_NAME, null, null);
		} catch (final Exception e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		finally{
			db.close();
		}
	}
	
	public List<CardModel> getCardBasedOnSuit(){
		final List<CardModel> playedCardList = new ArrayList<CardModel>();
		final SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		final Card.Suit[] allSuits = Card.Suit.values();
		final List<Card> suitCards = new ArrayList<Card>();
		try {
			for (Card.Suit suit : allSuits) {
				cursor = db.rawQuery(GET_ALL_CONTACTS,
						new String[] { CardConstants.SUIT + EQUALS + suit });
				if (cursor.moveToFirst()) {
					do {
						final String suitValue = cursor.getString(cursor.getColumnIndex(CardConstants.SUIT));
						final String number = cursor.getString(cursor.getColumnIndex(CardConstants.NUMBER));
						final String player = cursor.getString(cursor.getColumnIndex(CardConstants.PLAYER));
						final Card card = new Card(Suit.valueOf(suitValue),Numbers.valueOf(number));
						card.setPlayer(Player.valueOf(player));
						// playedCardList.add(card);
						suitCards.add(card);
					} while (cursor.moveToNext());
				}
				cursor.close();
				playedCardList.add(new CardModel(suitCards));
			}
		} catch (final Exception e) {
			Log.e(LOG_TAG, e.getMessage());
		} finally {
			if (cursor != null)
				cursor.close();
			db.close();
		}
		return playedCardList;
	}
	
	/*public List<Card> getHighestCardForEachSuit(){
		//final List<Card> playedCardList = new ArrayList<Card>();
		final SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		try{
			for(Suit suits : Suit.values()){
			cursor = db.rawQuery(GET_ALL_CONTACTS, new String[] {CardConstants.SUIT + EQUALS + suits} );
			if(cursor.moveToFirst()){
				do{
					final String suit = cursor.getString(cursor.getColumnIndex(CardConstants.SUIT));
					final String number = cursor.getString(cursor.getColumnIndex(CardConstants.NUMBER));
					final String player = cursor.getString(cursor.getColumnIndex(CardConstants.PLAYER));
					final Card card = new Card(SUITS.valueOf(suit), NUMBERS.valueOf(number));
					card.setPlayer(PLAYER.valueOf(player));
					playedCardList.add(card);
				}
				while(cursor.moveToNext());
			}
			}	
		} 
		catch(final Exception e){
			Log.e(LOG_TAG, e.getMessage());
		}
		finally{
			if(cursor != null)
				cursor.close();
			db.close();
		}
		
		return playedCardList;
	}*/	

}

package com.mysterio.cardgame.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.mysterio.cardgame.util.CardUtils;

public class Card implements Parcelable, Comparable<Card>{

	private Suit mCard;
	private Numbers mNumber;
	private boolean mCardStatus;
	private Player mPlayer;
	
	public enum Suit {
		HEARTS(1), SPADES(2), CLUB(3), DIAMOND(4);

		private int id;

		Suit(final int id) {
			this.id = id;
		}

		public int getID() {
			return id;
		}
	}
	
	public enum Numbers {
		ACE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(
				9), TEN(10), JACK(11), QUEEN(12), KING(13);

		private int id;

		private Numbers(final int id) {
			this.id = id;
		}

		public int getID() {
			return id;
		}
	}
	
	public enum Player {
		FIRST(1), SECOND(2), THIRD(3), FOURTH(4);
		
		private final int id;
		
		private Player(final int id) {
			this.id = id;
		}
		
		public int getID(){
			return id;
		}
	}
	
	public enum TEAM {
		TEAM_A, TEAM_B;
	}

	public Card(final Suit card, final Numbers number) {
		mCard = card;
		mNumber = number;
	}
	
	public Card(final Parcel parcel) {
		readFromParcel(parcel);
	}

	public Suit getSuit() {
		return mCard;
	}
	
	public boolean getCardStatus(){
		return mCardStatus;
	}
	
	
	
	public void setCardStatus(final boolean cardStatus){
		mCardStatus = cardStatus;
	}

	public Player getPlayer() {
		return mPlayer;
	}

	public void setPlayer(final Player mPlayer) {
		this.mPlayer = mPlayer;
	}

	public Numbers getNumber() {
		return mNumber;
	}
	
	public boolean has(final Object object){
		if(object instanceof Numbers){
			final Numbers number = (Numbers) object;
			return this.mNumber == number;
		}
		
		else if(object instanceof Suit){
			final Suit suits = (Suit) object;
			return this.mCard == suits;
		}
		
		return false;
	}

	@Override
	public boolean equals(final Object object) {
		if (object != null) {
			if(object instanceof Numbers){
				return (this.mNumber == (Numbers)object);
			}
			
			else if(object instanceof Suit){
				return (this.mCard == (Suit)object);
			}
			
			final Card cardObj = (Card) object;
			if (this.mCard == cardObj.mCard && this.mNumber == cardObj.mNumber)
				return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		super.hashCode();
		return this.mCard.getID() + this.mNumber.getID();
	}
	
	@Override
	public int compareTo(final Card another) {
		if(CardUtils.getRate(this) > CardUtils.getRate(another)){
			return 1;
		}
		else if(CardUtils.getRate(this) < CardUtils.getRate(another)){
			return -1;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[").append(" {Suit, ").append(mCard).append("} ");
		stringBuilder.append(" {Number, ").append(mNumber).append("} ");
		stringBuilder.append(" {Player, ").append(mPlayer).append("} ").append("]").append("\n");
		
		return stringBuilder.toString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		//if(mCard != null && mNumber != null){
			dest.writeString(mCard.name());
			dest.writeString(mNumber.name());
			dest.writeString(mPlayer.name());
		//}
		/*else{
			dest.writeString("");
			dest.writeString("");
			dest.writeString("");			
		}*/
	}
	
	private void readFromParcel(final Parcel parcel){
		mCard = Suit.valueOf(parcel.readString());
		mNumber = Numbers.valueOf(parcel.readString());
		mPlayer = Player.valueOf(parcel.readString());
	}
	
	public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
		public Card createFromParcel(final Parcel in) {
			return new Card(in);
		}

		public Card[] newArray(final int size) {
			return new Card[size];
		}
	};
}

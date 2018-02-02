package com.mysterio.cardgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mysterio.cardgame.database.CardDBHandler;
import com.mysterio.cardgame.model.Card;
import com.mysterio.cardgame.model.Card.Player;
import com.mysterio.cardgame.model.Card.Suit;
import com.mysterio.cardgame.model.Card.TEAM;
import com.mysterio.cardgame.model.Level;
import com.mysterio.cardgame.model.PlayingCard;
import com.mysterio.cardgame.services.BiddingIntentService;
import com.mysterio.cardgame.services.CalcOpponentCardService;
import com.mysterio.cardgame.util.CardUtils;
import com.mysterio.mycardgame.R;

public class CardGameActivity extends BaseActivity implements OnClickListener{

	private static final String LOG_TAG = "CardGameActivity";
	private LinearLayout mLeftPlayerLayout;
	private LinearLayout mTopPlayerLayout;
	private LinearLayout mRightPlayerLayout;
	private LinearLayout mBottomPlayerLayout;
	private LinearLayout mBottomMainLayout;
	//private RelativeLayout mMainLayout;
	private ImageView mLeftSelectedCardView;
	private ImageView mTopSelectedCardView;
	private ImageView mRightSelectedCardView;
	private ImageView mBottomSelectedCardView;
	private ImageView mTopOnlineImageView;
	private ImageView mLeftOnlineImageView;
	private ImageView mRightOnlineImageView;
	private ImageView mBottomOnlineImageView;
	//private static final String BACK = "back1";
	private CardDBHandler mCardDBHandler;
	private boolean mHasFourthPlayed;
	private List<Card> mOtherPlayedCardList;
	private Card.Suit mSuitHand;		//Defines for which suit the hand is being played
	private Card.Suit mTrumpCard;
	private boolean mIsTrumpRevealed;
	private ImageButton mTrumpCardView;
	private Map<Card, Integer> mBiddingMap;
	private int mTrumpValue = 16;
	private int mAllyPoints;
	private int mOpponentPoints;
	private int mTeamAPoints;
	private int mTeamBPoints;
	private Player mWhoMadeTrump;
	//private View mBubbleView;
	private Map<Player, Boolean> mPassedPlayerMap;
	private static final String TAG_TRUMP = "trump";
	private static final String SPACE = " ";
	private Dialog mDialog;
	private Player mStartingPlayer;
	private ImageView mPlayerImageView;
	private TextView mPlayerBidTextView;
	private boolean mFinishGame;
	private LinearLayout mButtonLayout;
	/*private Player mPlayerWithKQ;
	private boolean mIsKQCheckDone;*/
	private Level mLevel;
	private boolean mDouble;
	private Button seventhCardLayout;
	
	
//	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_page);
		mLevel = (Level) getIntent().getSerializableExtra(CardConstants.LEVEL);
		Log.d(LOG_TAG, "Level : "+ mLevel);
		init();
	}
	
	@Override
	protected void onDestroy() {
		//CardGameApplication.reset();
		super.onDestroy();
	}
	
	private void init(){
		mHasFourthPlayed = true;
		mOtherPlayedCardList = new ArrayList<Card>();
		mLeftPlayerLayout = (LinearLayout) findViewById(R.id.leftPlayerLayout);
		mTopPlayerLayout = (LinearLayout) findViewById(R.id.topPlayerLayout);
		mRightPlayerLayout = (LinearLayout) findViewById(R.id.rightPlayerLayout);
		mBottomMainLayout = (LinearLayout) findViewById(R.id.bottomPlayerLayout);
		mLeftSelectedCardView = (ImageView) findViewById(R.id.centerLeftImageView);
		mTopSelectedCardView = (ImageView) findViewById(R.id.centerTopImageView);
		mRightSelectedCardView = (ImageView) findViewById(R.id.centerRightImageView);
		mBottomSelectedCardView = (ImageView) findViewById(R.id.centerBottomImageView);
		
		mBottomOnlineImageView = (ImageView) findViewById(R.id.BottomOnlineImageView);
		mTopOnlineImageView = (ImageView) findViewById(R.id.topOnlineImageView);
		mRightOnlineImageView = (ImageView) findViewById(R.id.rightOnlineImageView);
		mLeftOnlineImageView = (ImageView) findViewById(R.id.leftOnlineImageView);
		
		//mMainLayout = (RelativeLayout) findViewById(R.id.table);
		mTrumpCardView =(ImageButton) findViewById(R.id.trumpCard);
		mTrumpCardView.setEnabled(false);
		mTrumpCardView.setOnClickListener(this);
		mCardDBHandler = new CardDBHandler(this, CardConstants.DB_NAME, null, CardConstants.DB_VERSION);
		mCardDBHandler.clearDB();
		mPassedPlayerMap = new HashMap<Card.Player, Boolean>();
		mPassedPlayerMap.put(Player.FIRST, false);
		mPassedPlayerMap.put(Player.SECOND, false);
		mPassedPlayerMap.put(Player.THIRD, false);
		mStartingPlayer = Player.FOURTH;
		Log.i(LOG_TAG, "Starting AyncTask ...");
		new MyDeckAsyncTask().execute();
		
	}
	
	private void addCards(final LinearLayout linearLayout, final Player player, final int start, final int end){
		StringBuilder stringBuilder = null;
		int drawable = 0;
		List<Card> cardPackList = null;
		Card card = null;
		switch (player) {
		case FOURTH:
			cardPackList = CardGameApplication.getDeckCards(player);
			/*final int count = linearLayout.getChildCount();
			for(int i = 0; i < count; i++){
				final View view = linearLayout.getChildAt(i);
				if(view instanceof LinearLayout){
					cardLinearLayout = (LinearLayout) view;
					break;
				}
			}*/
			if(mBottomPlayerLayout == null){
				mBottomPlayerLayout = new LinearLayout(this);
				mBottomPlayerLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				mBottomPlayerLayout.setOrientation(LinearLayout.HORIZONTAL);
				linearLayout.addView(mBottomPlayerLayout);
			}
			for(int i = start; i < end; i++){
				card = cardPackList.get(i);
				drawable = CardUtils.getDrawable(this, card);
				mBottomPlayerLayout.addView(addPlayerCardsView(drawable, card));
				
			}
			break;
		case FIRST:
			drawable = R.drawable.player1_100;
			break;
		case SECOND:
			drawable = R.drawable.player2_100;
			break;
		case THIRD:
			drawable = R.drawable.player3_100;
			break;
			
		default:
			break;
			/*cardPackList = CardGameApplication.getDeckCards(player);
			drawable = getResources().getIdentifier(String.valueOf(stringBuilder), "drawable", getPackageName());			
			break;*/
		}
		if (!player.equals(Player.FOURTH)) {
			linearLayout.addView(addPlayerCardsView(drawable, null));
		}	
		/*if (!player.equals(Player.FOURTH)) {
			for(int i = start; i < end; i++){
				card = cardPackList.get(i);
				linearLayout.addView(addPlayerCardsView(drawable, card));
			}
		}*/
	}
	
	private void offline(){
		mLeftOnlineImageView.setVisibility(View.INVISIBLE);
		mTopOnlineImageView.setVisibility(View.INVISIBLE);
		mRightOnlineImageView.setVisibility(View.INVISIBLE);
		mBottomOnlineImageView.setVisibility(View.INVISIBLE);
	}
	
	private void allowBlockFourth(final boolean enable) {
		if (mBottomPlayerLayout != null) {
			final int count = mBottomPlayerLayout.getChildCount();
			for (int i = 0; i < count; i++) {
				View view = mBottomPlayerLayout.getChildAt(i);
				view.setEnabled(enable);
				final Drawable drawable = view.getBackground();
				if (drawable != null) {
					if (enable) {
						drawable.mutate().setAlpha(255);
					} else {
						drawable.mutate().setAlpha(50);
					}
				}
			}
		}
	}
	
	/*
	 * This method will return an ImageButton object which will be added to the layout
	 * horizontly and vertically for players according to the spots.
	 */
	private ImageButton addPlayerCardsView(final int resID, final Card viewTag){
		final ImageButton imageButton = new ImageButton(this);
		imageButton.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		imageButton.setPadding(2, 0, 0, 0);
		imageButton.setImageResource(resID);
		imageButton.setBackgroundColor(Color.TRANSPARENT);
		imageButton.setTag(viewTag);
		imageButton.setOnClickListener(this);
		return imageButton;
	}
	
	class MyDeckAsyncTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			PlayingCard.shuffle(mLevel);
			return null;
		}
		
		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(final Void result) {
			addCards(mLeftPlayerLayout, Player.FIRST, 0, 1);
			addCards(mTopPlayerLayout, Player.SECOND, 0, 1);
			addCards(mRightPlayerLayout, Player.THIRD, 0, 1);
			//addCards(mBottomMainLayout, Player.FOURTH, 0, 4);
			Log.i(LOG_TAG, "start bidding service");
			allowBlockFourth(false);
			final Intent intent = BiddingIntentService.getIntent(CardGameActivity.this, mBiddingResultReceiver);
			startService(intent);
		}
	}
	
	private final ResultReceiver mBiddingResultReceiver = new ResultReceiver(new Handler()){
		
		@Override
		protected void onReceiveResult(final int resultCode, final Bundle resultData) {
			switch (resultCode) {
			
			case CardConstants.BOTS_BIDDING_VALUE:
				mBiddingMap = (Map<Card, Integer>) resultData.getSerializable(CardConstants.BIDDING_CARDS);
				Log.d(LOG_TAG, "Bidding map : "+mBiddingMap);
				final Bundle bundle = new Bundle();
				bundle.putParcelable(CardConstants.RESULT_RECEIVER, mBiddingResultReceiver);
				showDialog(CardConstants.BIDDING, bundle);
				break;
				
			case CardConstants.BACK_BUTTON_PRESS:
				mFinishGame = true;
				final ProgressDialog progressDialog = new ProgressDialog(CardGameActivity.this);
				progressDialog.setMessage("Finishing Game");
				progressDialog.setCancelable(false);
				progressDialog.show();
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						progressDialog.dismiss();
						//setResult(RESULT_OK);
						finish();
					}
				}, 4000);
				break;
				
			case CardConstants.PLAY_ANOTHER_GAME:
				new MyDeckAsyncTask().execute();
				break;
				
			case CardConstants.BIDDING_DONE:
				final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CardGameActivity.this);
				final String suit = sharedPreferences.getString(CardConstants.TRUMP_CARD, null);
				final boolean hasPlayerPassed = hasPlayerPassed();
				if(!hasPlayerPassed){
					mTrumpValue = getBiddingValue();					
				}
				if(suit != null){
					mTrumpCard = Suit.valueOf(suit);
				}
				Log.d(LOG_TAG, "Suit set :- "+suit + " hasPlayerPassed : "+hasPlayerPassed + " Bidding value : "+mTrumpValue);
				botsBidding(hasPlayerPassed);
				break;
				
			case CardConstants.SHOW_PLAYED_CARD:
				if(!mFinishGame){
					mOtherPlayedCardList = resultData.getParcelableArrayList(CardConstants.PLAYED_CARDS);
					mSuitHand = (Suit)resultData.getSerializable(CardConstants.SUIT);
					mHasFourthPlayed = resultData.getBoolean(CardConstants.HAS_FOURTH_PLAYED);
					Log.d(LOG_TAG, "played card list :- "+mOtherPlayedCardList);
					final Card card = mOtherPlayedCardList.get(mOtherPlayedCardList.size() - 1);
					showCards(card);
				}
				break;
				
			case CardConstants.SHOW_TRUMP_CARD:
				mTrumpCardView.setImageResource(getTrumpCardID());
				mIsTrumpRevealed = true;
				break;

			default:
				break;
			}
		}
	};
	
	private void addTrumpImageToPlayer(){
		switch (mWhoMadeTrump) {
		case FIRST:
			mLeftPlayerLayout.addView(showTrumpPlayer());
			break;
			
		case SECOND:
			mTopPlayerLayout.addView(showTrumpPlayer());
			break;
			
		case THIRD:
			mRightPlayerLayout.addView(showTrumpPlayer());
			break;
			
		case FOURTH:
			/*final LinearLayout centerLayout = (LinearLayout) findViewById(R.id.centerLayout);
			centerLayout.addView(showTrumpPlayer());*/
			//mBottomPlayerLayout.addView(showTrumpPlayer());
			break;			

		default:
			break;
		}
	}
	
	private void checkHighestBotBidding(){
		//check for the bots who can bid the highest.
		int highestValue = 0;
		int secondHighestValue = 0;
		for(Map.Entry<Card, Integer> entry : mBiddingMap.entrySet()){
			final int value = entry.getValue();
			if(value > highestValue){
				secondHighestValue = highestValue;
				highestValue = value;
				mWhoMadeTrump = entry.getKey().getPlayer();
				mTrumpCard = entry.getKey().getSuit();
			}
			else if(secondHighestValue < value){
				secondHighestValue = value;
			}
		}
		if(mTrumpValue <= secondHighestValue){
			mTrumpValue = secondHighestValue; 
		}
		final StringBuilder sb = new StringBuilder(getString(R.string.has_set_trump_on));
		sb.append(SPACE);
		sb.append(mTrumpValue);
		displayDialog(mWhoMadeTrump, sb.toString());
		mButtonLayout.setVisibility(View.VISIBLE);
		addTrumpImageToPlayer();
		resetSuit();
		/*new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(mDialog != null){
					if(Player.FOURTH.equals(mStartingPlayer)){
						allowBlockFourth(true);
					}
					else{
						allowBlockFourth(false);
					}					
					mDialog.dismiss();
					mDialog = null;
				}
				if(!Player.FOURTH.equals(mStartingPlayer)){
					final Intent intent = CalcOpponentCardService.getIntent(CardGameActivity.this, mBiddingResultReceiver, null, mStartingPlayer, mHasFourthPlayed, mIsTrumpRevealed, mTrumpCard);
					startService(intent);
				}
			}
		}, 2500);*/		
	}
	
	/*private void doBotsBidding(final List<Map.Entry<Card, Integer>> list, final int value, int i, boolean flag){
		if (i >= 0 && i < list.size()) {
			final Entry<Card, Integer> entry = list.get(i);
			Log.d(LOG_TAG, "Entry : " + entry + " present Value : " + value
					+ " flag :" + flag);
			if (entry.getValue() >= value) {
				if (flag) {
					flag = false;
					i++;
					mTrumpCard = entry.getKey().getSuit();
					mWhoMadeTrump = entry.getKey().getPlayer();
					
					//Toast.makeText(this, mWhoMadeTrump + " has bid " + mTrumpValue, Toast.LENGTH_SHORT).show();
					final StringBuilder sb = new StringBuilder(getString(R.string.calls));
					sb.append(SPACE);
					sb.append(mTrumpValue);
					displayDialog(mWhoMadeTrump, sb.toString());
					final int i1 = i;
					final boolean flag1 = flag;
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							doBotsBidding(list, mTrumpValue + 1, i1, flag1);
						}
					}, 2500);
					
				} else {
					mTrumpValue = value;
					flag = true;
					i--;
					
					mTrumpCard = entry.getKey().getSuit();
					mWhoMadeTrump = entry.getKey().getPlayer();
					
					//Toast.makeText(this, mWhoMadeTrump + " has bid " + mTrumpValue, Toast.LENGTH_SHORT).show();
					final StringBuilder sb = new StringBuilder(getString(R.string.bids));
					sb.append(SPACE);
					sb.append(mTrumpValue);
					displayDialog(mWhoMadeTrump, sb.toString());
					
					final int i1 = i;
					final boolean flag1 = flag;
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							doBotsBidding(list, mTrumpValue, i1, flag1);
						}
					}, 2500);					
				}
			} else {
				Log.d(LOG_TAG, "Flag : "+ flag + "TrumpValue : " + mTrumpValue);
				if (list.get(0).getValue() == 15) {
					i++;
				}
				else if (flag) {
					i--;
				} else {
					i++;
				}
				final Player player = entry.getKey().getPlayer();
				if(!mPassedPlayerMap.get(player)){					
					//Toast.makeText(this, player + " has passed ", Toast.LENGTH_SHORT).show();
					displayDialog(player, getString(R.string.pass));
				}
				mPassedPlayerMap.put(player, true);
				final int i1 = i;
				final boolean flag1 = flag;
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						Log.d(LOG_TAG, "Do BotsBidding with i value "+ i1);
						doBotsBidding(list, mTrumpValue+1, i1, flag1);
					}
				}, 2500);		
			}
		}
		else{
			Log.i(LOG_TAG, "Bots setting trump");
			StringBuilder sb = new StringBuilder(getString(R.string.has_set_trump_on));
			sb.append(SPACE);
			sb.append(mTrumpValue);
			displayDialog(mWhoMadeTrump, sb.toString());
			addTrumpImageToPlayer();
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if(mDialog != null){
						mDialog.dismiss();
						mDialog = null;
					}
					if(!Player.FOURTH.equals(mStartingPlayer)){
						final Intent intent = CalcOpponentCardService.getIntent(CardGameActivity.this, mBiddingResultReceiver, null, mStartingPlayer, mHasFourthPlayed, mIsTrumpRevealed, mTrumpCard);
						startService(intent);
					}
				}
			}, 1500);
		}
	}*/
	
	
	private void doPlayerBidding(final List<Map.Entry<Card, Integer>> list, int i){
		if (i < list.size()) {
			Log.i(LOG_TAG, "Player setting trump");
			final Entry<Card, Integer> entry = list.get(i);
			final Player player = entry.getKey().getPlayer();
			if (entry.getValue() > mTrumpValue) {
				++mTrumpValue;
				Log.d(LOG_TAG, player + " bids "+mTrumpValue);
				final StringBuilder sb = new StringBuilder(getString(R.string.bids));
				sb.append(SPACE);
				sb.append(mTrumpValue);
				displayDialog(player, sb.toString());
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						mWhoMadeTrump = Player.FOURTH;
						final Bundle bundle = new Bundle();
						bundle.putParcelable(CardConstants.RESULT_RECEIVER,
								mBiddingResultReceiver);
						bundle.putInt(CardConstants.BIDDING_VALUE, mTrumpValue);
						showDialog(CardConstants.BIDDING, bundle);
					}
				}, 2500);
			} else {
				final int count = ++i;
				Log.d(LOG_TAG, player + " has passed");
				if(!mPassedPlayerMap.get(player)){
					displayDialog(player, getString(R.string.pass));
				}
				mPassedPlayerMap.put(player, true);
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						doPlayerBidding(list, count);
					}
				}, 2500);
			}
		}
		else{
			mWhoMadeTrump = Player.FOURTH;
			if(Level.Hard == mLevel){
				CardGameApplication.sort(mWhoMadeTrump);
			}	
			Log.d(LOG_TAG, mWhoMadeTrump +" has set trump on "+mTrumpValue);
			StringBuilder sb = new StringBuilder(getString(R.string.has_set_trump_on));
			sb.append(SPACE);
			sb.append(mTrumpValue);
			displayDialog(mWhoMadeTrump, sb.toString());
			addTrumpImageToPlayer();
			mButtonLayout.setVisibility(View.VISIBLE);
			Log.d(LOG_TAG, mWhoMadeTrump + " has "+list);
			resetSuit();
		}
	}
	
	private void displayDialog(final Player player, final String biddingText){
		if(mDialog == null){
			Log.d(LOG_TAG, "dialog is null. Making new object");
			mDialog = new Dialog(this);
			mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mDialog.setContentView(showPlayerBid(player, biddingText));
			mDialog.setCancelable(false);
			mDialog.show();
		}
		else{
			Log.d(LOG_TAG, "dialog is not null. Modifying dialog");
			int resourceID = R.drawable.usericon;
			if(player != null){
				switch (player) {
				case FIRST:
					resourceID = R.drawable.player1_100;
					break;
					
				case SECOND:
					resourceID = R.drawable.player2_100;
					break;
					
				case THIRD:
					resourceID = R.drawable.player3_100;
					break;
					
				default:
					break;
				}
			}
			mPlayerImageView.setImageResource(resourceID);
			mPlayerBidTextView.setText(biddingText);
			if(!Player.FOURTH.equals(player)){
				seventhCardLayout.setText(R.string.strdouble);
			}
			else{
				seventhCardLayout.setText(R.string.seventhCard);
			}			
		}
	}
	
	private View showPlayerBid(final Player player, final String biddingText){
		final View view = LayoutInflater.from(this).inflate(R.layout.show_player_bid, null);
		mPlayerImageView = (ImageView) view.findViewById(R.id.playerImageView);
		mButtonLayout = (LinearLayout) view.findViewById(R.id.buttonLayout);
		seventhCardLayout = (Button) view.findViewById(R.id.seventhCardButton);
		final Button noThanksButton = (Button) view.findViewById(R.id.noThanksButton);
		if(!Player.FOURTH.equals(player)){
			seventhCardLayout.setText(R.string.strdouble);
		}
		else{
			seventhCardLayout.setText(R.string.seventhCard);
		}
		seventhCardLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Card card = CardGameApplication.getDeckCards(Player.FOURTH).get(6);
				Log.d(LOG_TAG, "Seventh card "+card);
				if(Player.FOURTH.equals(player)){
					mTrumpCard = card.getSuit();
				}
				else{
					mDouble = true;
				}
				mButtonLayout.setVisibility(View.INVISIBLE);			
				if(!Player.FOURTH.equals(mStartingPlayer)){
					allowBlockFourth(false);
					final Intent intent = CalcOpponentCardService.getIntent(CardGameActivity.this, mBiddingResultReceiver, null, mStartingPlayer, mHasFourthPlayed, mIsTrumpRevealed, mTrumpCard);
					startService(intent);
				}
				else{
					allowBlockFourth(true);	
				}
				
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
					addCards(mBottomMainLayout, Player.FOURTH, 0, 8);
				}				
			}
		});
		
		noThanksButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
					addCards(mBottomMainLayout, Player.FOURTH, 0, 8);
				}
				mButtonLayout.setVisibility(View.GONE);
				if(!Player.FOURTH.equals(mStartingPlayer)){
					allowBlockFourth(false);
					final Intent intent = CalcOpponentCardService.getIntent(CardGameActivity.this, mBiddingResultReceiver, null, mStartingPlayer, mHasFourthPlayed, mIsTrumpRevealed, mTrumpCard);
					startService(intent);
				}
				else{
					allowBlockFourth(true);
				}
			}
		});
		
		int resourceID = R.drawable.usericon;
		if(player != null){
			switch (player) {
			case FIRST:
				resourceID = R.drawable.player1_100;
				break;
				
			case SECOND:
				resourceID = R.drawable.player2_100;
				break;
				
			case THIRD:
				resourceID = R.drawable.player3_100;
				break;
				
			default:
				break;
			}
		}
		mPlayerImageView.setImageResource(resourceID);
		mPlayerBidTextView = (TextView) view.findViewById(R.id.playerBidTextView);
		mPlayerBidTextView.setText(biddingText);
		
		return view;
	}
	
	private ImageView showTrumpPlayer(){
		final ImageView imageView = new ImageView(this);
		imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		imageView.setScaleType(ScaleType.FIT_XY);
		imageView.setImageResource(R.drawable.trumpset);
		imageView.setOnClickListener(this);
		return imageView;
	}
	
	private void botsBidding(final boolean hasPlayerPassed){
		final Set<Map.Entry<Card, Integer>> set = mBiddingMap.entrySet();
		final List<Map.Entry<Card, Integer>> list1 = new ArrayList<Map.Entry<Card,Integer>>(set);		
		if(hasPlayerPassed){
			for(int i = 0; i < list1.size(); i++){
				Log.d(LOG_TAG, "list Value : "+list1.get(i));	
			}
			//doBotsBidding(list1, mTrumpValue, 0, true);
			checkHighestBotBidding();
			Log.d(LOG_TAG, "mWhoMadeTrump : "+mWhoMadeTrump + "mTrumpValue : "+mTrumpValue);
			if(Level.Hard == mLevel){
				CardGameApplication.sort(mWhoMadeTrump);
			}
			/*addCards(mBottomMainLayout, Player.FOURTH, 0, 8);*/
			Log.d(LOG_TAG, mWhoMadeTrump +" has set trump on "+mTrumpValue);
			//Toast.makeText(this, mWhoMadeTrump + " has set trump on " + mTrumpValue, Toast.LENGTH_SHORT).show();
			/*resetSuit();*/
		}
		else{
			doPlayerBidding(list1, 0);
		}
		/*mPlayerWithKQ = checkKQ();*/
	}
	
	//Check if any player has KQ Pair
	private Player checkKQ(){
		final Player[] players = {Player.FIRST, Player.SECOND, Player.THIRD, Player.FOURTH};
		Player playerWithKQ = null;
		for(Player player : players){
			playerWithKQ = CardUtils.getPlayerWithKQ(player, mTrumpCard);
			if(playerWithKQ != null){
				break;
			}
		}
		Log.d(LOG_TAG, "Player "+ playerWithKQ + " has KQ ");
		return playerWithKQ;
	}
	
	private void showCards(final Card card){
		CardGameApplication.remove(card.getPlayer(), card);
		final int stackSize = mOtherPlayedCardList.size();
		final int drawable = CardUtils.getDrawable(CardGameActivity.this, card);
		switch (card.getPlayer()) {
		case FIRST:
			mLeftSelectedCardView.setImageResource(drawable);
			mLeftOnlineImageView.setVisibility(View.INVISIBLE);
			mTopOnlineImageView.setVisibility(View.VISIBLE);
			break;
			
		case SECOND:
			mTopSelectedCardView.setImageResource(drawable);
			mRightOnlineImageView.setVisibility(View.VISIBLE);
			mTopOnlineImageView.setVisibility(View.INVISIBLE);
			break;
			
		case THIRD:
			mRightSelectedCardView.setImageResource(drawable);
			mBottomOnlineImageView.setVisibility(View.VISIBLE);
			mRightOnlineImageView.setVisibility(View.INVISIBLE);
			break;

		default:
			break;
		}
		Log.d(LOG_TAG, "How many players have played : "+stackSize);
		Log.d(LOG_TAG, "Has fourth played : "+mHasFourthPlayed);
		Log.d(LOG_TAG, "Last Player was : "+card.getPlayer() + "id : "+card.getPlayer().getID());
		if(stackSize < 4){
			if(!mHasFourthPlayed && (card.getPlayer().getID()+1) == 4){
				boolean hasSuitCard = false;
				final List<Card> fourthCardList = CardGameApplication.getDeckCards(Player.FOURTH);
				int count = 0;
				for(final Card fourthCard : fourthCardList){
					final View view = mBottomPlayerLayout.getChildAt(count);
					if(fourthCard.has(mSuitHand)){
						view.setEnabled(true);
						mTrumpCardView.setEnabled(false);
						hasSuitCard = true;
					}
					else{
						if(view != null){
							view.setEnabled(false);
						}	
					}
					count++;
				}
				if(!hasSuitCard){
					mTrumpCardView.setEnabled(true);
					allowBlockFourth(true);
				}
				return;
			}			
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					Log.i(LOG_TAG, "Still player has to play so start service");
					mTrumpCardView.setEnabled(false);
					final Intent intent = CalcOpponentCardService.getIntent(CardGameActivity.this, mBiddingResultReceiver, mOtherPlayedCardList, null, false, mIsTrumpRevealed, mTrumpCard);
					startService(intent);
				}
			}, 3000);
		}
		else{
			Log.i(LOG_TAG, "All the players have played the card. Now evaluate ");
			offline();
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					mTrumpCardView.setEnabled(false);
					evaluate();
				}
			}, 3000);			
		}		
	}
	
	private void evaluate(){
		final Card lastHandCards = CardUtils.checkWhoIsWinning(mOtherPlayedCardList, mSuitHand, mIsTrumpRevealed, mTrumpCard);
		Log.d(LOG_TAG, lastHandCards.getPlayer()+" won this hand");
		/*if (mPlayerWithKQ != null && !mIsKQCheckDone) {
			mPlayerWithKQ = checkKQ();
			if (mPlayerWithKQ != null) {
				final boolean isSameTeamWhoWon = CardUtils.isSameTeam(lastHandCards.getPlayer(), mPlayerWithKQ);
				final boolean isSameTeam = CardUtils.isSameTeam(mWhoMadeTrump, mPlayerWithKQ);
				if(isSameTeamWhoWon && isSameTeam){
					//Then decrease the mTrumpValue to 16
					mTrumpValue = 16;
				}
			}
		}*/
		if(!mFinishGame){
			String player = null;
			if(lastHandCards.getPlayer().equals(Player.FOURTH)){
				player = "You have ";
			}
			else{
				player = lastHandCards.getPlayer().name();
			}
			Toast.makeText(CardGameActivity.this, player+" won this hand", Toast.LENGTH_SHORT).show();
		}
		addPoints(lastHandCards);
		refreshForNextHand();
		mCardDBHandler.savePlayedHand(lastHandCards);
		Log.d(LOG_TAG, "**********************   END   ******************************************");
		/*Log.d(LOG_TAG, "Card remaining : "+CardGameApplication.getDeckCards(PLAYER.FIRST));
		Log.d(LOG_TAG, "Card remaining : "+CardGameApplication.getDeckCards(PLAYER.SECOND));
		Log.d(LOG_TAG, "Card remaining : "+CardGameApplication.getDeckCards(PLAYER.THIRD));
		Log.d(LOG_TAG, "Card remaining : "+CardGameApplication.getDeckCards(PLAYER.FOURTH));*/
		Log.d(LOG_TAG, "deckSize : "+CardGameApplication.deckSize());
		Log.d(LOG_TAG, "**********************   START NEXT HAND   ******************************************");
		if(CardGameApplication.deckSize() == 0){
			if (!mFinishGame) {
				Toast.makeText(CardGameActivity.this, "Game Over",
						Toast.LENGTH_SHORT).show();
				/*
				 *Calculate who won the round.  
				 */
				Log.d(LOG_TAG, "Team A has made " + mOpponentPoints
						+ " Team B has made " + mAllyPoints);
				checkWhichTeamWon();
				updateScore();
				reset();
				mCardDBHandler.clearDB();
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						final Bundle bundle = new Bundle();
						bundle.putParcelable(CardConstants.RESULT_RECEIVER,
								mBiddingResultReceiver);
						showDialog(CardConstants.PLAY_ANOTHER_GAME, bundle);

					}
				}, 5000);
			}
			return;
		}
		if(!Player.FOURTH.equals(lastHandCards.getPlayer())){
			allowBlockFourth(false);
			new Handler().postDelayed(new Runnable() {
				
			@Override
				public void run() {
					Log.i(LOG_TAG, "Evaluation has been done . Last hand was won by player other than FOURTH ");
					final Intent intent = CalcOpponentCardService.getIntent(CardGameActivity.this, mBiddingResultReceiver, null, lastHandCards.getPlayer(), mHasFourthPlayed, mIsTrumpRevealed, mTrumpCard);
					startService(intent);
				}
			}, 1000);
		}else{
			mBottomOnlineImageView.setVisibility(View.VISIBLE);
			allowBlockFourth(true);
		}
	}
	
	private void updateScore(){
		
		/********************************************************************
		 * RIGHT SIDE SCORE POINTS (ALLY)
		 *********************************************************************/
		
		TextView textView = (TextView) findViewById(R.id.allyScoreTextView);
		/*StringBuilder sb = new StringBuilder(getString(R.string.ally_score));
		sb.append("\n");
		sb.append(Math.abs(mTeamBPoints));
		textView.setText(sb.toString());*/
		textView.setText(String.valueOf(Math.abs(mTeamBPoints)));
		textView.setTextSize(20);
		textView.setTypeface(Typeface.DEFAULT_BOLD);
		int resourceID = 0;
		if(mTeamBPoints >= 0){
			resourceID = R.drawable.hearts_32;
		}
		else{
			resourceID = R.drawable.spades_32;
		}
		textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, resourceID, 0);
		/*List<ArrayList<Integer>> scoreIconList = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < 2; i++){
			final List<Integer> list = new ArrayList<Integer>();
			for(int j = 0; i < 3; i++){
				list.add(resourceID);/home/sunny/Desktop/card_images/diamond1.png
			}
		}
		
		final ScoreAdapter scoreAdapter = new ScoreAdapter(this, scoreIconList);
		leftGridView.setAdapter(scoreAdapter);*/
		/********************************************************************
		 * LEFT SIDE SCORE POINTS (OPPONENT)
		 *********************************************************************/
		
		textView = (TextView) findViewById(R.id.opponentScoreTextView);
		/*sb = new StringBuilder(getString(R.string.opponent_score));
		sb.append("\n");
		sb.append(Math.abs(mTeamAPoints));
		textView.setText(sb.toString());*/
		textView.setText(String.valueOf(Math.abs(mTeamAPoints)));
		textView.setTextSize(20);
		textView.setTypeface(Typeface.DEFAULT_BOLD);		
		if(mTeamAPoints >= 0){
			resourceID = R.drawable.diamond_32;
		}
		else{
			resourceID = R.drawable.club_32;
		}
		textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, resourceID, 0);
	}
	
/*	private void showScore(final int resourceID, final int score){
		final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.leftScorelayout);
		final TextView textView = (TextView) findViewById(R.id.opponentScoreTextView);
		final int absoluteValue = Math.abs(score);
		int cardScore = 6;
		if(absoluteValue > 0 && absoluteValue <= 6){
			cardScore = 6;
		}
		else if(absoluteValue > 6 && absoluteValue <= 11){
			cardScore = 5;
		}
		else if(absoluteValue > 11 && absoluteValue <= 15){
			cardScore = 4;
		}
		else if(absoluteValue > 15 && absoluteValue <= 18){
			cardScore = 3;
		}
		else if(absoluteValue > 18 && absoluteValue <= 20){
			cardScore = 2;
		}
		addLayout(cardScore, resourceID);
	}
	
	private LinearLayout addLayout(final int value, final int resourceID){
		final LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		for(int i = 0; i < 2; i++){
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setImageResource(resourceID);
			linearLayout.addView(imageView);
			if(value < 2){
				break;
			}
		}
		return linearLayout;
	}
	*/
	private void addPoints(final Card card){
		final int points = CardUtils.getPoints(mOtherPlayedCardList);
		switch (card.getPlayer()) {
		case FIRST:
		case THIRD:	
			mOpponentPoints += points;
			break;

		case SECOND:
		case FOURTH:
			mAllyPoints += points;
			break;
			
		default:
			break;
		}
	}
	
	private void checkWhichTeamWon(){
		TEAM team = null;
		//Check won made the trump first
		switch (mWhoMadeTrump) {
		case FIRST:
		case THIRD:
			team = TEAM.TEAM_A;
			break;
			
		case SECOND:
		case FOURTH:
			team = TEAM.TEAM_B;
			break;

		default:
			break;
		}
		
		Log.d(LOG_TAG, "mTrumpValue : "+ mTrumpValue + " mOpponentPoints : "+mOpponentPoints + " mAllyPoints : "+mAllyPoints);
		switch (team) {
		case TEAM_A:
			if(mOpponentPoints >= mTrumpValue){
				Toast.makeText(this, "Team A has won", Toast.LENGTH_SHORT).show();
				if(mDouble){
					mTeamAPoints += 2;
				}
				else{
					mTeamAPoints++;
				}	
			}
			else{
				Toast.makeText(this, "Team A has lost by "+(mTrumpValue - mOpponentPoints) + " points", Toast.LENGTH_SHORT).show();
				if(mDouble){
					mTeamAPoints -= 2;
				}
				else{
					mTeamAPoints--;
				}
			}
			break;
			
		case TEAM_B:
			if(mAllyPoints >= mTrumpValue){
				Toast.makeText(this, "Team B has won", Toast.LENGTH_SHORT).show();
				if(mDouble){
					mTeamBPoints += 2;
				}
				else{
					mTeamBPoints++;
				}
			}
			else{
				Toast.makeText(this, "Team B has lost by "+(mTrumpValue - mAllyPoints) + " points", Toast.LENGTH_SHORT).show();
				if(mDouble){
					mTeamBPoints -= 2;
				}
				else{
					mTeamBPoints--;
				}
			}			
			break;			

		default:
			break;
		}		
	}
	
	private void refreshForNextHand(){
		mOtherPlayedCardList.clear();
		mSuitHand = null;
		mTopSelectedCardView.setImageResource(R.drawable.back1);
		mLeftSelectedCardView.setImageResource(R.drawable.back1);
		mRightSelectedCardView.setImageResource(R.drawable.back1);
		mBottomSelectedCardView.setImageResource(R.drawable.back1);
		mHasFourthPlayed = false;
	}
	
	protected void reset(){
		/*mIsKQCheckDone = false;*/
		mSuitHand = null;
		mTrumpCard = null;
		mBottomPlayerLayout.removeAllViews();
		mLeftPlayerLayout.removeAllViewsInLayout();
		mTopPlayerLayout.removeAllViewsInLayout();
		mRightPlayerLayout.removeAllViewsInLayout();
		mHasFourthPlayed = false;
		mAllyPoints = 0;
		mOpponentPoints = 0;
		mWhoMadeTrump = null;
		mIsTrumpRevealed = false;
		mPassedPlayerMap.put(Player.FIRST, false);
		mPassedPlayerMap.put(Player.SECOND, false);
		mPassedPlayerMap.put(Player.THIRD, false);
		/*CardGameApplication.reset();*/
		mTrumpCardView.setImageResource(R.drawable.back1);
		int playerId = 4;
		Log.d(LOG_TAG, "Starting player : "+mStartingPlayer + "playerId "+mStartingPlayer.getID());
		if(mStartingPlayer.getID() == 4){
			playerId = 0;
		}
		else{
			playerId = mStartingPlayer.ordinal()+1;
		}
		Log.d(LOG_TAG, "Starting player after resetting : "+mStartingPlayer + " playerId "+playerId);
		mStartingPlayer = Player.values()[playerId];
		mDouble = false;
		super.reset();
	}

	@Override
	public void onClick(final View view) {
	final Object object = view.getTag();
		if(object != null && object instanceof Card){
			final Card card = (Card) object;
			final Player player = card.getPlayer();
			final int drawable = CardUtils.getDrawable(CardGameActivity.this, card);
			switch (player) {
			
			/*
			 * When you click on a card. BOTS should start playing their cards too
			 * Remove the card from the CardDeck for the fourth player
			 * 1) Check the same suit for the 1st, 2nd , 3rd player
			 * 2) If present then check which bot it is, If second player then play point card
			 * 3) If not present then play other SUIT card.
			 */
			case FOURTH:
				allowBlockFourth(false);
				mOtherPlayedCardList.add(card);		//add the played card to the list which contains other player played cards
				mBottomSelectedCardView.setImageResource(drawable);
				mBottomPlayerLayout.removeView(view);
				CardGameApplication.remove(player, card);
				mBottomOnlineImageView.setVisibility(View.INVISIBLE);
				mLeftOnlineImageView.setVisibility(View.VISIBLE);				
				if(mOtherPlayedCardList.size() < 4){
					Log.i(LOG_TAG, "onClick start service");
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							final Intent intent = CalcOpponentCardService.getIntent(
									CardGameActivity.this, mBiddingResultReceiver,
									mOtherPlayedCardList, null, true, mIsTrumpRevealed,
									mTrumpCard);
							startService(intent);
						}
					}, 2000);					
				}
				else{
					offline();
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							evaluate();
						}
					}, 3000);
				}
				break;

			default:
				break;
			}
		}
		else{
			//final Object obj = view.getTag();
			final int id = view.getId();
			if(id == R.id.trumpCard){
			/*if (obj != null && obj instanceof String) {
				final String tag = (String) obj;
				if (tag.equals(TAG_TRUMP)) {*/
					final List<Card> fourthCardDeck = CardGameApplication
							.getDeckCards(Player.FOURTH);
					if (!fourthCardDeck.contains(mSuitHand)) {
						mTrumpCardView.setImageResource(getTrumpCardID());
						mIsTrumpRevealed = true;
					}
					return;
				//}
			}
			super.onClick(view);
		}
	}
	
	private int getTrumpCardID(){
		int colorID = 0;
		mIsTrumpRevealed = true;
		switch (mTrumpCard) {
		case SPADES:
			colorID = R.drawable.spades2; 
			break;
			
		case CLUB:
			colorID = R.drawable.club2; 
			break;
			
		case DIAMOND:
			colorID = R.drawable.diamond2; 
			break;
			
		case HEARTS:
			colorID = R.drawable.hearts2; 
			break;					

		default:
			colorID = R.drawable.back1;
			break;
		}
		return colorID;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			showDialog(CardConstants.BACK_BUTTON_PRESS);
			break;

		default:
			break;
		}
		return true;
	}
}

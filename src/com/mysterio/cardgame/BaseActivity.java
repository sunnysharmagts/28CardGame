package com.mysterio.cardgame;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.Toast;

import com.mysterio.cardgame.model.Card;
import com.mysterio.cardgame.model.Card.Player;
import com.mysterio.cardgame.model.Card.Suit;
import com.mysterio.cardgame.services.CalcOpponentCardService;
import com.mysterio.mycardgame.R;

/**
 * This is the Root Activity which does all the activity based common code
 */
public class BaseActivity extends Activity implements View.OnClickListener{

	private Button mOkButton;
	private Dialog mDialog;
	private NumberPicker mNumberPicker;
	private CheckBox mCheckBox;
	private int mBiddingValue;
	private SharedPreferences mSharedPreferences;
	private ResultReceiver mResultReceiver;
	private Suit mSuit;
	private View mPreviousView;
	private static final String BIDDING_OK = "bidding_ok";
	private static final String LOG_TAG = "BaseActivity";
	private AlertDialog alertDialog;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mBiddingValue = 16;
		CardGameApplication.reset();
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	protected void reset(){
		mBiddingValue = 16;
		CardGameApplication.reset();
		mCheckBox.setChecked(false);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
		if(bundle != null){
			mResultReceiver = bundle.getParcelable(CardConstants.RESULT_RECEIVER);
			mBiddingValue = bundle.getInt(CardConstants.BIDDING_VALUE, 16);
		}
		switch(id) {
		case CardConstants.BIDDING:
			Log.d(LOG_TAG, "The bidding value to set is : "+mBiddingValue);
			dialog.setContentView(getView());
			dialog.setCancelable(false);
			dialog.show();
			break;
			
			default:
				break;
		}
		//super.onPrepareDialog(id, dialog, bundle);
	}
	
	@Override
	protected Dialog onCreateDialog(final int id, final Bundle bundle) {
		Log.d(LOG_TAG, "Bundle : "+bundle);
		if(bundle != null){
			mResultReceiver = bundle.getParcelable(CardConstants.RESULT_RECEIVER);
			mBiddingValue = bundle.getInt(CardConstants.BIDDING_VALUE, 16);
			Log.d(LOG_TAG, "onCreateDialog() bidding value is : "+mBiddingValue);
		}
		switch(id) {
		case CardConstants.BIDDING:
			mDialog = new Dialog(this);
			mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mDialog.setContentView(getView());
			mDialog.setCancelable(false);
			mDialog.show();
			break;
			
		case CardConstants.SHOW_BID_PLAYER:
			final Player player = (Player) bundle.getSerializable(CardConstants.PLAYER);
			final String biddingText = bundle.getString(CardConstants.BIDDING_VALUE);
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
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.icon);
			builder.setMessage(biddingText);
			builder.setCancelable(false);
			alertDialog = builder.create();
			alertDialog.show();
			return alertDialog;
			
		case CardConstants.PLAY_ANOTHER_GAME:
			try {
				builder = new AlertDialog.Builder(this);
				builder.setMessage(getString(R.string.play_another_game));
				builder.setCancelable(false);
				builder.setPositiveButton(getString(android.R.string.yes), new OnClickListener() {
					
					@Override
					public void onClick(final DialogInterface dialog, int which) {
						dialog.dismiss();
						mResultReceiver.send(CardConstants.PLAY_ANOTHER_GAME, null);
					}
				});
				builder.setNegativeButton(getString(R.string.no), new OnClickListener() {
					
					@Override
					public void onClick(final DialogInterface dialog, int which) {
						setResult(RESULT_OK);
						finish();
					}
				});
				
				alertDialog = builder.create();
				alertDialog.show();
			} catch (final Exception e) {
				Log.e(LOG_TAG, e.getMessage());
			}
			return alertDialog;	
			
		case CardConstants.BACK_BUTTON_PRESS:
			builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.exit_game));
			builder.setCancelable(false);
			builder.setPositiveButton(getString(android.R.string.ok), new OnClickListener() {
				
				@Override
				public void onClick(final DialogInterface dialog, int which) {
					final Intent intent = CalcOpponentCardService.getIntent(BaseActivity.this, null, null, null, false, false, null);
					stopService(intent);
					/*final CardDBHandler cardDBHandler = new CardDBHandler(BaseActivity.this, CardConstants.DB_NAME, null, CardConstants.DB_VERSION);
					cardDBHandler.clearDB();*/
					final Bundle bundle = new Bundle();
					bundle.putBoolean(CardConstants.FINISH_GAME, true);
					mResultReceiver.send(CardConstants.BACK_BUTTON_PRESS, bundle);
					//finish();
				}
			});
			builder.setNegativeButton(getString(R.string.cancel), new OnClickListener() {
				
				@Override
				public void onClick(final DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			
			alertDialog = builder.create();
			alertDialog.show();
			return alertDialog;
			
		default:
			break;
		}
		return mDialog;
	}
	
	private View getView(){
		final List<Card> biddingCardList = CardGameApplication.getDeckCards(Player.FOURTH);
		final View view = LayoutInflater.from(this).inflate(R.layout.bidding_dialog, null);
		final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.biddingCardLayout);
		mOkButton = (Button) view.findViewById(R.id.okButton);
		mOkButton.setTag(BIDDING_OK);
		mCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
		mNumberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
		mNumberPicker.setValue(mBiddingValue);
		mNumberPicker.setMinValue(mBiddingValue);
		mNumberPicker.setMaxValue(28);
		mNumberPicker.setWrapSelectorWheel(true);
		mNumberPicker.setOnValueChangedListener(biddingValueChangedListener);
		mOkButton.setOnClickListener(this);
		if(biddingCardList != null){
			boolean flag = true;
			for(int i = 0; i < 4; i++){
				final Suit suit = biddingCardList.get(i).getSuit();
				final ImageView imageView = new ImageView(this);
				final StringBuilder stringBuilder = new StringBuilder(suit.name().toLowerCase());
				stringBuilder.append(biddingCardList.get(i).getNumber().getID());
				final int drawable = getResources().getIdentifier(stringBuilder.toString(), "drawable", getPackageName());
				imageView.setImageResource(drawable);
				imageView.setPadding(3, 3, 3, 3);
				imageView.setOnClickListener(this);
				imageView.setBackgroundColor(Color.TRANSPARENT);
				imageView.setTag(suit);
				linearLayout.addView(imageView);
				if(flag && mSuit != null && mSuit.equals(suit)){
					imageView.setBackgroundColor(Color.RED);
					flag = false;
				}
			}
		}
		return view;
	}
	
	protected boolean hasPlayerPassed(){
		return mCheckBox.isChecked();
	}
	
	protected int getBiddingValue(){
		return mBiddingValue;
	}
	
	protected void resetSuit(){
		mSuit = null;
	}
	
	final OnValueChangeListener biddingValueChangedListener = new OnValueChangeListener() {
		
		@Override
		public void onValueChange(final NumberPicker picker, final int oldVal, final int newVal) {
			mBiddingValue = newVal;
		}
	};

	@Override
	public void onClick(final View view) {
		final Object object = view.getTag();		
		Log.i(LOG_TAG, "BaseActivity onClicked() called ");
		if(object != null && object instanceof Suit){
			if(mPreviousView != null){
				mPreviousView.setBackgroundColor(Color.TRANSPARENT);
			}
			view.setBackgroundColor(Color.RED);
			mPreviousView = view;
			mSuit = (Suit) object;
			final Editor editor = mSharedPreferences.edit();
			editor.putString(CardConstants.TRUMP_CARD, mSuit.name());
			editor.putBoolean(CardConstants.HAS_PLAYER_PASSED, mCheckBox.isChecked());
			editor.commit();
			return;
		}
		
		if(object != null && (object instanceof String)){
			final String stringTag = object.toString();
			if((mSuit != null || mCheckBox.isChecked()) && stringTag.equals(BIDDING_OK)){
				Log.i(LOG_TAG, "Ok Button Clicked ");
				mDialog.dismiss();
				if(mResultReceiver != null){
					Log.i(LOG_TAG, "Bidding Done ");
					mResultReceiver.send(CardConstants.BIDDING_DONE, null);
				}
			}
			else{
				Toast.makeText(getApplicationContext(), "Please select a suit.", Toast.LENGTH_SHORT).show();
			}
		}
	}
}

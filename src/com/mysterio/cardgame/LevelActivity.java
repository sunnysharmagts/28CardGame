package com.mysterio.cardgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.mysterio.cardgame.model.Level;
import com.mysterio.mycardgame.R;

public class LevelActivity extends BaseActivity implements OnClickListener{

	private Button mEasyButton;
	private Button mHardButton;
	private AdView mAdView;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		setContentView(R.layout.level);
		super.onCreate(savedInstanceState);
		mAdView = new AdView(this);
		mAdView.setAdSize(AdSize.BANNER);
		mAdView.setAdUnitId(CardConstants.AD_UNIT_ID);
		mEasyButton = (Button) findViewById(R.id.easy);
		mHardButton = (Button) findViewById(R.id.hard);
		mEasyButton.setOnClickListener(this);
		mHardButton.setOnClickListener(this);
		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.mainLayout);
		relativeLayout.addView(mAdView);
	    // Create an ad request. Check logcat output for the hashed device ID to
	    // get test ads on a physical device.
	    AdRequest adRequest = new AdRequest.Builder().build();

	    // Start loading the ad in the background.
	    mAdView.loadAd(adRequest);
	}
	
	  @Override
	  public void onResume() {
	    super.onResume();
	    if (mAdView != null) {
	      mAdView.resume();
	    }
	  }

	  @Override
	  public void onPause() {
	    if (mAdView != null) {
	      mAdView.pause();
	    }
	    super.onPause();
	  }

	  /** Called before the activity is destroyed. */
	  @Override
	  public void onDestroy() {
	    // Destroy the mAdView.
	    if (mAdView != null) {
	      mAdView.destroy();
	    }
	    super.onDestroy();
	  }	
	
	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.easy){
			final Intent intent = new Intent(LevelActivity.this, CardGameActivity.class);
			intent.putExtra(CardConstants.LEVEL, Level.Easy);
			startActivity(intent);
		}
		else if(view.getId() == R.id.hard){
			final Intent intent = new Intent(LevelActivity.this, CardGameActivity.class);
			intent.putExtra(CardConstants.LEVEL, Level.Hard);
			startActivity(intent);			
		}
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}
}

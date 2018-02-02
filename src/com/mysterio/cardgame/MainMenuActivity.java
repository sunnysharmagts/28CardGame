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
import com.mysterio.mycardgame.R;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;

public class MainMenuActivity extends BaseActivity implements OnClickListener{

	private Button mPlayButton;
	private Button mAboutButton;
	private Button mExitButton;
	private AdView mAdView;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		AdBuddiz.showAd(this);
		mAdView = new AdView(this);
		mAdView.setAdSize(AdSize.BANNER);
		mAdView.setAdUnitId(CardConstants.AD_UNIT_ID);		
		mPlayButton = (Button) findViewById(R.id.play);
		mAboutButton = (Button) findViewById(R.id.about);
		mExitButton = (Button) findViewById(R.id.exit);
		mPlayButton.setOnClickListener(this);
		mAboutButton.setOnClickListener(this);
		mExitButton.setOnClickListener(this);
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
	public void onClick(final View view) {
		switch (view.getId()) {
		case R.id.play:
			startActivity(new Intent(MainMenuActivity.this, LevelActivity.class));
			break;
			
		case R.id.about:
			startActivity(new Intent(MainMenuActivity.this, AboutUsActivity.class));
			break;
			
		case R.id.exit:
			finish();

		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}
	
}

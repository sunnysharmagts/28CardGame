package com.mysterio.cardgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.mysterio.mycardgame.R;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;

public class SplashScreenActivity extends Activity implements Runnable{
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		AdBuddiz.setPublisherKey("a6c2f2d5-48e1-4221-868c-a7af7a59a45d");
	    AdBuddiz.cacheAds(this);        
        setContentView(R.layout.splash);
        new Handler().postDelayed(this, 3000);
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void run() {
		startActivity(new Intent(this, MainMenuActivity.class));
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		finish();
	}
}


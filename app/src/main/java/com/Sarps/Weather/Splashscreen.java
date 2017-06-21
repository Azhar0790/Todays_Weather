package com.Sarps.Weather;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


public class Splashscreen extends Activity
{
	RelativeLayout rl_splash;
	private static int splash_screen=5000;
	private ProgressBar pb_prog;
	private ImageView cloudthree,cloudtwo,cloudone;
	 Animation animation2;
	public final String[] PERMISSION_ALL = {
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.INTERNET
	};
	public final int PERMISSION_REQUEST_CODE = 100;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen);
		int currentapiVersion = Build.VERSION.SDK_INT;
		if (currentapiVersion >= Build.VERSION_CODES.M) {
			ActivityCompat.requestPermissions(Splashscreen.this, PERMISSION_ALL, PERMISSION_REQUEST_CODE);
		}
		cloudone = (ImageView) findViewById(R.id.cloudone);
		cloudtwo = (ImageView) findViewById(R.id.cloudtwo);
		rl_splash = (RelativeLayout) findViewById(R.id.rl_splash);

		cloudthree = (ImageView) findViewById(R.id.cloudthree);


		 Display display = getWindowManager().getDefaultDisplay();

		float width = (float)(display.getWidth())/2;
		TranslateAnimation movement = new TranslateAnimation(-width,width+400,0.0f,0.0f);
		movement.setDuration(8000);
		movement.setRepeatCount(-1);
		//	movement.setRepeatMode(1);

		animation2 = AnimationUtils.loadAnimation(this, R.anim.translate);
		cloudone.startAnimation(movement);
		cloudtwo.startAnimation(movement);
		cloudthree.startAnimation(movement);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Splashscreen.this.finish();
				Intent i=new Intent(Splashscreen.this,MainActivity.class);
				startActivity(i);
			}
		}, splash_screen);
	}
}

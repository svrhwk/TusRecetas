package com.example.tusrecetas;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

public class Prueba extends Activity {

	private static final String LL1_VISIBILITY = "ll1_visibility";
	LinearLayout ll1;
	LinearLayout ll2;
	boolean ll1_vis=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_receta3);
		ll1 = (LinearLayout) findViewById(R.id.ll1);
		ll2 = (LinearLayout) findViewById(R.id.ll2);
		if(savedInstanceState!=null){
			ll1_vis=savedInstanceState.getBoolean(LL1_VISIBILITY);
			if(ll1_vis){
				ll1.setVisibility(View.VISIBLE);
			}else{
				ll1.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.prueba, menu);
		return true;
	}
	public void clickbtn(View view){
		ll1_vis=!ll1_vis;
		if(ll1_vis){
			ll1.setVisibility(View.VISIBLE);
		}else{
			ll1.setVisibility(View.GONE);
		}
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBoolean(LL1_VISIBILITY, ll1_vis);
		
	}
}

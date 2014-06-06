package com.odeme.keypay;

import java.util.Locale;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends Activity{
	private static final String UYARI="setting_lang";
	RadioButton rdB_TR;
	RadioButton rdB_EN;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setLocale(Keypay_Odeme.getSys_Language());
		setContentView(R.layout.settings);
		loadSetting();
		
		rdB_TR=(RadioButton)findViewById(R.id.rdB_TR);
		rdB_EN=(RadioButton)findViewById(R.id.rdB_EN);

		final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.switchcontainer);
		radioGroup.setOnCheckedChangeListener(this.radioGroup);
		
		Button btn_language=(Button)findViewById(R.id.btnLanguages);
		View panelProfile = findViewById(R.id.panelProfile);
		panelProfile.setVisibility(View.GONE);
		
		btn_language.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// DO STUFF				
				View panelProfile = findViewById(R.id.panelProfile);
				
				if(panelProfile.getVisibility()==8)//panel kapalý demek
					panelProfile.setVisibility(View.VISIBLE);
				else
					panelProfile.setVisibility(View.GONE);

			}
		});
	}
	
	private void loadSetting()
	{
		SharedPreferences app_preferences = 
	        	PreferenceManager.getDefaultSharedPreferences(this);
        
	        // Get the value for the run counter
	        String lang=app_preferences.getString(UYARI, "TR");
	        if(lang.equals("TR"))
	        	((RadioButton)findViewById(R.id.rdB_TR)).setChecked(true);	        		        
	        else if(lang.equals("EN"))
	        	((RadioButton)findViewById(R.id.rdB_EN)).setChecked(true);	        		         
	        	     	
	}
	private void setLocale(String str)
	{
		Configuration c = new Configuration();
		if(str.equals("TR"))
	       c.locale = new Locale("tr","TR");
		else
			c.locale=new Locale("en","US");
	    getResources().updateConfiguration(c, null);
	}

	RadioGroup.OnCheckedChangeListener radioGroup=new RadioGroup.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			String str="";
			if(rdB_TR.isChecked())
				str="TR";
			else
				str="EN";		
			
			SharedPreferences ayar=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = ayar.edit();
	        editor.putString(UYARI, str);
	        editor.commit(); // Very important		
	        Keypay_Odeme.setSys_Language(str);
	        setLocale(str);
			setContentView(R.layout.settings);
			loadSetting();
			finish();
		}
	};
}

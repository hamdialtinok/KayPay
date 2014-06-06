package com.odeme.keypay;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class Keypay_Odeme extends TabActivity {
    /** Called when the activity is first created. */
	
	private static String sys_Language;
	private static final String UYARI="setting_lang";
	private static final String _ISNEW="setting_isnew";
	private static int IType;
	private boolean isNew=true;
	private static boolean flg=true;
	
    public static String getSys_Language() {
		return sys_Language;
	}

	public static void setSys_Language(String sys_Language) {
		Keypay_Odeme.sys_Language = sys_Language;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Keypay_Odeme.setSys_Language(getLanguage());
        setLocale(getSys_Language());
        setContentView(R.layout.main);             
        
        
        //Ýnternet baðlantýsý kontrol...
        NetworkControl();
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, Mobile.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("mobile").setIndicator("",
                          res.getDrawable(R.drawable.ic_tab_mobile))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, Pos.class);
        spec = tabHost.newTabSpec("pos").setIndicator("",
                          res.getDrawable(R.drawable.ic_tab_pos))
                      .setContent(intent);
        tabHost.addTab(spec);
        tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.tab_slected);
        tabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.tab_slected);
        tabHost.setCurrentTab(0);
        
        //Sisteme kayýt ekranýnýn açýlmasý. Ýlk yüklendiðinde açýlacak.
        String asd=user_isNew();
        if(flg==true && (asd.equals("False")||asd=="False")){        	
        	showNewUserLayout();
        }
        else{
        	//enterUserShow();  //Programa þifre ile giriþ yapma. Kaldýrýldý...
        }
                    
    }

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		NetworkControl();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater menuInflater=getMenuInflater();
		menuInflater.inflate(R.menu.settingmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.clear();
		MenuInflater menuInflater=getMenuInflater();
		menuInflater.inflate(R.menu.settingmenu, menu);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		//Toast.makeText(getApplicationContext(),R.string.algorithm, Toast.LENGTH_SHORT).show();		
		Intent menuIntent=new Intent(this,SettingsActivity.class);
		startActivity(menuIntent);
		return super.onOptionsItemSelected(item);
	}
	private String getLanguage()//seçilen dilin xml den okunmasý
	{
		SharedPreferences app_preferences = 
	        	PreferenceManager.getDefaultSharedPreferences(this);
	        
	        // Get the value for the run counter
		String lang=app_preferences.getString(UYARI, "TR");
		
		return lang;
	}
	private void setLocale(String str)// dili deðiþtirme
	{
		Configuration c = new Configuration();
		if(str.equals("TR"))
	       c.locale = new Locale("tr","TR");
		else
			c.locale=new Locale("en","US");
	    getResources().updateConfiguration(c, null);
	}
	private void showNewUserLayout()//ilk kayýt ekraný
	{
		final Dialog dialog = new Dialog(Keypay_Odeme.this);
        dialog.setContentView(R.layout.new_user);
        dialog.setTitle(R.string.dialog_newuser);
        dialog.setCancelable(false);
        
        final TextView txtNewUserPass=(TextView)dialog.findViewById(R.id.txtNewUserPass1);
        final TextView txtNewUserPass2=(TextView)dialog.findViewById(R.id.txtNewUserPass2);
        Button btnNewUser=(Button)dialog.findViewById(R.id.btn_newuser);
        Button btnNewUser_Cancel=(Button)dialog.findViewById(R.id.btn_newuser_cancel);
        final Spinner spnNewUser=(Spinner)dialog.findViewById(R.id.spinNewUser);
        
        btnNewUser.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!txtNewUserPass.getText().toString().equals(txtNewUserPass2.getText().toString()))
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_PassControlFirst), Toast.LENGTH_SHORT).show();
				else if(txtNewUserPass.getText()==null||txtNewUserPass2.getText()==null)
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_newPassControl), Toast.LENGTH_SHORT).show();
				else{
					String cntrl= sendNewUserInfo(txtNewUserPass.getText().toString(), spnNewUser.getSelectedItem().toString());
					if(cntrl.equals("TRUE")){
					newUserFirst("FALSE");
					dialog.cancel();
					}
					else
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.service_newuser), Toast.LENGTH_SHORT).show();
				}
			}
		});
        
        btnNewUser_Cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.exit(1);
			}
		});
        
        dialog.show();
	}
	private String user_isNew()//kullanýcý ilk kontrol
	{
		TelephonyManager tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    String pNum=tm.getLine1Number();
		return checkUserInfo(pNum);
	}
	protected void newUserFirst(String str)
	{
		SharedPreferences ayar=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = ayar.edit();
        editor.putString(_ISNEW, str);
        editor.commit(); // Very important		
	}
	private String sendNewUserInfo(String pw2,String bank)//Get Password butonuna basýldýktan sonra bilgileri gönderme
	  {		  
		TelephonyManager tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    String pNum=tm.getLine1Number();
	      final String pNum_cryp=Mobile.getCryptionWord(pNum);
	      final String bank_cryp=Mobile.getCryptionWord(bank);
	      final String pw2_cryp=Mobile.getCryptionWord(pw2);
	      
	      	      
	      JSONObject json = new JSONObject();			
			try {
				json.put("Number", pNum_cryp);
				json.put("PW2", pw2_cryp);
				json.put("Bank", bank_cryp);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			httpSend ftpServer=new httpSend();
			String resultSend= ftpServer.SendData(json, "wsAddNewUser");
			return resultSend;
	  }
	private void enterUserShow()//Þifre ile giriþ yapma
	{
		final Dialog dialog = new Dialog(Keypay_Odeme.this);
        dialog.setContentView(R.layout.giris);
        dialog.setTitle(R.string.btn_login);
        dialog.setCancelable(false);
        
        final TextView txtUserPass=(TextView)dialog.findViewById(R.id.txtUserPass2);
        final CheckBox cbUser=(CheckBox)dialog.findViewById(R.id.cBGiris);
        Button btnUser=(Button)dialog.findViewById(R.id.btn_userGiris);  
        Button btnCancel=(Button)dialog.findViewById(R.id.btn_login_cancel);
        
        TelephonyManager tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    final String pNum=tm.getLine1Number();
        
        btnUser.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(txtUserPass.getText().toString().equals(""))
					Toast.makeText(getApplicationContext(),getResources().getString(R.string.txt_newPassControl), Toast.LENGTH_SHORT).show();
				else
				{
					if(checkUserInfo( pNum).equals("True"))
						dialog.cancel();
					else
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_newUserControl), Toast.LENGTH_SHORT).show();
				}
			}
		});
        
        cbUser.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str="";
				if(cbUser.isChecked())
				{
					IType=txtUserPass.getInputType();
					str=txtUserPass.getText().toString();
					txtUserPass.setInputType(InputType.TYPE_CLASS_NUMBER);
					txtUserPass.setText(str);					
				}
				else
				{
					str=txtUserPass.getText().toString();
					txtUserPass.setInputType(IType);
					txtUserPass.setText(str);
				}				
			}
		});
        
        btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.exit(1);
			}
		});
        
        dialog.show();
	}
	private String checkUserInfo(String pNum)//Þifre iel giriþ yapma. Kullanýcý þifresi kontrol etme
	  {		  
		
	      final String pNum_cryp=Mobile.getCryptionWord(pNum);
	      
	      	      
	      JSONObject json = new JSONObject();			
			try {
				json.put("Number", pNum_cryp);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			httpSend ftpServer=new httpSend();
			String resultSend= ftpServer.SendData(json, "wsCheckUser");	
			return resultSend;
	  }
	private void NetworkControl()
	{
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);	
        
        final android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
               
        if(!wifi.isConnectedOrConnecting()&&!mobile.isConnectedOrConnecting())
        {
        	flg=false;
        	AlertDialog.Builder builder=new AlertDialog.Builder(this);
        	builder.setMessage(R.string.ad_network_msg).setCancelable(false).setPositiveButton(R.string.ad_active_yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent net=new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
					startActivity(net);
				}
			});
        	builder.setNegativeButton(R.string.ad_active_no, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
					System.exit(1);
				}
			});
        	AlertDialog alert=builder.create();
        	alert.show();
        }
      //...... SON.....Ýnternet baðlantýsý kontrol...
	}
}
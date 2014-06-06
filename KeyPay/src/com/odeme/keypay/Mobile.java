package com.odeme.keypay;

import java.util.Locale;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Mobile extends Activity {
	
	private static String algo="";
	private static int bit=0;
	private static Random mRnd = new Random();
	private static int countOfunique=6;
	private Spinner banks=null;
	private String uniqueCode="";
	private String resultSend="";
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    private static String Latitude="";
    private static String Longitude="";
    
    protected LocationManager locationManager;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        setLocale(Keypay_Odeme.getSys_Language());
        setContentView(R.layout.mobilemain);            
        
        loadElementofLayout();
        
        algo=this.getString(R.string.algorithm);
        bit=Integer.parseInt(this.getString(R.string.getBit));
        loadUserInfo();
        
        //Setup Location Manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);        
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 
                MINIMUM_TIME_BETWEEN_UPDATES, 
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                new MyLocationListener()
        );
    }
    
    //Bank Spinner Click...
	OnItemSelectedListener listener=new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			
		    User.setBank(banks.getSelectedItem().toString());
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
    //Get Password Function...
    OnClickListener GetPassword = new OnClickListener() {
        public void onClick(View v) {
          new GetPassTask().execute();
        }
    };
      
    //Get information about how does this system work or what is this...
	View.OnClickListener About = new View.OnClickListener() {
	    public void onClick(View v) {

	    	// it was the 1st button
	    	final Dialog dialog = new Dialog(Mobile.this);
            dialog.setContentView(R.layout.about);
            dialog.setTitle(R.string.dialog_about);
            dialog.setCancelable(true);
            //there are a lot of settings, for dialog, check them all out!
            
            //set up text
            TextView text = (TextView) dialog.findViewById(R.id.TextView01);            
            text.setText(R.string.txt_about);
                        
            
            //set up image view
            ImageView img = (ImageView) dialog.findViewById(R.id.ImageView01);
            img.setImageResource(R.drawable.logo);

            //set up button
            Button button = (Button) dialog.findViewById(R.id.Button01);
            //button.setText(mylist.get(0).)
            button.setOnClickListener(new OnClickListener() {
            @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            //now that the dialog is set up, it's time to show it    
            dialog.show();
	    }
	  };
	  
	//System or Application Quit...
	View.OnClickListener Quit = new View.OnClickListener() {
	    public void onClick(View v) {
	    	AlertDialog.Builder msg_quit=new AlertDialog.Builder(Mobile.this);
	    	msg_quit.setMessage(R.string.ad_quit_msg).setCancelable(false).setPositiveButton(R.string.ad_yes_msg, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					System.exit(1);
				}
			});
	    	msg_quit.setNegativeButton(R.string.ad_no_msg, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			});
	    	
	    	AlertDialog alert=msg_quit.create();
	    	alert.show();
	    }
	  }; 
	  
	  private void showPasswordDialog()//Þifrenin Ekranda gösterilmesi. Dialog
	  {
		  final Dialog dialog = new Dialog(Mobile.this);
          dialog.setContentView(R.layout.getpassword);
          dialog.setCancelable(true);
          //there are a lot of settings, for dialog, check them all out!

          
          //set up text
          final TextView text = (TextView) dialog.findViewById(R.id.txtPassword);
          text.setText(uniqueCode);

          final TextView txtCountDown=(TextView)dialog.findViewById(R.id.txtCountDown);
          new CountDownTimer(60000, 1000) {
              public void onTick(long millisUntilFinished) {
                  txtCountDown.setText("00:" + millisUntilFinished / 1000);
              }

              public void onFinish() {
                  txtCountDown.setText(R.string.timeCount);
                  text.setText("");
              }
          }.start();
          
          //set up button
          Button button = new Button(Mobile.this);
          button.setOnClickListener(new OnClickListener() {
          @Override
              public void onClick(View v) {
                  dialog.cancel();
              }
          });
          
          text.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
          //now that the dialog is set up, it's time to show it    
          dialog.show();
	  }
	  
	  private static String getUniqeCode()//6 haneli tek kullanýmlýk þifrenin oluþmasý...
	  {
		  final int[] mArray=new int[32];
		  int index=0;
		  String unique="";
		  for (int i = 0; i < 32; i++) {
			mArray[index]=mRnd.nextInt(10);
			index++;
		  }
		  for (int i = 0; i < countOfunique; i++) {
			unique+=String.valueOf(mArray[mRnd.nextInt(32)]);
		  }
		  return unique;
	  }
	  
	  //webservise password1 gönderilecek, kullanýcý numarasý ve seçilen bankaya göre aranýp gereken yere eklenecek.
	  private void sendPW1andTime1(String pw1)
	  {		  
	      final String pNum_cryp=getCryptionWord(User.getNumber());
	      final String bank_cryp=getCryptionWord(User.getBank());
	      final String pw1_cryp=getCryptionWord(pw1);
	      
	      showCurrentLocation();
	      JSONObject json = new JSONObject();			
			try {
				json.put("Number", pNum_cryp);
				json.put("PW1", pw1_cryp);
				json.put("Bank", bank_cryp);
				json.put("LON", Longitude);
				json.put("LAT", Latitude);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			httpSend ftpServer=new httpSend();
			resultSend= ftpServer.SendData(json, "wsAddPW1_Time");			
	  }
	  
	  //Get cryption word for sended str
	  public static String getCryptionWord(String str)
	  {
		  String cryp_word="";
		  try {
				LocalEncrypter l=new LocalEncrypter(algo, bit);
				cryp_word=l.GetCrypted(str);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return cryp_word;   
	  }
	  
	  //progressBar kullanýmý
	  private class GetPassTask extends AsyncTask<String, Void, Void>{//thread ile þifre oluþturma ve bilgileri gönderme.

		ProgressDialog pd;
		protected void onPreExecute()
		{
			  pd=ProgressDialog.show(Mobile.this, getResources().getString(R.string.pd_getPass) , getResources().getString(R.string.pd_loading));
		}
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			uniqueCode=getUniqeCode();			
			sendPW1andTime1(uniqueCode);	
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			if(resultSend.equals("True"))
				showPasswordDialog();
			else
				ShowConnectionError();
			if(pd.isShowing())
				pd.dismiss();
			super.onPostExecute(result);
		}		
	  }	
	private void loadUserInfo()//Telefon numarasýnýn alýnmasý
	{
		TelephonyManager tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    String pNum=tm.getLine1Number();	 
	    
	    User.setNumber(pNum);	    
	    User.setBank(banks.getSelectedItem().toString());
	}
	  /*private String getTextFromStrings(String str)//Dil desteði için yapýldý...
	  {
		  String lang=Keypay_Odeme.getSys_Language();
		  int id=getResources().getIdentifier(str+lang, "string" , "com.odeme.keypay");			
		  return getResources().getString(id);
	  }*/
	private void setLocale(String str)//Dili deðiþtirme
	{
		Configuration c = new Configuration();
		if(str.equals("TR"))
	       c.locale = new Locale("tr","TR");
		else
			c.locale=new Locale("en","US");
	    getResources().updateConfiguration(c, null);
	}
	private void ShowConnectionError()//Ýnternet sorunu varsa ekranda gösterme
	  {
		  //dialog yapýp göster...	
		  final Dialog dialog = new Dialog(Mobile.this);
        dialog.setContentView(R.layout.result);
        dialog.setCancelable(true);
        
      //initially text_Result and imageview_result
        final TextView text = (TextView) dialog.findViewById(R.id.txtResult);
        final ImageView imgResult=(ImageView)dialog.findViewById(R.id.imgViewResult);
        
      	imgResult.setImageResource(R.drawable.cancel);   
      	if(resultSend.equals("Bank_False")||resultSend=="Bank_False")
      		 text.setText(R.string.dialog_bank_control);
      	else
      		text.setText(R.string.ad_network_msg);     
	    text.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
	    imgResult.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});	   
        dialog.show();
        
	  }

	private void loadElementofLayout()
	{
		banks=(Spinner) findViewById(R.id.spin_banks);
        banks.setOnItemSelectedListener(listener);
        
        Button btn_getPass=(Button) findViewById(R.id.btn_getPassword);
        Button btn_about=(Button) findViewById(R.id.btn_about);
        Button btn_quit=(Button) findViewById(R.id.btn_quit);
        
        btn_getPass.setOnClickListener(GetPassword);
        btn_about.setOnClickListener(About);
        btn_quit.setOnClickListener(Quit);
	}


	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		setContentView(R.layout.mobilemain);
		loadElementofLayout();
	}
	
	//Lat ve long deðiþkenlerine current location ýný atama
	protected void showCurrentLocation() {

	        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

	        if (location != null) {
	        	Latitude=Double.toString( location.getLatitude());
	            Longitude=Double.toString( location.getLongitude());
	            
	        }

	    }   
	 
	 //LocationListener Sýnýfý <LocationChanged> fonk.
	private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            Latitude=Double.toString( location.getLatitude());
            Longitude=Double.toString( location.getLongitude());
        }

        public void onStatusChanged(String s, int i, Bundle b) {
            
        }

        public void onProviderDisabled(String s) {
            
        }

        public void onProviderEnabled(String s) {
            
        }

    }
}



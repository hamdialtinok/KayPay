package com.odeme.keypay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Pos extends Activity{
	private EditText txtFee;
	private EditText txtUniqueCode;
	private EditText txtPassword2;
	private static String Result;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posmain);
        loadElemenofLayout();        
    }
    
    //Send butonu
    View.OnClickListener SendFromPos=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//Textview ler dolu mu boþmu
			if(txtFee.getText().toString().equals("")||txtUniqueCode.getText().toString().equals("")||txtPassword2.getText().toString().equals(""))
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_InfoControl), Toast.LENGTH_SHORT).show();
			else
				new SendDataTask().execute();//Thread. Bilgilerin gönderilmesi
		}
	};
	
	//Close butonu
	View.OnClickListener ClosePos=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			AlertDialog.Builder msg_quit=new AlertDialog.Builder(Pos.this);
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
	  
	//ProgressBar kullanýmý..
	  private class SendDataTask extends AsyncTask<String, Void, Void>{//Thread ile datalar gönderildi ve Sonuç dialoðu oluþturuldu

			ProgressDialog pd;
			protected void onPreExecute()
			{
				//getResources().getString(R.string.pd_sendPass) ....... strings.xml den veri çekmek için 
				  pd=ProgressDialog.show(Pos.this, getResources().getString(R.string.pd_sendPass), getResources().getString(R.string.pd_waiting));
			}
			@Override
			protected Void doInBackground(String... params) {
				// TODO Auto-generated method stub
				
				//sendPW1andPW2();
				SendTransaction();
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub		
				ShowTransactionControl();
				if(pd.isShowing())
					pd.dismiss();
				super.onPostExecute(result);
			}
	  }
	  
	  private void ShowTransactionControl()//Sonuç dialoðu oluþturma
	  {
		  //dialog yapýp göster...	
		  final Dialog dialog = new Dialog(Pos.this);
          dialog.setContentView(R.layout.result);
          dialog.setCancelable(true);
                   
        //initially text_Result and imageview_result
          final TextView text = (TextView) dialog.findViewById(R.id.txtResult);
          final ImageView imgResult=(ImageView)dialog.findViewById(R.id.imgViewResult);
          
          if(Result.equals("True")){
        	  imgResult.setImageResource(R.drawable.accept);
        	  text.setText(R.string.result_true);
          }
          else{
        	  imgResult.setImageResource(R.drawable.cancel);          
	          if(Result.equals("RM_False"))//Remnant Error
	        	  text.setText(R.string.result_rm);
	          else if(Result.equals("TM_False"))//Time error
	        	  text.setText(R.string.result_tm);
	          else if(Result.equals("PW_False"))//Password Error
	        	  text.setText(R.string.result_pw);
	          else
	        	  text.setText(R.string.result_null);
          }
                    
          	text.setOnClickListener(new OnClickListener() {
  			
	  			@Override
	  			public void onClick(View v) {
	  				// TODO Auto-generated method stub
	  				clearElementInfo();
	  				dialog.cancel();	  				
	  			}
	  		});
	  	    imgResult.setOnClickListener(new OnClickListener() {
	  			
	  			@Override
	  			public void onClick(View v) {
	  				// TODO Auto-generated method stub
	  				clearElementInfo();
	  				dialog.cancel();
	  			}
	  		});	   
          dialog.show();
          
	  }
	  private void SendTransaction()//HTTP RECEIVE
	  {
		  httpReceive http=new httpReceive();
		  String pw1=Mobile.getCryptionWord(txtUniqueCode.getText().toString());
		  String pw2=Mobile.getCryptionWord(txtPassword2.getText().toString());
		  String remnant=txtFee.getText().toString();
		  Result=http.GetList(pw1,pw2,remnant);
	  }

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		setContentView(R.layout.posmain);
		loadElemenofLayout();
	}
	private void loadElemenofLayout()
	{
		txtFee=(EditText)findViewById(R.id.txtFee);
        txtUniqueCode=(EditText)findViewById(R.id.txtUniqueCode);
        txtPassword2=(EditText)findViewById(R.id.txtPassword2);
        
        Button btnClosePos=(Button)findViewById(R.id.btnClosePos);
        Button btnSendFromPos=(Button)findViewById(R.id.btnSendfromPos);
        
        btnClosePos.setOnClickListener(ClosePos);
        btnSendFromPos.setOnClickListener(SendFromPos);        
	}
	private void clearElementInfo()
	{
		txtFee.setText("");
		txtUniqueCode.setText("");
		txtPassword2.setText("");
	}
}
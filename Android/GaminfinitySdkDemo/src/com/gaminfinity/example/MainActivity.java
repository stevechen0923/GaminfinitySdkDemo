package com.gaminfinity.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gaminfinity.*;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
public class MainActivity extends Activity implements ClientSDK.EventHandler{

	private static final String LOG_TAG = "MainActivity";
	private static final String GAMINFINITY_SERVER_URL = "http://219.87.94.74/sns/new_bind_uuid.php";
	private static final Boolean OPTION_HIDE_INSTANT_LOGIN_BUTTON_WHEN_FB_LOGIN = true;
	public ClientSDK clientSdk;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		clientSdk = new ClientSDK(this);
		
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
        updateView();  
	}

	@Override
	public void onGetAccountId(int result, String  accountId) {
		Log.d(LOG_TAG, "onGetAccountID(), result=" + result + ", AccountID=" + accountId);
		if( result == 1){
			Log.d(LOG_TAG, "取得帳號成功，帳號=" + accountId);
			showTextMessage("取得帳號成功，帳號=" + accountId);
		}else{
			Log.d(LOG_TAG, "取得帳號失敗，錯誤代碼=" + result);
			showTextMessage("取得帳號失敗，錯誤代碼=" + result);
		}
	}

	private void showTextMessage(String msg){
		TextView tv = (TextView)findViewById(R.id.textViewMessage);
		tv.setText(msg);
	}
	
	public void onInstantLoginButtonClick(View v){
		clientSdk.getAccountId(this, null, GAMINFINITY_SERVER_URL);
	}
	 
	public void onFacebookLoginButtonClick(View v){
		Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
	}	
	
	public void onFacebookLogoutButtonClick(View v) {
		 Session session = Session.getActiveSession();
	     if (!session.isClosed()) {
	    	 session.closeAndClearTokenInformation();
	     }
	 }
	
	private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        	if (session.isOpened()) {
        		clientSdk.getAccountId(MainActivity.this, session.getAccessToken(), GAMINFINITY_SERVER_URL);
        	 }
        	updateView();
        }
    }
	
	@Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
    
    private void updateView() {
        Session session = Session.getActiveSession();
        Button btnInstantLogin = (Button) findViewById(R.id.instantLoginButton);
        Button btnFbLogin = (Button) findViewById(R.id.fbLoginButton);
        Button btnFbLogout = (Button) findViewById(R.id.fbLogoutButton);
        if (session.isOpened()) {    
        	if(OPTION_HIDE_INSTANT_LOGIN_BUTTON_WHEN_FB_LOGIN)
        		btnInstantLogin.setVisibility(View.INVISIBLE);
        	btnFbLogin.setVisibility(View.INVISIBLE);
        	btnFbLogout.setVisibility(View.VISIBLE);
        }
        else{
        	if(OPTION_HIDE_INSTANT_LOGIN_BUTTON_WHEN_FB_LOGIN)
        		btnInstantLogin.setVisibility(View.VISIBLE);
        	btnFbLogin.setVisibility(View.VISIBLE);
        	btnFbLogout.setVisibility(View.INVISIBLE);
        	showTextMessage("");
        }
    }
    
    private void debugLog(String msg){
		Log.v(LOG_TAG, msg);
	}
	
	private void showToast(String msg){
		Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
}

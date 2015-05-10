package com.adenda.plugin.urbanairship;

import com.urbanairship.push.PushMessage;
import com.urbanairship.widget.LandingPageWebView;
import com.urbanairship.widget.UAWebViewClient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import sdk.adenda.lockscreen.fragments.AdendaFragmentInterface;
import sdk.adenda.widget.DateTimeFragment;

public class UrbanAirshipLockscreenFragment extends Fragment implements AdendaFragmentInterface 
{
	protected static final String NOTIFICATION_URL = "notification_url";
	protected static final String UA_PUSH_MESSAGE = "ua_push_message";
	private static final int DEFAULT_DATE_TIME_TXT_COLOR = 0xFF000000;
	private static final int DEFAULT_BACKGROUND_COLOR = 0XFFFFFFFF;
	private static final String ADENDA_DATETIME_COLOR_PARAM = "adenda_datetime_color";
	private static final String ADENDA_BKGRD_COLOR_PARAM = "adenda_background_color";
	private static final String ADENDA_ACTION_URI = "adenda_action_uri";
	
	private LandingPageWebView mWebView;
	private String mNotificationUrl;
	private int mDateTimeColor;
	private int mBackgroundColor;
	private String mActionUri;
	private PushMessage mMessage;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Bundle args = getArguments();
		if (args != null)
		{
			mMessage = (PushMessage)args.getParcelable(UA_PUSH_MESSAGE);
			mNotificationUrl = args.getString(NOTIFICATION_URL);
			Long txtColor = getDateTimeColor(mMessage);
			mDateTimeColor =  txtColor != null ? (int)txtColor.longValue() : DEFAULT_DATE_TIME_TXT_COLOR;
			Long bkgrndColor = getBackgroundColor(mMessage);
			mBackgroundColor = bkgrndColor != null ? (int)bkgrndColor.longValue() : DEFAULT_BACKGROUND_COLOR;
			mActionUri = getActionUri(mMessage);
		}
	}
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Load layout
		View view = inflater.inflate(R.layout.landing_page, container, false);
		final ProgressBar progressBar = (ProgressBar) view.findViewById(android.R.id.progress);
		mWebView = (LandingPageWebView) view.findViewById(android.R.id.primary);

		// Set background color
		View dateTimeContainer = view.findViewById(R.id.date_time_container);
		if(dateTimeContainer != null)
			dateTimeContainer.setBackgroundColor(mBackgroundColor);
		
		// Add date/time fragment!
		DateTimeFragment dateTimeFragment = DateTimeFragment.newInstance(DateTimeFragment.TXT_CENTER_JUSTIFY, mDateTimeColor, false);
		getChildFragmentManager().beginTransaction().replace(R.id.date_time_container, dateTimeFragment).commit();
		
		// Load actual notification!
		if (mNotificationUrl != null && mWebView != null && progressBar != null)
		{
			 mWebView.setWebViewClient(new UAWebViewClient() {
		            @Override
		            public void onPageFinished(WebView view, String url) {
		                // Make sure to call through to the super's implementation
		                // or the javascript native bridge will not be fully loaded
		                super.onPageFinished(view, url);
		                // Hide the progress bar when the page is loaded
		                progressBar.setVisibility(View.GONE);
		            }
		        });
			 
			 mWebView.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					UrbanAirshipLockscreenFragment.this.getActivity().onTouchEvent(event);
					return false;
				}});
			 
			 // Load URL
			 mWebView.loadUrl(mNotificationUrl);
		}
		
		return view;
	}
	
	@Override
	public boolean expandOnRotation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Intent getActionIntent() 
	{
		// Record Urban Airship event
		AdendaLockscreenAction.requestRun(mMessage, mActionUri);
		
		if ( mActionUri != null && !mActionUri.isEmpty())
		{
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(mActionUri));
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			return i;
		}
		return null;
	}
	
	private Long getBackgroundColor (PushMessage message)
    {
    	return getHexParam(message, ADENDA_BKGRD_COLOR_PARAM);
    }
    
    private Long getDateTimeColor( PushMessage message)
    {
    	return getHexParam(message, ADENDA_DATETIME_COLOR_PARAM);
    }
    
    private Long getHexParam( PushMessage message, String sParamName)
    {
    	if (message == null || message.getPushBundle() == null)
    		return null;
    	
    	String sDateTimeColor = message.getPushBundle().getString( sParamName);
    	if ( sDateTimeColor == null)
    		return null;
    	
    	return Long.parseLong(sDateTimeColor, 16);
    }
    
    private String getActionUri(PushMessage message)
    {
    	if (message == null || message.getPushBundle() == null)
    		return null;
    	
    	return message.getPushBundle().getString(ADENDA_ACTION_URI);
    }

	@Override
	public boolean coverEntireScreen() {
		return true;
	}

	@Override
	public Pair<Integer, Integer> getGlowpadResources() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getStartHelperForResult() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onActionFollowedAndLockScreenDismissed() {
		// TODO Auto-generated method stub
	}
}

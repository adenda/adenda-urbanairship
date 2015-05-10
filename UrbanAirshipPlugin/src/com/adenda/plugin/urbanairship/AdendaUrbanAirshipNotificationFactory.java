package com.adenda.plugin.urbanairship;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.urbanairship.actions.LandingPageAction;
import com.urbanairship.push.PushMessage;
import com.urbanairship.push.notifications.DefaultNotificationFactory;

import org.json.JSONException;
import org.json.JSONObject;

import sdk.adenda.lockscreen.AdendaAgent;

/**
 * A custom push notification builder to create inbox style notifications
 * for rich push messages. In the case of standard push notifications, it will
 * fall back to the default behavior.
 */
public class AdendaUrbanAirshipNotificationFactory extends DefaultNotificationFactory 
{
	private static final String ADENDA_LOCKSCREEN_PARAM = "adenda_lockscreen";

    public AdendaUrbanAirshipNotificationFactory(Context context) {
        super(context);
    }

    @Override
    public Notification createNotification(PushMessage message, int notificationId) {
    	
    	if (message == null)
    		return null;
    	
    	String sLockScreenParam = null;
    	if (message.getPushBundle() != null)
    		sLockScreenParam = message.getPushBundle().getString(ADENDA_LOCKSCREEN_PARAM);
    	
    	if ( sLockScreenParam.contentEquals("true"))
    	{
    		String sUrl = null;
    		// Get Payload
    		String sActionsPayload = message.getActionsPayload();
   
    		try {
    			// Get actual url
    			if (sActionsPayload != null && !sActionsPayload.isEmpty())
    			{	
    				JSONObject payloadJson = new JSONObject(sActionsPayload);
					sUrl = (String) payloadJson.get(LandingPageAction.DEFAULT_REGISTRY_SHORT_NAME);
    			}
			} catch (JSONException e) {
				Log.e(getClass().getSimpleName(), e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "Could not get Payload!");
				e.printStackTrace();
			}

			if (sUrl != null)
			{
				// We found a url!
				Bundle args = new Bundle();
				args.putString(UrbanAirshipLockscreenFragment.NOTIFICATION_URL, sUrl);		
				// Add message as parameter so that we can extract other info such as text and background color
				args.putParcelable(UrbanAirshipLockscreenFragment.UA_PUSH_MESSAGE, message);
				// Notify Adenda to display this next
				AdendaAgent.addCustomFragmentContent(getContext(), null, UrbanAirshipLockscreenFragment.class.getName(), args, message.getTitle(), false, true);
				// Flush Content so that the Urban Airship notification screen appears right away
            	AdendaAgent.flushContentCache(getContext().getApplicationContext());
			}
			
    		// Don't display a notification
        	return null;
    	}
        
        return super.createNotification(message, notificationId);
    }
}

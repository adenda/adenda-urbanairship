package com.adenda.plugin.urbanairship;

import java.util.Map;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.urbanairship.actions.ActionValue;
import com.urbanairship.actions.LandingPageAction;
import com.urbanairship.push.PushMessage;
import com.urbanairship.push.notifications.DefaultNotificationFactory;

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
    	
    	// If it's an Adenda message but the user is not opted in
    	if ( sLockScreenParam != null && sLockScreenParam.contentEquals("true") && !AdendaAgent.isOptedIn(getContext()))
    		// Then just ignore it, but mark it as processed
    		return null;
    	
    	// If it's an Adenda message and the use is opted in
    	if ( sLockScreenParam != null && sLockScreenParam.contentEquals("true"))
    	{
    		// Start processing it
    		String sUrl = null;
    		// Get Payload
    		Map<String, ActionValue> actions = message.getActions();
    		ActionValue urlAction = actions.get(LandingPageAction.DEFAULT_REGISTRY_SHORT_NAME);
    		if (urlAction != null)
    			sUrl = urlAction.getString();
    		else
    			Log.e(getClass().getSimpleName(), "Could not get UA Payload!");
    			
			if (sUrl != null)
			{
				String sIdentifier = "UAID:" + message.getCanonicalPushId();
				// We found a url!
				Bundle args = new Bundle();
				args.putString(UrbanAirshipLockScreenFragment.NOTIFICATION_URL, sUrl);
				// Add message as parameter so that we can extract other info such as text and background color
				args.putParcelable(UrbanAirshipLockScreenFragment.UA_PUSH_MESSAGE, message);
				// Notify Adenda to display this next
				AdendaAgent.addCustomFragmentContent(getContext(), null, UrbanAirshipLockScreenFragment.class.getName(), args, sIdentifier, false, true);
				// Flush Content so that the Urban Airship notification screen appears right away
            	AdendaAgent.flushContentCache(getContext().getApplicationContext());
			}
			
    		// Don't display a notification
        	return null;
    	}
        
        return super.createNotification(message, notificationId);
    }
}

package com.adenda.plugin.urbanairship;

import android.app.Application;

import com.urbanairship.UAirship;

public class AdendaUrbanAirshipApplication extends Application 
{
    @Override
    public void onCreate() {

    	// Urban Airship Support
		final AdendaUrbanAirshipNotificationFactory factory = new AdendaUrbanAirshipNotificationFactory(AdendaUrbanAirshipApplication.this);
		 // Set the accent color
        factory.setColor(getResources().getColor(R.color.color_primary));

        // Set the notification icon
        factory.setSmallIconId(R.drawable.ic_notification);
        
		UAirship.takeOff(this, new UAirship.OnReadyCallback(){

			@Override
			public void onAirshipReady(UAirship airship) {
				// Set the factory
                airship.getPushManager().setNotificationFactory(factory);
                // Setup custom lockscreen action
                AdendaLockscreenAction.register();
			}
		});
		UAirship.shared().getPushManager().setUserNotificationsEnabled(true);	
    }
}

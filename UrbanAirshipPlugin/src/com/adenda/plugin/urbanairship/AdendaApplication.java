package com.adenda.plugin.urbanairship;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.CallSuper;

import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;

public class AdendaApplication extends Application {

    private static final String FIRST_RUN_KEY = "first_run";

    @Override
    public void onCreate() {
        super.onCreate();

        AirshipConfigOptions configOptions = createAirshipConfig();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final boolean isFirstRun = preferences.getBoolean(FIRST_RUN_KEY, true);
        if (isFirstRun) {
            preferences.edit().putBoolean(FIRST_RUN_KEY, false).apply();
        }

        UAirship.takeOff(this, configOptions, new UAirship.OnReadyCallback() {
            @Override
            public void onAirshipReady(UAirship airship) {
                AdendaApplication.this.onAirshipReady(airship, isFirstRun);
            }
        });
    }

    /**
     * Called when UAirship is ready. Perform any airship customizations. The default
     * implementation sets the {@link AdendaNotificationFactory}.
     *
     * @param airship The Urban Airship instance.
     * @param isFirstRun {@code true} if its the first run, otherwise {@code false}.
     */
    @CallSuper
    protected void onAirshipReady(UAirship airship, boolean isFirstRun) {
        AdendaNotificationFactory factory = new AdendaNotificationFactory(this);

        factory.setColor(airship.getAirshipConfigOptions().notificationAccentColor);
        if (airship.getAirshipConfigOptions().notificationIcon != 0) {
            factory.setSmallIconId(airship.getAirshipConfigOptions().notificationIcon);
        }

        airship.getPushManager().setNotificationFactory(factory);

        if (isFirstRun) {
            airship.getPushManager().setUserNotificationsEnabled(true);
        }
    }

    /**
     * Called to create AirshipConfigOptions. If null, airship config options
     * will be loaded from `airshipconfig.properties` file.
     *
     * @return AirshipConfigOptions.
     */
    protected AirshipConfigOptions createAirshipConfig() {
        return null;
    }
}

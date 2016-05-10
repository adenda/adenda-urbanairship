package com.adenda.plugin.urbanairship;

import com.urbanairship.UAirship;

import sdk.adenda.widget.AdendaButtonCallback;

public class UrbanAirshipAdendaCallback implements AdendaButtonCallback {

    @Override
    public String getUserId() {
        return UAirship.shared().getPushManager().getChannelId();
    }

    @Override
    public String getUserGender() {
        return null;
    }

    @Override
    public String getUserDob() {
        return null;
    }

    @Override
    public float getUserLatitude() {
        return 0;
    }

    @Override
    public float getUserLongitude() {
        return 0;
    }

    @Override
    public void onPreOptIn() {
    }

    @Override
    public void onPreOptOut() {
    }

    @Override
    public void onPostOptIn() {
    }

    @Override
    public void onPostOptOut() {
    }
}

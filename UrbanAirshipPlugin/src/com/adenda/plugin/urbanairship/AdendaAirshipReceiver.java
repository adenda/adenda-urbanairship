package com.adenda.plugin.urbanairship;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.urbanairship.AirshipReceiver;
import com.urbanairship.actions.ActionValue;
import com.urbanairship.actions.LandingPageAction;
import com.urbanairship.push.PushMessage;

import sdk.adenda.lockscreen.AdendaAgent;

public class AdendaAirshipReceiver extends AirshipReceiver {

    private static final String ADENDA_LOCKSCREEN_PARAM = "adenda_lockscreen";

    @Override
    protected void onPushReceived(@NonNull Context context, @NonNull PushMessage message, boolean notificationPosted) {
        ActionValue landingPageActionValue = message.getActions().get(LandingPageAction.DEFAULT_REGISTRY_SHORT_NAME);

        if (landingPageActionValue == null || !PushUtils.parseBooleanExtra(message, ADENDA_LOCKSCREEN_PARAM, false)) {
            return;
        }

        String identifier = "UAID: " + message.getCanonicalPushId();

        Bundle args = new Bundle();
        args.putString(UrbanAirshipLockScreenFragment.NOTIFICATION_URL, landingPageActionValue.getString());
        args.putParcelable(UrbanAirshipLockScreenFragment.UA_PUSH_MESSAGE, message);

        // Notify Adenda to display this next
        AdendaAgent.addCustomFragmentContent(context, null, UrbanAirshipLockScreenFragment.class.getName(), args, identifier, false, true);

        // Flush Content so that the Urban Airship notification screen appears right away
        AdendaAgent.flushContentCache(context.getApplicationContext());
    }
}

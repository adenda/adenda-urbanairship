package com.adenda.plugin.urbanairship;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.urbanairship.AirshipReceiver;
import com.urbanairship.actions.ActionValue;
import com.urbanairship.actions.LandingPageAction;
import com.urbanairship.push.PushMessage;

import sdk.adenda.lockscreen.AdendaAgent;

public class AdendaAirshipReceiver extends AirshipReceiver {

    private static final String ADENDA_LOCKSCREEN_PARAM = "adenda_lockscreen";
    private static final String ADENDA_FLUSH_CONTENT = "adenda_flush_content";
    private static final String ADENDA_UA_NOTIF_PREFS = "adenda_ua_notif_prefs";
    private static final String ADENDA_UA_NOTIF_PREFIX = "adenda_ua_notif-";
    private static final String ADENDA_UA_FIRST_RUN = "adenda_ua_first_run";

    @Override
    protected void onChannelCreated(@NonNull Context context, @NonNull String channelId) {
        SharedPreferences preferences = context.getSharedPreferences(ADENDA_UA_NOTIF_PREFS, Context.MODE_PRIVATE);
        final boolean isFirstRun = preferences.getBoolean(ADENDA_UA_FIRST_RUN, true);

        if (isFirstRun) {
            preferences.edit().putBoolean(ADENDA_UA_FIRST_RUN, false).apply();
            new AdendaAgent.LockScreenHelper(context, new UrbanAirshipAdendaCallback()).startLockscreen();
        }
    }

    @Override
    protected boolean onNotificationOpened (@NonNull Context context, @NonNull AirshipReceiver.NotificationInfo notificationInfo) {
        // Cancel the lock screen notification since the landing page will be viewed inside the app
        long lockScreenNotifId = context.getSharedPreferences(ADENDA_UA_NOTIF_PREFS, Context.MODE_PRIVATE)
                .getLong(ADENDA_UA_NOTIF_PREFIX + notificationInfo.getNotificationId(), -1);

        if (lockScreenNotifId > 0) {
            AdendaAgent.removeCustomContent(context.getApplicationContext(), lockScreenNotifId);
            AdendaAgent.flushContentCache(context.getApplicationContext());
        }

        return super.onNotificationOpened(context, notificationInfo);
    }
    @Override
    protected void onNotificationPosted(@NonNull Context context, @NonNull AirshipReceiver.NotificationInfo notificationInfo) {
        // Post Lock Screen Notification
        Long lockScreenNotifId = postLockScreenNotification(context, notificationInfo.getMessage(), notificationInfo.getNotificationId());

        if (lockScreenNotifId != null) {
            // Save Lock Screen Notification ID in case we need to dismiss it later
            context.getSharedPreferences(ADENDA_UA_NOTIF_PREFS, Context.MODE_PRIVATE).edit().putLong(ADENDA_UA_NOTIF_PREFIX + notificationInfo.getNotificationId(), lockScreenNotifId).commit();
        }
    }

    @Override
    protected void onPushReceived(@NonNull Context context, @NonNull PushMessage message, boolean notificationPosted) {
        if (!notificationPosted) {
            postLockScreenNotification(context, message, null);
        }
    }

    private static Long postLockScreenNotification(@NonNull Context context, @NonNull PushMessage message, Integer notificationId) {
        ActionValue landingPageActionValue = message.getActions().get(LandingPageAction.DEFAULT_REGISTRY_SHORT_NAME);

        if (landingPageActionValue == null || !PushUtils.parseBooleanExtra(message, ADENDA_LOCKSCREEN_PARAM, false)) {
            return null;
        }

        String identifier = "UAID: " + message.getCanonicalPushId();

        Bundle args = new Bundle();
        args.putString(UrbanAirshipLockScreenFragment.NOTIFICATION_URL, landingPageActionValue.getString());
        args.putParcelable(UrbanAirshipLockScreenFragment.UA_PUSH_MESSAGE, message);
        if (notificationId != null)
            args.putInt(UrbanAirshipLockScreenFragment.NOTIFICATION_ID, notificationId);

        // Notify Adenda to display this next
        long lockScreenNotifId = AdendaAgent.addCustomFragmentContent(context.getApplicationContext(), null, UrbanAirshipLockScreenFragment.class.getName(), args, identifier, false, true);

        if (PushUtils.parseBooleanExtra(message, ADENDA_FLUSH_CONTENT, true)) {
            // Flush Content so that the Urban Airship notification screen appears right away
            AdendaAgent.flushContentCache(context.getApplicationContext());
        }

        return lockScreenNotifId;
    }
}

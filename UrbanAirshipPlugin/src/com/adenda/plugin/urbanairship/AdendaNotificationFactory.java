package com.adenda.plugin.urbanairship;

import android.app.Notification;
import android.content.Context;
import android.support.annotation.NonNull;

import com.urbanairship.push.PushMessage;
import com.urbanairship.push.notifications.DefaultNotificationFactory;


public class AdendaNotificationFactory extends DefaultNotificationFactory {

    public AdendaNotificationFactory(Context context) {
        super(context);
    }

    @Override
    public Notification createNotification(@NonNull PushMessage message, int notificationId) {
        if (PushUtils.parseBooleanExtra(message, AdendaAirshipReceiver.ADENDA_LOCKSCREEN_PARAM, false)) {
            return null;
        }

        return super.createNotification(message, notificationId);
    }
}

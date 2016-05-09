package com.adenda.plugin.urbanairship;

import android.app.Notification;
import android.content.Context;
import android.support.annotation.NonNull;

import com.urbanairship.push.PushMessage;
import com.urbanairship.push.notifications.DefaultNotificationFactory;


public class AdendaNotificationFactory extends DefaultNotificationFactory {

    private static final String ADENDA_DISPLAY_NOTIFICATION = "adenda_display_notification";

    public AdendaNotificationFactory(Context context) {
        super(context);
    }

    @Override
    public Notification createNotification(@NonNull PushMessage message, int notificationId) {
        if (!PushUtils.parseBooleanExtra(message, ADENDA_DISPLAY_NOTIFICATION, true)) {
            return null;
        }

        return super.createNotification(message, notificationId);
    }
}

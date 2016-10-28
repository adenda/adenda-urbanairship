package com.adenda.plugin.urbanairship;

import android.app.Notification;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.urbanairship.push.PushMessage;
import com.urbanairship.push.notifications.NotificationFactory;
import com.urbanairship.util.UAStringUtil;


public class AdendaNotificationFactory extends NotificationFactory {

    private static final String ADENDA_DISPLAY_NOTIFICATION = "adenda_display_notification";

    AdendaNotificationFactory(@NonNull Context context) {
        super(context);
    }

    @Override
    public Notification createNotification(@NonNull PushMessage message, int notificationId) {
        if (!PushUtils.parseBooleanExtra(message, ADENDA_DISPLAY_NOTIFICATION, true)) {
            return null;
        }

        if(UAStringUtil.isEmpty(message.getAlert())) {
            return null;
        } else {
            NotificationCompat.Builder builder = this.createNotificationBuilder(message, notificationId, (new NotificationCompat.BigTextStyle()).bigText(message.getAlert()));
            return this.extendBuilder(builder, message, notificationId).build();
        }
    }

    public NotificationCompat.Builder extendBuilder(@NonNull NotificationCompat.Builder builder, @NonNull PushMessage message, int notificationId) {
        return builder;
    }
}

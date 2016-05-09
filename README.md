# Adenda Urban Airship Plugin

Adenda integration for Urban Airship.

## Quick Setup

1) Add the plugin to your project.

2) Set the application's name as `AdendaApplication` in AndroidManifest.xml:

   ```
    <application android:name=".AdendaApplication">
   ```
3) Add `airshipconfig.properties` file to your applications assets directory with your Urban Airship config:

    ```
    developmentAppKey = Your Development App Key
    developmentAppSecret = Your Development App Secret
    
    productionAppKey = Your Production App Key
    productionAppSecret = Your Production Secret
    
    # Toggles between the development and production app credentials
    # Before submitting your application to an app store set to true
    inProduction = false
    
    # LogLevel is "VERBOSE", "DEBUG", "INFO", "WARN", "ERROR" or "ASSERT"
    developmentLogLevel = DEBUG
    productionLogLevel = ERROR
    
    # GCM Sender ID
    gcmSender = Your Google API Project Number
    
    # Notification customization
    notificationIcon = ic_notification
    notificationAccentColor = #ff0000
    ```

## Sending Full-Page Notifications

To get started with full-page notifications, log into your Urban Airship dashboard and do the following:

1) Create a new message.
3) Add a landing page action. This will be used as the full-page lock screen notification.
4) Add the Android extra `adenda_lockscreen` with the value `true`.
5) Add the Android extra `adenda_display_notification` with the value `true` to display the standard notification, or `false` to ignore it and only show the full-page notifications. Defaults to true.
5) Set audience and delivery.
6) Send the push notification.

The following extras can be used to customize the lock screen notification:

- `adenda_action_uri`: Defines a URL or deep-link to follow when a user unlocks his device to activate the landing page action
- `adenda_background_color`: Defines the color of the lock screen background. Default is white. Format must be 8 hexadecimal characters: the first two determine the color’s alpha, while the last 6 represent the color’s hex code. e.g.: FFFFFFFF for white.
- `adenda_datetime_color`: Defines the color of the date and time text. Default is black. Format must be 8 hexadecimal characters: the first two determine the color’s alpha, while the last 6 represent the color’s hex code. e.g.: FF000000 for black.
- `adenda_expand_content`: Specifies whether the notification content should expand under the time and date. e.g: true

## Customizing Take Off

If you need to customize Urban Airship or provide custom config options, applications should
extend  ``AdendaApplication`` and override either ``onAirshipReady`` or ``createAirshipConfig``.

1) Create a custom `Application` that extends `AdendaApplication`:
   
   ```
   public class CustomApplication extends AdendaApplication {
        
        /**
         * Called when UAirship is ready. Perform any airship customizations.
         * The default implementation sets the {@link AdendaNotificationFactory}.
         *
         * @param airship The Urban Airship instance.
         * @param isFirstRun {@code true} if its the first run, otherwise {@code false}.
         */
        protected void onAirshipReady(UAirship airship, boolean isFirstRun) {
            super.onAirshipReady(airship, isFirstRun);
            
            // Do any customization
        }
    
        /**
         * Called to create AirshipConfigOptions. If null, airship config options
         * will be loaded from `airshipconfig.properties` file.
         *
         * @return AirshipConfigOptions.
         */
        protected AirshipConfigOptions createAirshipConfig() {
            // return null or a custom config
            return null;
        }
    }
    ```
    
2) Then set the extended `AdendaApplication` as the application's name in AndroidManifest.xml:

    ```
    <application android:name=".CustomApplication">
    ```

If you are unable to extend or use the `AdendaApplication` you can instead just set the `AdendaNotificationFactory`
directly after takeOff:

    AdendaNotificationFactory factory = new AdendaNotificationFactory(this);

    factory.setColor(airship.getAirshipConfigOptions().notificationAccentColor);
    if (airship.getAirshipConfigOptions().notificationIcon != 0) {
        factory.setSmallIconId(airship.getAirshipConfigOptions().notificationIcon);
    }

    airship.getPushManager().setNotificationFactory(factory);


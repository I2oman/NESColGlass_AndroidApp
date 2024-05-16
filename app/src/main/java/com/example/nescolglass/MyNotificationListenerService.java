package com.example.nescolglass;

import static com.example.nescolglass.Globals.*;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.HashMap;
import java.util.Objects;

public class MyNotificationListenerService extends NotificationListenerService {
    // HashMap to map package names to app IDs
    static HashMap<String, String> apps = new HashMap<String, String>();
    // Array of localStorage keys corresponding to app preferences
    static String localStorageKeys[] = new String[]{
            SHTELEGRAM,
            SHWHATSAPP,
            SHTEAMS,
            SHGMAIL,
            SHOUTLOOK,
            SHINSTAGRAM,
            SHMESSENGER,
            SHDISCORD,
            SHVIBER,
            SHMESSAGES,
            SHPHONE
    };

    static {
        // Initialize package names and app IDs
        apps.put("org.telegram.messenger", "0");
        apps.put("com.whatsapp", "1");
        apps.put("com.microsoft.teams", "2");
        apps.put("com.google.android.gm", "3");
        apps.put("com.microsoft.office.outlook", "4");
        apps.put("com.instagram.android", "5");
        apps.put("com.facebook.orca", "6");
        apps.put("com.discord", "7");
        apps.put("com.viber.voip", "8");
        apps.put("com.google.android.apps.messaging", "9");
        apps.put("com.google.android.dialer", "10");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // Method called when a notification is posted
        sentUsefulMsg(sbn); // Send useful message if conditions are met
    }

    private void sentUsefulMsg(StatusBarNotification sbn) {
        // Check if the notification is of interest (Messaging, BigText, or CallStyle)
        if ("android.app.Notification$MessagingStyle".equals(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEMPLATE)) ||
                "android.app.Notification$BigTextStyle".equals(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEMPLATE)) ||
                "android.app.Notification$CallStyle".equals(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEMPLATE))) {
            if (apps.get(sbn.getPackageName()) != null) {
                // Check if app alert is enabled in preferences
                if (MainActivity.localStorage.getPrefs(localStorageKeys[Integer.parseInt(apps.get(sbn.getPackageName()))], Boolean.class)) {
                    return; // Exit if app alert is enabled
                }
                // Extract title and content from the notification
                CharSequence title = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE);
                StringBuilder contentBuilder = new StringBuilder(Objects.requireNonNull(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT)));

                // Check and format title and content for overlap
                String overlap = "4=";
                if (title.length() > 10) {
                    overlap += "1;"; // Indicate title overlap
                    title = title.subSequence(0, 10); // Truncate title
                } else {
                    overlap += "0;"; // No title overlap
                }

                // Insert newlines for content if it exceeds a certain length
                if (contentBuilder.length() > 15) {
                    for (int i = 15; i < contentBuilder.length(); i += 22) {
                        contentBuilder.insert(i, "\n      ");
                    }
                }

                // Format the message for transmission
                String formattedText = "3=" + apps.get(sbn.getPackageName()) + ";" + overlap + "5=" + title + ";6=" + contentBuilder + ";";
                Log.d("System.out.println()", formattedText);
                // Send the formatted message if connected
                if (MainActivity.sendReceive != null) {
                    if (MainActivity.sendReceive.isConnected()) {
                        MainActivity.sendReceive.write(formattedText.getBytes());
                    }
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Handle removed notifications here if needed
    }
}

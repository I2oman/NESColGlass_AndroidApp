package com.example.nescolglass;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.HashMap;

public class MyNotificationListenerService extends NotificationListenerService {
    static HashMap<String, String> apps = new HashMap<String, String>();

    static {
        apps.put("org.telegram.messenger", "Telegram");
        apps.put("com.whatsapp", "WhatsApp");
        apps.put("com.viber.voip", "Viber");
        apps.put("com.microsoft.teams", "Teams");
        apps.put("com.google.android.gm", "Gmail");
        apps.put("com.microsoft.office.outlook", "Outlook");
        apps.put("com.instagram.android", "Instagram");
        apps.put("com.facebook.orca", "Messenger");
        apps.put("??", "Facebook");
        apps.put("com.discord", "Discord");
    }

    @Override
    public void onListenerConnected() {
        StatusBarNotification[] activeNotifications = getActiveNotifications();
        if (activeNotifications != null) {
            for (StatusBarNotification sbn : activeNotifications) {
                if ("android.app.Notification$MessagingStyle".equals(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEMPLATE)) ||
                        "android.app.Notification$BigTextStyle".equals(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEMPLATE))) {
                    soutFullMsg(sbn);
                }
            }
        }
    }

    private void soutFullMsg(StatusBarNotification sbn) {
        Log.i("System.out.println()", "MSG!!!------------");
        if (apps.get(sbn.getPackageName()) != null) {
            Log.d("System.out.println()", "Package Name: " + apps.get(sbn.getPackageName()));
            if (apps.get(sbn.getPackageName()).equals("Gmail")) {
                Log.d("System.out.println()", sbn.getNotification().toString());
            }
        } else {
            Log.d("System.out.println()", "Package Name: " + sbn.getPackageName());
        }
        soutMsg("EXTRA_TITLE", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE));
        soutMsg("EXTRA_TEXT", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT));
        soutMsg("EXTRA_BIG_TEXT", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_BIG_TEXT));
        soutMsg("EXTRA_INFO_TEXT", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_INFO_TEXT));
        soutMsg("EXTRA_SUB_TEXT", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_SUB_TEXT));
        soutMsg("EXTRA_SUMMARY_TEXT", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT));
        soutMsg("EXTRA_TEMPLATE", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEMPLATE));
        soutMsg("EXTRA_PEOPLE", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_PEOPLE));
        soutMsg("EXTRA_REMOTE_INPUT_HISTORY", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_REMOTE_INPUT_HISTORY));
        soutMsg("EXTRA_MEDIA_SESSION", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_MEDIA_SESSION));
        soutMsg("EXTRA_LARGE_ICON", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_LARGE_ICON));
        soutMsg("EXTRA_SMALL_ICON", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_SMALL_ICON));
        soutMsg("EXTRA_CHANNEL_ID", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_CHANNEL_ID));
        soutMsg("EXTRA_NOTIFICATION_ID", sbn.getNotification().extras.getCharSequence(Notification.EXTRA_NOTIFICATION_ID));
        Log.d("System.out.println()", "");
    }

    private void soutMsg(String name, CharSequence value) {
        if (value != null) {
            Log.d("System.out.println()", name + ": " + value);
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // Handle incoming notifications here
        Log.i("System.out.println()", "Message handler");

        if (apps.get(sbn.getPackageName()) != null) {
            Log.d("System.out.println()", "Package Name: " + apps.get(sbn.getPackageName()));
        } else {
            Log.d("System.out.println()", "Package Name: " + sbn.getPackageName());
        }
        Log.d("System.out.println()", "Title: " + sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE));
        Log.d("System.out.println()", "Text: " + sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT));
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Handle removed notifications here
    }
}

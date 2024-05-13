package com.example.nescolglass;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.HashMap;
import java.util.Objects;

public class MyNotificationListenerService extends NotificationListenerService {
    static HashMap<String, String> apps = new HashMap<String, String>();

    static {
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

//    @Override
//    public void onListenerConnected() {
//        StatusBarNotification[] activeNotifications = getActiveNotifications();
//        if (activeNotifications != null) {
//            for (StatusBarNotification sbn : activeNotifications) {
//                if ("android.app.Notification$MessagingStyle".equals(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEMPLATE)) ||
//                        "android.app.Notification$BigTextStyle".equals(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEMPLATE))) {
//                    soutFullMsg(sbn);
//                }
//            }
//        }
//    }

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
//        Log.i("System.out.println()", "Message handler");
//
//        if (apps.get(sbn.getPackageName()) != null) {
//            Log.d("System.out.println()", "Package Name: " + apps.get(sbn.getPackageName()));
//        } else {
//            Log.d("System.out.println()", "Package Name: " + sbn.getPackageName());
//        }
//        Log.d("System.out.println()", "Title: " + sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE));
//        Log.d("System.out.println()", "Text: " + sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT));
        sentUsefulMsg(sbn);
    }

    private void sentUsefulMsg(StatusBarNotification sbn) {
        if ("android.app.Notification$MessagingStyle".equals(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEMPLATE)) ||
                "android.app.Notification$BigTextStyle".equals(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEMPLATE)) ||
                "android.app.Notification$CallStyle".equals(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEMPLATE))) {
            if (apps.get(sbn.getPackageName()) != null) {
                CharSequence title = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE);
                StringBuilder contentBuilder = new StringBuilder(Objects.requireNonNull(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT)));

                String overlap = "4=";

                if (title.length() > 10) {
                    overlap += "1;";
                    title = title.subSequence(0, 10);
                } else {
                    overlap += "0;";
                }

                if (contentBuilder.length() > 15) {
                    for (int i = 15; i < contentBuilder.length(); i += 22) {
                        contentBuilder.insert(i, "\n      ");
                    }
                }

                String formattedText = "3=" + apps.get(sbn.getPackageName()) + ";" + overlap + "5=" + title + ";6=" + contentBuilder + ";";
                Log.d("System.out.println()", formattedText);
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
        // Handle removed notifications here
    }
}

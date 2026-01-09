package com.micewine.emu.core;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.google.gson.Gson;
import com.micewine.emu.BuildConfig;
import com.micewine.emu.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class Updater {
    // Repository URL for checking updates
    private static final String REPO_URL = "WINDROID-EMU/Windroid-emu";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/" + REPO_URL + "/releases/latest";
    private static final String CHANNEL_ID = "updates_channel";

    public static void check(Context context) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(GITHUB_API_URL)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    Gson gson = new Gson();
                    GitHubRelease release = gson.fromJson(response.body().string(), GitHubRelease.class);

                    String latestVersion = release.tag_name;
                    String currentVersion = BuildConfig.VERSION_NAME;

                    // Simple string comparison for now. Ideally should parse semantic versioning.
                    // Improving this to specific check: if latest version is different from current
                    // AND not equal (assuming tag is vX.Y.Z)
                    if (!latestVersion.equals(currentVersion)) {
                        // Check if latestVersion actually looks like a version (starts with v or has
                        // number)
                        // And verify if it's "newer" or just "different".
                        // For now, if it's different, we notify.
                        // But commonly developers run dev builds with same version or different.
                        // Let's assume tags are like "v1.0.0".

                        showUpdateNotification(context, latestVersion, release.html_url);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void showUpdateNotification(Context context, String version, String url) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    context.getString(R.string.channel_updates), NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        android.app.Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new android.app.Notification.Builder(context, CHANNEL_ID);
        } else {
            builder = new android.app.Notification.Builder(context);
        }

        builder.setContentTitle(context.getString(R.string.update_available_title))
                .setContentText(String.format(context.getString(R.string.update_available_message), version))
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(2, builder.build());
    }

    private static class GitHubRelease {
        String tag_name;
        String html_url;
    }
}

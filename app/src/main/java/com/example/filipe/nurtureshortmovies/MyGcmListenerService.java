/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.filipe.nurtureshortmovies;

import android.support.v4.app.NotificationCompat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    private JSONObject header;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + data.toString());

        if (from.startsWith("/topics/"))
        {
            // message received from some topic.
        }

        else
        {
            try {
                JSONObject jsonObj = new JSONObject(message);

                header = jsonObj.getJSONObject("header");

            }catch(JSONException e) {
            }

            }


            // normal downstream message

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(header);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     *  message GCM message received.
     */
    private void sendNotification(JSONObject header) {

        String messageFromJson = "";
        String title = "";
        String imageUrl = "";

        Bitmap remote_picture = null;


        try {
            title = header.getString("title");
            messageFromJson = header.getString("message");
            imageUrl = header.getString("imageUrl");

            Log.d("Title: ", title);
            Log.d("Message: ", messageFromJson);
            Log.d("imageUrl", imageUrl);
        }catch(JSONException e){

        }

        NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
        notiStyle.setSummaryText(messageFromJson);

        try {
            remote_picture = BitmapFactory.decodeStream((InputStream) new URL(imageUrl
            ).getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        notiStyle.bigPicture(remote_picture);


        Log.d("FILIPE_DEBUG","Aqui era pra mandar a notificação");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder    .setContentTitle(title);
        notificationBuilder   .setContentText(messageFromJson);
        notificationBuilder   .setAutoCancel(true);
        notificationBuilder   .setSound(defaultSoundUri);
        notificationBuilder   .setStyle(new NotificationCompat.BigTextStyle().bigText(messageFromJson));
        notificationBuilder.setLargeIcon(remote_picture);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setStyle(notiStyle);
        notificationBuilder.build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
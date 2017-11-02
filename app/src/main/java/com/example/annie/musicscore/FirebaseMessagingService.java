package com.example.annie.musicscore;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Annie on 15-08-2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        showNotification(remoteMessage.getData().get("message"),remoteMessage.getData().get("name"),remoteMessage.getData().get("username"),
                remoteMessage.getData().get("perfil"));
    }

    private void showNotification(String message, String name, String username, String perfil) {

        Intent i = new Intent(this,MenuPpal.class);
        String fuente = "notificacion";
        i.putExtra("nombre", name);
        i.putExtra("correo",username);
        i.putExtra("perfil", perfil);
        i.putExtra("fuente", fuente);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Music Score")
                .setContentText(message)
                .setSmallIcon(R.drawable.gafas)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{0,300,200,300});

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
    }
}

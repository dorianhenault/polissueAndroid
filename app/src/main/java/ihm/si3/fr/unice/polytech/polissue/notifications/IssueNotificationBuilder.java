package ihm.si3.fr.unice.polytech.polissue.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import ihm.si3.fr.unice.polytech.polissue.R;
import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;

public class IssueNotificationBuilder {

    private IssueModel issue;
    private Context context;


    public IssueNotificationBuilder(IssueModel issue, Context context) {
        this.issue = issue;
        this.context = context;
    }

    public void build(){

        Intent intent = new Intent(context, IssueNotificationBuilder.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,context.getString(R.string.CHANNEL_ID))
                .setSmallIcon(R.mipmap.ic_logo_polissue_round)
                .setContentTitle(issue.getTitle())
                .setContentText(context.getString(R.string.notification_message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, mBuilder.build());
    }



}

package edu.monash.ljket1.activi.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.monash.ljket1.activi.R;
import edu.monash.ljket1.activi.models.Notification;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    public NotificationAdapter(Context context, List<Notification> notifications) {
        super(context, 0, notifications);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Notification notification = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_notification, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.notificationTitle);
        TextView subtitle = (TextView) convertView.findViewById(R.id.notificationSubtitle);
        if (notification != null) {
            title.setText(notification.eventTitle);
            subtitle.setText("Please leave a rating for " + notification.userId);
        }

        return convertView;
    }
}

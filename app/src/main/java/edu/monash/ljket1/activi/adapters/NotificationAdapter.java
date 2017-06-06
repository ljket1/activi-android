package edu.monash.ljket1.activi.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.monash.ljket1.activi.R;
import edu.monash.ljket1.activi.models.domain.NotificationInfo;

public class NotificationAdapter extends ArrayAdapter<NotificationInfo> {

    public NotificationAdapter(Context context, List<NotificationInfo> notifications) {
        super(context, 0, notifications);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        NotificationInfo notification = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_notification, parent, false);
        }

        ImageView category = (ImageView) convertView.findViewById(R.id.notificationCategoryImageView);
        //TODO

        TextView name = (TextView) convertView.findViewById(R.id.notificationNameTextView);
        if (notification != null) {
            name.setText(notification.getNotification().name);
        }

        return convertView;
    }
}

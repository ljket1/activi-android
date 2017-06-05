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
import edu.monash.ljket1.activi.models.Profile;
import edu.monash.ljket1.activi.models.domain.ProfileInfo;

public class AttendAdapter extends ArrayAdapter<ProfileInfo> {

    public AttendAdapter(Context context, List<ProfileInfo> attendees) {
        super(context, 0, attendees);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Profile profile = getItem(position).getProfile();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_attendee, parent, false);
        }

        TextView attendeeNameText = (TextView) convertView.findViewById(R.id.attendeeName);
        if (profile != null) {
            attendeeNameText.setText(profile.name);
        }

        return convertView;
    }
}

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
import edu.monash.ljket1.activi.models.Event;
import edu.monash.ljket1.activi.models.domain.EventInfo;

public class EventAdapter extends ArrayAdapter<EventInfo> {

    public EventAdapter(Context context, List<EventInfo> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Event event = getItem(position).getEvent();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
        }

        TextView eventTitle = (TextView) convertView.findViewById(R.id.eventItemTitle);
        TextView eventTime = (TextView) convertView.findViewById(R.id.eventItemTime);
        if (event != null) {
            eventTitle.setText(event.title);
//            eventTime.setText(event.startTime + " " + event.startDate + " - " + event.endTime + " " + event.endDate);
        }

        return convertView;
    }


}

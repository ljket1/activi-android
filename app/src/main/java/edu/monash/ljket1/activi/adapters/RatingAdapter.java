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
import edu.monash.ljket1.activi.models.Rating;

public class RatingAdapter extends ArrayAdapter<Rating> {

    public RatingAdapter(Context context, List<Rating> ratings) {
        super(context, 0, ratings);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Rating rating = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_rating, parent, false);
        }

        TextView ratingText = (TextView) convertView.findViewById(R.id.ratingItemRating);
        TextView commentText = (TextView) convertView.findViewById(R.id.ratingItemComment);
        if (rating != null) {
            ratingText.setText(rating.rating);
            commentText.setText(rating.comment);
        }

        return convertView;
    }
}

package com.takahidesato.popularphotos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoItemAdapter extends ArrayAdapter<PhotoItem> {
    public PhotoItemAdapter(Context context, List<PhotoItem> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PhotoItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
        }

        ImageView photo = (ImageView) convertView.findViewById(R.id.imv_photo);
        photo.setImageResource(0);
        Picasso.with(getContext()).load(item.imageUrl).into(photo);
        TextView caption = (TextView) convertView.findViewById(R.id.txv_caption);
        caption.setText(item.caption);

        return convertView;
    }
}

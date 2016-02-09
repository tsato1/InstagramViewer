package com.takahidesato.popularphotos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotoItemAdapter extends ArrayAdapter<PhotoItem> {
    static boolean sIsVideo;
    static String sVideoUrl;

    public PhotoItemAdapter(Context context, List<PhotoItem> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PhotoItem item = getItem(position);

        sIsVideo = item.type.equals("video")? true: false;
        if (sIsVideo) {
            sVideoUrl = item.videoUrl;
        }

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(1)
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        viewHolder.userPhoto.setImageResource(0);
        Picasso.with(getContext()).load(item.userPhotoUrl).fit().transform(transformation).into(viewHolder.userPhoto);

        viewHolder.username.setText(item.username);

        Date nowDate = new Date();
        Long longTime = new Long(nowDate.getTime()/1000);
        CharSequence result = DateUtils.getRelativeTimeSpanString(item.createdTime, longTime, DateUtils.SECOND_IN_MILLIS);
        viewHolder.timeStamp.setText(String.valueOf(result));

        if (item.type.equals("video")) {
            viewHolder.videoIcon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.videoIcon.setVisibility(View.GONE);
        }
        viewHolder.photo.setImageResource(0);
        Picasso.with(getContext()).load(item.imageUrl).placeholder(R.layout.progress_animation).into(viewHolder.photo);

        viewHolder.likesCount.setText(" " + item.likesCount + " likes");

        viewHolder.caption.setText(item.caption);

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.imv_profile_pic) ImageView userPhoto;
        @Bind(R.id.txv_username) TextView username;
        @Bind(R.id.txv_timestamp) TextView timeStamp;
        @Bind(R.id.imv_photo) ImageView photo;
        @Bind(R.id.imv_video_icon) ImageView videoIcon;
        @Bind(R.id.txv_likes_count) TextView likesCount;
        @Bind(R.id.txv_caption) TextView caption;
        @OnClick(R.id.imv_photo)
        public void onClick(View view) {
            if (sIsVideo) {
                Intent intent = new Intent(view.getContext(), VideoActivity.class);
                intent.putExtra("videoUrl", sVideoUrl);
                view.getContext().startActivity(intent);
            }
        }

        public ViewHolder (View view) {
            ButterKnife.bind(this, view);
        }
    }
}

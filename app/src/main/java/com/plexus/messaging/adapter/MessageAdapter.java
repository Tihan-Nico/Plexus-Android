package com.plexus.messaging.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.plexus.R;
import com.plexus.messaging.activity.ViewImage;
import com.plexus.messaging.activity.ViewVideo;
import com.plexus.model.messaging.Message;
import com.plexus.model.account.User;
import com.plexus.utils.MediaPlayerUtils;
import com.vanniktech.emoji.EmojiTextView;

import java.io.IOException;
import java.util.List;

/******************************************************************************
 * Copyright (c) 2020. Plexus, Inc.                                           *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 *  limitations under the License.                                            *
 ******************************************************************************/

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> implements SeekBar.OnSeekBarChangeListener{

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Message> mMessage;
    Handler mHandler;
    StorageReference storageReference;
    FirebaseUser fuser;
    MediaPlayer mediaPlayer = new MediaPlayer();
    private MediaPlayerUtils mediaPlayerUtils;
    private SeekBar seekBar;

    Message messageModel;

    public MessageAdapter(Context mContext, List<Message> mMessage){
        this.mMessage = mMessage;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @SuppressLint({"NewApi", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = mMessage.get(position);
        String type = mMessage.get(position).getType();
        storageReference = FirebaseStorage.getInstance().getReference();
        getProfileInfo(holder.profile_image, message.getSender());

        mHandler = new Handler();

        mediaPlayer = new MediaPlayer();
        mediaPlayerUtils = new MediaPlayerUtils();

        holder.seekbar.setOnSeekBarChangeListener(this);
        seekBar = holder.seekbar;

        if (position == mMessage.size()-1){
            if (message.isIsseen()){
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }

        SimpleDraweeView video_thumbnail = holder.video.findViewById(R.id.video_thumbnail);
        SeekBar audio_seekbar = holder.audio.findViewById(R.id.seekbar);
        ImageView audio_play = holder.audio.findViewById(R.id.play_audio);

        holder.play_voice_note.setOnClickListener(v -> {
            if(mediaPlayer.isPlaying()){
                if(mediaPlayer!=null){
                    mediaPlayer.pause();
                    // Changing button image to play button
                    holder.play_voice_note.setImageResource(R.drawable.play);
                }
            }else{
                // Resume audio
                if(mediaPlayer!=null){
                    mediaPlayer.start();
                    // Changing button image to pause button
                    holder.play_voice_note.setImageResource(R.drawable.pause);
                }
            }
            try {
                playAudio(message, seekBar, holder.play_voice_note);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        audio_play.setOnClickListener(v -> {
            if(mediaPlayer.isPlaying()){
                if(mediaPlayer!=null){
                    mediaPlayer.pause();
                    // Changing button image to play button
                    audio_play.setImageResource(R.drawable.play);
                }
            }else{
                // Resume audio
                if(mediaPlayer!=null){
                    mediaPlayer.start();
                    // Changing button image to pause button
                    audio_play.setImageResource(R.drawable.pause);
                }
            }
            try {
                playAudio(message, audio_seekbar, audio_play);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        switch (type) {
            case "text":
                holder.show_message.setVisibility(View.VISIBLE);
                holder.show_message.setText(message.getMessage());
                break;
            case "image":
                holder.show_image.setVisibility(View.VISIBLE);
                /*if(image.exists()){
                    holder.show_image.setImageURI(Uri.fromFile(image));
                    Toast.makeText(mContext, "loaded from storage", Toast.LENGTH_SHORT).show();
                } else {
                    holder.show_image.setImageURI(message.getDownloadURL());
                    Toast.makeText(mContext, "loaded from network", Toast.LENGTH_SHORT).show();
                }*/
                holder.show_image.setImageURI(message.getDownloadURL());
                Toast.makeText(mContext, "loaded from network", Toast.LENGTH_SHORT).show();
                break;
            case "file":
                holder.show_file.setVisibility(View.VISIBLE);
                holder.show_file.setOnClickListener(view -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mMessage.get(position).getMessage()));
                    holder.show_file.getContext().startActivity(intent);
                });
                break;
            case "voice_note":
                holder.voice_note.setVisibility(View.VISIBLE);
                break;
            case "audio":
                holder.audio.setVisibility(View.VISIBLE);
                break;
            default:
                holder.video.setVisibility(View.VISIBLE);
                /*if(video.exists()){
                    Glide.with(mContext).load(video).into(video_thumbnail);
                    Toast.makeText(mContext, "loaded from storage", Toast.LENGTH_SHORT).show();
                } else {
                    holder.show_image.setImageURI(message.getDownloadURL());
                    Toast.makeText(mContext, "loaded from network", Toast.LENGTH_SHORT).show();
                }*/
                video_thumbnail.setImageURI(message.getDownloadURL());
                Toast.makeText(mContext, "loaded from network", Toast.LENGTH_SHORT).show();
                break;
        }

        holder.show_image.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ViewImage.class);
            intent.putExtra("messageid", message.getId());
            intent.putExtra("userid", message.getSender());
            mContext.startActivity(intent);
        });

        holder.video.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ViewVideo.class);
            intent.putExtra("messageid", message.getId());
            /*if(video.exists()){
                intent.putExtra("videoUrl", video);
                Toast.makeText(mContext, "loaded from storage", Toast.LENGTH_SHORT).show();
            } else {
                intent.putExtra("videoUrl", message.getDownloadURL());
                Toast.makeText(mContext, "loaded from network", Toast.LENGTH_SHORT).show();
            }*/
            intent.putExtra("videoUrl", message.getDownloadURL());
            Toast.makeText(mContext, "loaded from network", Toast.LENGTH_SHORT).show();
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (mMessage == null)
            return 0;
        return mMessage.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public SimpleDraweeView show_image, profile_image;
        public TextView txt_seen, file_name, show_message_reply, replied_message;
        public LinearLayout show_file, reply_message;
        public EmojiTextView show_message;
        public RelativeLayout voice_note;
        public ImageView microphone, play_voice_note;
        public SeekBar seekbar;
        public View video, audio;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            file_name = itemView.findViewById(R.id.file_name);
            show_image = itemView.findViewById(R.id.show_image);
            show_file = itemView.findViewById(R.id.show_file);
            voice_note = itemView.findViewById(R.id.voice_note);
            profile_image = itemView.findViewById(R.id.profile_image);
            microphone = itemView.findViewById(R.id.microphone);
            play_voice_note = itemView.findViewById(R.id.play_voice_note);
            seekbar = itemView.findViewById(R.id.seekbar);
            reply_message = itemView.findViewById(R.id.reply_message);
            show_message_reply = itemView.findViewById(R.id.show_message_reply);
            replied_message = itemView.findViewById(R.id.replied_message);
            video = itemView.findViewById(R.id.video);
            audio = itemView.findViewById(R.id.audio);
        }
    }

    public void playAudio(Message message, SeekBar seekBar, ImageView imageView) throws IOException {
        mediaPlayer.reset();
        mediaPlayer.setDataSource(message.getMessage());
        mediaPlayer.prepare();
        mediaPlayer.start();

        // Changing Button Image to pause image
        imageView.setImageResource(R.drawable.pause);

        // set Progress bar values
        seekBar.setProgress(0);
        seekBar.setMax(100);

        // Updating progress bar
        updateProgressBar();
    }

    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            /*songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));*/

            // Updating progress bar
            int progress = (int)(mediaPlayerUtils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = mediaPlayerUtils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    private void getProfileInfo(SimpleDraweeView simpleDraweeView, String profileid){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                simpleDraweeView.setImageURI(user.getImageurl());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mMessage.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
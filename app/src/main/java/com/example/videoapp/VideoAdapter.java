package com.example.videoapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter <VideoAdapter.VideoViewHolder>{

    private int vNumItems;
    private final ListItemClickListener mOnClickListener;
    private static int viewHolderCount;
    private List<VideoResponse> mDataSet;

    public VideoAdapter(int numListItems,ListItemClickListener listener){
        vNumItems = numListItems;
        mOnClickListener = listener;
        mDataSet = null;
    }

    public void setData(List<VideoResponse> myDataSet){
        for (int i = 0;i < myDataSet.size();i++){
           myDataSet.get(i).avatar =  myDataSet.get(i).avatar.replaceFirst("http://","https://");
        }
        mDataSet = myDataSet;
    }
    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType){
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.video_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        VideoViewHolder videoHolder = new VideoViewHolder(view);

        return videoHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder videoViewHolder, int position){
        videoViewHolder.bind(position);
    }

    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView video;
        private ImageView buttonPlay;
        private TextView author,description;

        @RequiresApi(api = Build.VERSION_CODES.N)
        public VideoViewHolder(@NonNull View itemView){
            super(itemView);
            buttonPlay = itemView.findViewById(R.id.buttonPlay);
            video = itemView.findViewById(R.id.videopicture);
            description = itemView.findViewById(R.id.description);
            author = itemView.findViewById(R.id.author);
            //点击视频的listener
            itemView.setOnClickListener(this);
        }

        public void bind(int position){
            Glide.with(itemView.getContext())
                    .load(mDataSet.get(position).avatar)
                    .into(video);
            buttonPlay.setImageResource(R.drawable.play2);

            author.setText(mDataSet.get(position).nickname+"");
            description.setText(mDataSet.get(position).description+"");

        }

        @Override
        public void onClick(View v){
            int clickedPosition = getAdapterPosition();
            if (mOnClickListener != null) {
                mOnClickListener.onListItemClick(clickedPosition);
            }
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
}

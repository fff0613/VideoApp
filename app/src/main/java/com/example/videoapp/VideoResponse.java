package com.example.videoapp;

import com.google.gson.annotations.SerializedName;

public class VideoResponse {
        @SerializedName("_id")
        public String id;
        @SerializedName("feedurl")
        public String url;
        @SerializedName("nickname")
        public String nickname;
        @SerializedName("description")
        public String description;
        @SerializedName("likecount")
        public int likecount;
        @SerializedName("avatar")
        public String avatar;

        @Override
        public String toString(){
            return id + " "+nickname+" "+description+" "+likecount +" "+ avatar;
        }
}

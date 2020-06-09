package com.example.videoapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DemoActivity extends AppCompatActivity implements View.OnClickListener {
    public static Bundle outstate;
    private String LAST_PLAYED_TIME = "lasttime";
    private String TAG="change";
    private String url;
    private String description;
    private String name;
    private String likecountString;
    private float likecount;
    private int like;
    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SurfaceHolder holder;
    private SeekBar seekBar;
    private TextView tv_end;
    private TextView tv_start;
    private RelativeLayout mVideoFrameLayout;
    private int parentW;
    private int parentH;
    private Handler seekHandler;
    private int mLastPlayedTime = 0;
    private ImageView buttonPlay;
    private ImageView buttonshare;
    private int videoWidth;
    private int videoHeight;
    private int screenWidth;
    private int screenHeight;
    private Display display;
    private float downX;
    private float downY;
    private int FACTOR = 250;
    private TextView tvSound,authorView,likeView,desricptionView;
    private Handler handler;
    private Handler hearthandler;
    private int clickCount = 0;
    private static int timeout = 400;
    private ImageView heart;
    private ImageView returnimage;
    private ImageView zanheart;
    private int islike = 0;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demovideo);

        url = null;
        mLastPlayedTime = 0;

        init();
    }
    public void init(){
        if(url == null)
            url = getIntent().getStringExtra("url");
        description = getIntent().getStringExtra("description");
        likecount = (float)getIntent().getIntExtra("likecount",like);
        if(likecount >= 1000){
            likecount = likecount / 1000;
            likecountString = likecount +"k";
        }else
            likecountString = ""+(int)likecount;
        name = getIntent().getStringExtra("author");

        authorView = findViewById(R.id.author);
        authorView.setText("@"+name);
        likeView = findViewById(R.id.likecount);
        likeView.setText(likecountString);
        desricptionView = findViewById(R.id.descriptionvideo);
        desricptionView.setText(description);
        surfaceView = findViewById(R.id.surfaceView);
        mVideoFrameLayout = findViewById(R.id.relativelayout);
        parentH = surfaceView.getHeight();
        parentW = surfaceView.getWidth();
        holder = surfaceView.getHolder();
        holder.addCallback(new DemoActivity.PlayerCallBack());
        display = getWindowManager().getDefaultDisplay();
        seekBar = findViewById(R.id.seekBar);
        tv_end = findViewById(R.id.tv_end);
        tv_start = findViewById(R.id.tv_start);
        tvSound = findViewById(R.id.tv_sound);
        heart =findViewById(R.id.heart);
        returnimage = findViewById(R.id.returnimage);
        zanheart = findViewById(R.id.zan);
        handler = new Handler();
        hearthandler = new Handler();
        ScheduledExecutorService mExecutorService;

        seekHandler= new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (player != null) {
                    int currentPosition = player.getCurrentPosition();
                    tv_start.setText(toTime(currentPosition));
                    seekBar.setProgress(currentPosition);
                }
            }
        };

        //更新一下进度条
        mExecutorService = new ScheduledThreadPoolExecutor(1);
        mExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                seekHandler.sendEmptyMessage(1);
            }
        }, 1, 1, TimeUnit.SECONDS);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (player != null ) {
                    int progress = seekBar.getProgress();
                    Log.d("seekbarstop",progress+"");
                    player.seekTo(progress);
                    tv_start.setText(toTime(progress));
//                    set(seekBar.getProgress(),player.getDuration());
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (player != null && fromUser) {
                    Log.d("seekbarchange",progress+"");
                    player.seekTo(seekBar.getProgress());
                    tv_start.setText(toTime(seekBar.getProgress()));
                }
            }
        });

        buttonPlay = (ImageView)findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player != null){
                    if(player.isPlaying()){
                        player.pause();
                        buttonPlay.setImageResource(R.drawable.play2);
                    }
                    else{
                        player.start();
                        buttonPlay.setImageResource(R.drawable.pause4);
                    }
                }
            }
        });

        buttonshare = findViewById(R.id.buttonshare);
        buttonshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( Intent.ACTION_SEND);
                intent.setType( "text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(intent, "分享网络视频地址到..."));
            }
        });

        returnimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        zanheart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(islike == 0){
                    zanheart.setImageResource(R.drawable.heart6);
                    islike = 1;
                }
                else{
                    zanheart.setImageResource(R.drawable.heart4);
                    islike = 0;
                }
            }
        });

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        clickCount++;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (clickCount == 1) {
                                    onClick(v);
                                }else if(clickCount==2){
                                    doubleClick();
                                }
                                handler.removeCallbacksAndMessages(null);
                                //清空handler延时，并防内存泄漏
                                clickCount = 0;//计数清零
                            }
                        },timeout);//延时timeout后执行run方法中的代码

                        break;
                    case MotionEvent.ACTION_MOVE:
                        // TODO 音量
                        float distanceX = event.getX() - downX;
                        float distanceY = event.getY() - downY;
                        if (downX > screenWidth - 200
                                && Math.abs(distanceX) < 50
                                && distanceY > FACTOR)
                        {
                            // TODO 减小音量
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setVolume(false);
                                }
                            },200);

                        }
                        else if (downX > screenWidth - 200
                                && Math.abs(distanceX) < 50
                                && distanceY < -FACTOR)
                        {
                            // TODO 增加音量
                            setVolume(true);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setVolume(true);;
                                }
                            },200);
                        }
                        // TODO 播放进度调节
                        if (Math.abs(distanceY) < 50 && distanceX > 100)
                        {
                            // TODO 快进
                            int currentT = player.getCurrentPosition();//播放的位置
                            player.seekTo(currentT + 1000);
                            downX = event.getX();
                            downY = event.getY();
                        }
                        else if (Math.abs(distanceY) < 50
                                && distanceX < -100)
                        {
                            // TODO 快退
                            int currentT = player.getCurrentPosition();
                            player.seekTo(currentT - 1000);
                            downX = event.getX();
                            downY = event.getY();
                        }
                        break;
                }
                return true;
            }
        });
    }


    private void setVolume(boolean flag) {
        // 获取音量管理器
        AudioManager manager = (AudioManager) getSystemService(AUDIO_SERVICE);
        // 获取当前音量
        int curretnV = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (flag) {
            curretnV++;
        }
        else {
            curretnV--;
        }
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, curretnV,
                AudioManager.FLAG_SHOW_UI);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("url", url);
        outState.putInt("lasttime",player.getCurrentPosition());
        Log.d("lifedemo","save");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        url = savedInstanceState.getString("url");
        mLastPlayedTime =savedInstanceState.getInt("lasttime");
        Log.d("lifedemo","restore");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(outstate != null){
            outstate.clear();
            outstate =null;
        }
        outstate = new Bundle();
        outstate.putString("pauseurl",url);
        outstate.putInt("time",player.getCurrentPosition());

        Log.d("lifedemo","pause");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("lifedemo","resume");
    }

    @Override
    protected void onRestart(){
        super.onRestart();

        if(player != null){
            player.stop();
            player.release();
            player = null;
        }

        if(outstate != null){
            url = outstate.getString("pauseurl");
            mLastPlayedTime = outstate.getInt("time");
        }
        Log.d("lifedemo","restart");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(player != null){
            player.stop();
            player.release();
            player = null;
        }
        Log.d("lifedemo","destory");
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(player != null){
            player.pause();
        }
        Log.d("lifedemo","stop");
    }


    private String toTime(int progress){
        StringBuffer stringBuffer = new StringBuffer();
        int s = (progress/1000) % 60;
        int m = progress / 60000;
        stringBuffer.append(m).append(":");
        if(s < 10){
            stringBuffer.append(0);
        }
        stringBuffer.append(s);
        return stringBuffer.toString();
    }


    private void setscreen(){
        int surfaceWidth = surfaceView.getWidth();
        int surfaceHeight = surfaceView.getHeight();
        Log.d(TAG, "视频的宽度:" + surfaceWidth);
        Log.d(TAG, "视频的高度:" + surfaceHeight);
        if(videoWidth > videoHeight){
            surfaceHeight = surfaceWidth * videoHeight /videoWidth;
            Log.d(TAG, "视频的宽度:" + surfaceWidth);
            Log.d(TAG, "视频的高度:" + surfaceHeight);
        }
        else{
            surfaceWidth = surfaceHeight * videoWidth / videoHeight;
            Log.d(TAG, "视频的宽度:" + surfaceWidth);
            Log.d(TAG, "视频的高度:" + surfaceHeight);
        }
        Log.d(TAG, "视频的宽度:" + videoWidth);
        Log.d(TAG, "视频的高度:" + videoHeight);

        surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(surfaceWidth, surfaceHeight));
    }


    @Override
    public void onClick(View v){
        if(v.getId() == R.id.surfaceView){
            if(player != null){
                if(player.isPlaying()){
                    player.pause();
                    buttonPlay.setImageResource(R.drawable.play2);
                }
                else{
                    player.start();
                    buttonPlay.setImageResource(R.drawable.pause4);
                }
            }
        }
    }


    public void doubleClick(){
        heart.setVisibility(View.VISIBLE);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(heart,
                "scaleX", 1.3f, 0.8f);
        scaleXAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scaleXAnimator.setInterpolator(new LinearInterpolator());
        scaleXAnimator.setDuration(1000);

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(heart,
                "scaleY", 1.3f, 0.8f);
        scaleYAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scaleYAnimator.setInterpolator(new LinearInterpolator());
        scaleYAnimator.setDuration(1000);

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(heart,"alpha",1f,0f);
        animator1.setDuration(1000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator,animator1);
        animatorSet.start();

        heart.clearAnimation();
        zanheart.setImageResource(R.drawable.heart6);
        islike = 1;
    }


    private class PlayerCallBack implements SurfaceHolder.Callback{
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player = new MediaPlayer();
            try {
                url = url.replace("http://","https://");
                player.setDataSource(DemoActivity.this, Uri.parse(url));

                player.prepare();
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                            @Override
                            public boolean onError(MediaPlayer mp, int what, int extra) {
                                return false;
                            }
                        });
                        videoHeight = player.getVideoHeight();
                        videoWidth = player.getVideoWidth();
                        setscreen();
                        // 自动播放
                        player.start();
                        buttonPlay.setImageResource(R.drawable.pause4);
                        player.seekTo(mLastPlayedTime);
                        seekBar.setMax(player.getDuration());
                        tv_end.setText(toTime(player.getDuration()));
                        tv_start.setText(toTime(mLastPlayedTime));
                        player.setLooping(true);
                    }
                });
                player.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    }
                });
                player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.setDisplay(holder);

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            player.setDisplay(holder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }
}

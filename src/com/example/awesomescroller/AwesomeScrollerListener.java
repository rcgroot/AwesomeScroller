package com.example.awesomescroller;

import java.io.IOException;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class AwesomeScrollerListener implements OnScrollListener, AnimatorUpdateListener, AnimatorListener
{
   private static final int FADEOUT_DURATION = 2500;
   private static final int FADEIN_DURATION = 1500;
   private static final String TAG = "AwesomeScrollerListener";
   private static MediaPlayer player;
   private static int state = -1;

   final private int STATE_FADE_OUT = 1;
   final private int STATE_FADE_IN = 2;
   final private int STATE_PLAYING = 3;
   final private int STATE_PAUSED = 4;

   private float volume = 1.0F;
   ValueAnimator animator = new ValueAnimator();

   AwesomeScrollerListener(Context ctx, String assetFileName)
   {
      AssetFileDescriptor afd;
      try
      {
         afd = ctx.getAssets().openFd(assetFileName);
         if (player == null)
         {
            player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.setLooping(true);
            player.prepare();
            player.setVolume(0.0F, 0.0F);
            state = STATE_PAUSED;
         }
      }
      catch (IOException e)
      {
         Log.e(TAG, "Failed to be awesome", e);
         player = null;
      }
   }

   @Override
   public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
   {
   }

   @Override
   public void onScrollStateChanged(AbsListView view, int scrollState)
   {
      switch (scrollState)
      {
         case SCROLL_STATE_IDLE:
            if (state != STATE_PAUSED || state != STATE_FADE_OUT)
            {
               animator.cancel();
               animator = ValueAnimator.ofFloat(volume, 0.0F);
               animator.setDuration(FADEOUT_DURATION);
               animator.addUpdateListener(this);
               animator.addListener(this);
               animator.start();
               state = STATE_FADE_OUT;
            }
            break;
         default:
            if (state == STATE_PAUSED || state == STATE_FADE_OUT)
            {
               if (!player.isPlaying())
               {
                  player.start();
               }
               animator.cancel();
               animator = ValueAnimator.ofFloat(volume, 1.0F);
               animator.setDuration(FADEIN_DURATION);
               animator.addUpdateListener(this);
               animator.addListener(this);
               animator.start();
               state = STATE_FADE_IN;
            }
            break;
      }
   }

   @Override
   public void onAnimationUpdate(ValueAnimator arg0)
   {
      volume = (Float) arg0.getAnimatedValue();
      player.setVolume(volume, volume);
   }

   @Override
   public void onAnimationEnd(Animator arg0)
   {
      if (volume == 0.0F)
      {
         if (player.isPlaying())
         {
            player.pause();
         }
         state = STATE_PAUSED;
      }
      else if (volume == 1.0F)
      {
         state = STATE_PLAYING;
      }
   }

   @Override
   public void onAnimationRepeat(Animator arg0)
   {
   }

   @Override
   public void onAnimationStart(Animator arg0)
   {
   }

   @Override
   public void onAnimationCancel(Animator arg0)
   {
   }

}
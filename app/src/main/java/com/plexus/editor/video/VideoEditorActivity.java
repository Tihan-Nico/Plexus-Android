package com.plexus.editor.video;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import com.plexus.R;
import com.plexus.core.editor.video.components.VideoSeekBarView;
import com.plexus.core.editor.video.components.VideoTimelineView;
import com.plexus.core.editor.video.utils.AndroidUtilities;

import java.io.File;
import java.util.List;

@TargetApi(16)
public class VideoEditorActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    boolean created = false;
    private MediaPlayer videoPlayer = null;
    private VideoTimelineView videoTimelineView = null;
    View videoContainerView = null;
    private TextView originalSizeTextView = null;
    private TextView editedSizeTextView = null;
     View textContainerView = null;
    private ImageView playButton = null;
    private VideoSeekBarView videoSeekBarView = null;
    private TextureView textureView = null;
    View controlView = null;
    private CheckBox compressVideo = null;
    private boolean playerPrepared = false;
    private String videoPath = "/mnt/sdcard/bichooser/SANAM RE Title Song (VIDEO) - Pulkit Samrat, Yami Gautam, Divya Khosla Kumar - T-Series.mp4";
    private float lastProgress = 0;
    private boolean needSeek = false;
    private VideoEditorActivityDelegate delegate;

    private final Object sync = new Object();
    private Thread thread = null;

    private int rotationValue = 0;
    private int originalWidth = 0;
    private int originalHeight = 0;
    private int resultWidth = 0;
    private int resultHeight = 0;
    private int bitrate = 0;
    private int originalBitrate = 0;
    private float videoDuration = 0;
    private long startTime = 0;
    private long endTime = 0;
    private long audioFramesSize = 0;
    private long videoFramesSize = 0;
    private int estimatedSize = 0;
    private long esimatedDuration = 0;
    private long originalSize = 0;

    public interface VideoEditorActivityDelegate {
        void didFinishEditVideo(String videoPath, long startTime, long endTime, int resultWidth, int resultHeight, int rotationValue, int originalWidth, int originalHeight, int bitrate, long estimatedSize, long estimatedDuration);
    }

    private Runnable progressRunnable = () -> {
        boolean playerCheck;

        while (true) {
            synchronized (sync) {
                try {
                    playerCheck = videoPlayer != null && videoPlayer.isPlaying();
                } catch (Exception e) {
                    playerCheck = false;
                    Log.e("tmessages", e.toString());
                }
            }
            if (!playerCheck) {
                break;
            }
            runOnUiThread(() -> {
                if (videoPlayer != null && videoPlayer.isPlaying()) {
                    float startTime = videoTimelineView.getLeftProgress() * videoDuration;
                    float endTime = videoTimelineView.getRightProgress() * videoDuration;
                    if (startTime == endTime) {
                        startTime = endTime - 0.01f;
                    }
                    float progress = (videoPlayer.getCurrentPosition() - startTime) / (endTime - startTime);
                    float lrdiff = videoTimelineView.getRightProgress() - videoTimelineView.getLeftProgress();
                    progress = videoTimelineView.getLeftProgress() + lrdiff * progress;
                    if (progress > lastProgress) {
                        videoSeekBarView.setProgress(progress);
                        lastProgress = progress;
                    }
                    if (videoPlayer.getCurrentPosition() >= endTime) {
                        try {
                            videoPlayer.pause();
                            onPlayComplete();
                        } catch (Exception e) {

                        }
                    }
                }
            });

            try {
                Thread.sleep(50);
            } catch (Exception e) {
                Log.e("tmessages", e.toString());
            }
        }
        synchronized (sync) {
            thread = null;
        }
    };


    @Override
    protected void onStart() {
        super.onStart();

        processOpenVideo();


        videoPlayer = new MediaPlayer();
        videoPlayer.setOnCompletionListener(mp -> onPlayComplete());
        videoPlayer.setOnPreparedListener(mp -> {
            playerPrepared = true;
            if (videoTimelineView != null && videoPlayer != null) {
                videoPlayer.seekTo((int) (videoTimelineView.getLeftProgress() * videoDuration));
            }
        });
        try {
            videoPlayer.setDataSource(videoPath);
            videoPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e("tmessages", e.toString());

        }


        created = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videoTimelineView != null) {
            videoTimelineView.destroy();
        }
        if (videoPlayer != null) {
            try {
                videoPlayer.stop();
                videoPlayer.release();
                videoPlayer = null;
            } catch (Exception e) {
                Log.e("tmessages", e.toString());
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_video_editor_layout);


        originalSizeTextView = (TextView) findViewById(R.id.original_size);
        editedSizeTextView = (TextView) findViewById(R.id.edited_size);
        videoContainerView = findViewById(R.id.video_container);
        textContainerView = findViewById(R.id.info_container);
        controlView = findViewById(R.id.control_layout);
        compressVideo = (CheckBox) findViewById(R.id.compress_video);
        compressVideo.setText("Copress Video");
        SharedPreferences preferences = getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        compressVideo.setVisibility(originalHeight != resultHeight || originalWidth != resultWidth ? View.VISIBLE : View.GONE);
        compressVideo.setChecked(preferences.getBoolean("compress_video", true));
        compressVideo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(VideoEditorActivity.this);
            SharedPreferences.Editor editor = preferences1.edit();
            editor.putBoolean("compress_video", isChecked);
            editor.commit();

        });

        if (Build.VERSION.SDK_INT < 18) {
/*
            try {
                MediaCodecInfo codecInfo = MediaController.selectCodec(MediaController.MIME_TYPE);
                if (codecInfo == null) {
                    compressVideo.setVisibility(View.GONE);
                } else {
                    String name = codecInfo.getName();
                    if (name.equals("OMX.google.h264.encoder") ||
                            name.equals("OMX.ST.VFM.H264Enc") ||
                            name.equals("OMX.Exynos.avc.enc") ||
                            name.equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER") ||
                            name.equals("OMX.MARVELL.VIDEO.H264ENCODER") ||
                            name.equals("OMX.k3.video.encoder.avc") || //fix this later
                            name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) { //fix this later
                        compressVideo.setVisibility(View.GONE);
                    } else {
                        if (MediaController.selectColorFormat(codecInfo, MediaController.MIME_TYPE) == 0) {
                            compressVideo.setVisibility(View.GONE);
                        }
                    }
                }
            } catch (Exception e) {
                compressVideo.setVisibility(View.GONE);
                Log.e("tmessages", e.toString());
            }
*/
        }

        TextView titleTextView = (TextView) findViewById(R.id.original_title);
        titleTextView.setText("Original Video");
        titleTextView = (TextView) findViewById(R.id.edited_title);
        titleTextView.setText("Edited Video");

        videoTimelineView = (VideoTimelineView) findViewById(R.id.video_timeline_view);
        videoTimelineView.setVideoPath(videoPath);
        videoTimelineView.setDelegate(new VideoTimelineView.VideoTimelineViewDelegate() {
            @Override
            public void onLeftProgressChanged(float progress) {
                if (videoPlayer == null || !playerPrepared) {
                    return;
                }
                try {
                    if (videoPlayer.isPlaying()) {
                        videoPlayer.pause();
                        playButton.setImageResource(R.drawable.play);
                    }
                    videoPlayer.setOnSeekCompleteListener(null);
                    videoPlayer.seekTo((int) (videoDuration * progress));
                } catch (Exception e) {

                }
                needSeek = true;
                videoSeekBarView.setProgress(videoTimelineView.getLeftProgress());
                updateVideoEditedInfo();
            }

            @Override
            public void onRifhtProgressChanged(float progress) {
                if (videoPlayer == null || !playerPrepared) {
                    return;
                }
                try {
                    if (videoPlayer.isPlaying()) {
                        videoPlayer.pause();
                        playButton.setImageResource(R.drawable.play);
                    }
                    videoPlayer.setOnSeekCompleteListener(null);
                    videoPlayer.seekTo((int) (videoDuration * progress));
                } catch (Exception e) {

                }
                needSeek = true;
                videoSeekBarView.setProgress(videoTimelineView.getLeftProgress());
                updateVideoEditedInfo();
            }
        });

        videoSeekBarView = (VideoSeekBarView) findViewById(R.id.video_seekbar);
        videoSeekBarView.delegate = progress -> {
            if (progress < videoTimelineView.getLeftProgress()) {
                progress = videoTimelineView.getLeftProgress();
                videoSeekBarView.setProgress(progress);
            } else if (progress > videoTimelineView.getRightProgress()) {
                progress = videoTimelineView.getRightProgress();
                videoSeekBarView.setProgress(progress);
            }
            if (videoPlayer == null || !playerPrepared) {
                return;
            }
            if (videoPlayer.isPlaying()) {
                try {
                    videoPlayer.seekTo((int) (videoDuration * progress));
                    lastProgress = progress;
                } catch (Exception e) {

                }
            } else {
                lastProgress = progress;
                needSeek = true;
            }
        };

        playButton = (ImageView) findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        textureView = (TextureView) findViewById(R.id.video_view);
        textureView.setSurfaceTextureListener(this);

        updateVideoOriginalInfo();
        updateVideoEditedInfo();


    }


    private void setPlayerSurface() {
        if (textureView == null || !textureView.isAvailable() || videoPlayer == null) {
            return;
        }
        try {
            Surface s = new Surface(textureView.getSurfaceTexture());
            videoPlayer.setSurface(s);
            if (playerPrepared) {
                videoPlayer.seekTo((int) (videoTimelineView.getLeftProgress() * videoDuration));
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fixLayoutInternal();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        setPlayerSurface();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (videoPlayer == null) {
            return true;
        }
        videoPlayer.setDisplay(null);
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void onPlayComplete() {
        if (playButton != null) {
            playButton.setImageResource(R.drawable.play);
        }
        if (videoSeekBarView != null && videoTimelineView != null) {
            videoSeekBarView.setProgress(videoTimelineView.getLeftProgress());
        }
        try {
            if (videoPlayer != null) {
                if (videoTimelineView != null) {
                    videoPlayer.seekTo((int) (videoTimelineView.getLeftProgress() * videoDuration));
                }
            }
        } catch (Exception e) {

        }
    }

    private void updateVideoOriginalInfo() {
        if (originalSizeTextView == null) {
            return;
        }
        int width = rotationValue == 90 || rotationValue == 270 ? originalHeight : originalWidth;
        int height = rotationValue == 90 || rotationValue == 270 ? originalWidth : originalHeight;
        String videoDimension = String.format("%dx%d", width, height);
        long duration = (long) Math.ceil(videoDuration);
        int minutes = (int) (duration / 1000 / 60);
        int seconds = (int) Math.ceil(duration / 1000) - minutes * 60;
        String videoTimeSize = String.format("%d:%02d, %s", minutes, seconds, AndroidUtilities.formatFileSize(originalSize));
        originalSizeTextView.setText(String.format("%s, %s", videoDimension, videoTimeSize));
    }

    private void updateVideoEditedInfo() {
        if (editedSizeTextView == null) {
            return;
        }
        esimatedDuration = (long) Math.ceil((videoTimelineView.getRightProgress() - videoTimelineView.getLeftProgress()) * videoDuration);

        int width;
        int height;

        if (compressVideo.getVisibility() == View.GONE || compressVideo.getVisibility() == View.VISIBLE && !compressVideo.isChecked()) {
            width = rotationValue == 90 || rotationValue == 270 ? originalHeight : originalWidth;
            height = rotationValue == 90 || rotationValue == 270 ? originalWidth : originalHeight;
            estimatedSize = (int) (originalSize * ((float) esimatedDuration / videoDuration));
        } else {
            width = rotationValue == 90 || rotationValue == 270 ? resultHeight : resultWidth;
            height = rotationValue == 90 || rotationValue == 270 ? resultWidth : resultHeight;
            estimatedSize = calculateEstimatedSize((float) esimatedDuration / videoDuration);
        }

        if (videoTimelineView.getLeftProgress() == 0) {
            startTime = -1;
        } else {
            startTime = (long) (videoTimelineView.getLeftProgress() * videoDuration) * 1000;
        }
        if (videoTimelineView.getRightProgress() == 1) {
            endTime = -1;
        } else {
            endTime = (long) (videoTimelineView.getRightProgress() * videoDuration) * 1000;
        }

        String videoDimension = String.format("%dx%d", width, height);
        int minutes = (int) (esimatedDuration / 1000 / 60);
        int seconds = (int) Math.ceil(esimatedDuration / 1000) - minutes * 60;
        String videoTimeSize = String.format("%d:%02d, %s", minutes, seconds, AndroidUtilities.formatFileSize(estimatedSize));
        editedSizeTextView.setText(String.format("%s, %s", videoDimension, videoTimeSize));
    }

    private void fixVideoSize() {

/*
        int viewHeight = AndroidUtilities.dp(0);


        int width;
        int height;

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                width = AndroidUtilities.dp(100) - AndroidUtilities.dp(24);
                height = viewHeight - AndroidUtilities.dp(32);
            } else {
                width = AndroidUtilities.dp(150);
                height = viewHeight - AndroidUtilities.dp(276 + (compressVideo.getVisibility() == View.VISIBLE ? 20 : 0));

        }

        int aWidth = width;
        int aHeight = height;
        int vwidth = rotationValue == 90 || rotationValue == 270 ? originalHeight : originalWidth;
        int vheight = rotationValue == 90 || rotationValue == 270 ? originalWidth : originalHeight;
        float wr = (float) width / (float) vwidth;
        float hr = (float) height / (float) vheight;
        float ar = (float) vwidth / (float) vheight;

        if (wr > hr) {
            width = (int) (height * ar);
        } else {
            height = (int) (width / ar);
        }

        if (textureView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) textureView.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            layoutParams.leftMargin = 0;
            layoutParams.topMargin = 0;
            textureView.setLayoutParams(layoutParams);
        }
*/
    }

    private void fixLayoutInternal() {


//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) videoContainerView.getLayoutParams();
//        layoutParams.topMargin = AndroidUtilities.dp(16);
//        layoutParams.bottomMargin = AndroidUtilities.dp(260 + (compressVideo.getVisibility() == View.VISIBLE ? 20 : 0));
//        layoutParams.width = LayoutHelper.MATCH_PARENT;
//        layoutParams.leftMargin = 0;
//        videoContainerView.setLayoutParams(layoutParams);
//
//        layoutParams = (FrameLayout.LayoutParams) controlView.getLayoutParams();
//        layoutParams.topMargin = 0;
//        layoutParams.leftMargin = 0;
//        layoutParams.bottomMargin = AndroidUtilities.dp(150 + (compressVideo.getVisibility() == View.VISIBLE ? 20 : 0));
//        layoutParams.width = LayoutHelper.MATCH_PARENT;
//        layoutParams.gravity = Gravity.BOTTOM;
//        controlView.setLayoutParams(layoutParams);
//
//        layoutParams = (FrameLayout.LayoutParams) textContainerView.getLayoutParams();
//        layoutParams.width = LayoutHelper.MATCH_PARENT;
//        layoutParams.leftMargin = AndroidUtilities.dp(16);
//        layoutParams.rightMargin = AndroidUtilities.dp(16);
//        layoutParams.bottomMargin = AndroidUtilities.dp(16);
//        textContainerView.setLayoutParams(layoutParams);
//
//        fixVideoSize();
//        videoTimelineView.clearFrames();
    }


    private void play() {
        if (videoPlayer == null || !playerPrepared) {
            return;
        }
        if (videoPlayer.isPlaying()) {
            videoPlayer.pause();
            playButton.setImageResource(R.drawable.play);
        } else {
            try {
                playButton.setImageDrawable(null);
                lastProgress = 0;
                if (needSeek) {
                    videoPlayer.seekTo((int) (videoDuration * videoSeekBarView.getProgress()));
                    needSeek = false;
                }
                Log.e("progress", lastProgress + "b");

                videoPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        float startTime = videoTimelineView.getLeftProgress() * videoDuration;
                        float endTime = videoTimelineView.getRightProgress() * videoDuration;
                        if (startTime == endTime) {
                            startTime = endTime - 0.01f;
                        }
                        lastProgress = (videoPlayer.getCurrentPosition() - startTime) / (endTime - startTime);
                        float lrdiff = videoTimelineView.getRightProgress() - videoTimelineView.getLeftProgress();
                        lastProgress = videoTimelineView.getLeftProgress() + lrdiff * lastProgress;
                        videoSeekBarView.setProgress(lastProgress);
                        Log.e("progress", lastProgress + "a");
                    }
                });
                videoPlayer.start();
                synchronized (sync) {
                    if (thread == null) {
                        thread = new Thread(progressRunnable);
                        thread.start();
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    public void setDelegate(VideoEditorActivityDelegate delegate) {
        this.delegate = delegate;
    }

    private boolean processOpenVideo() {
        try {
            File file = new File(videoPath);
            originalSize = file.length();

            IsoFile isoFile = new IsoFile(videoPath);
            List<Box> boxes = Path.getPaths(isoFile, "/moov/trak/");
            TrackHeaderBox trackHeaderBox = null;
            boolean isAvc = true;
            boolean isMp4A = true;

            Box boxTest = Path.getPath(isoFile, "/moov/trak/mdia/minf/stbl/stsd/mp4a/");
            if (boxTest == null) {
                isMp4A = false;
            }

            if (!isMp4A) {
                return false;
            }

            boxTest = Path.getPath(isoFile, "/moov/trak/mdia/minf/stbl/stsd/avc1/");
            if (boxTest == null) {
                isAvc = false;
            }

            for (Box box : boxes) {
                TrackBox trackBox = (TrackBox) box;
                long sampleSizes = 0;
                long trackBitrate = 0;
                try {
                    MediaBox mediaBox = trackBox.getMediaBox();
                    MediaHeaderBox mediaHeaderBox = mediaBox.getMediaHeaderBox();
                    SampleSizeBox sampleSizeBox = mediaBox.getMediaInformationBox().getSampleTableBox().getSampleSizeBox();
                    for (long size : sampleSizeBox.getSampleSizes()) {
                        sampleSizes += size;
                    }
                    videoDuration = (float) mediaHeaderBox.getDuration() / (float) mediaHeaderBox.getTimescale();
                    trackBitrate = (int) (sampleSizes * 8 / videoDuration);
                } catch (Exception e) {

                }
                TrackHeaderBox headerBox = trackBox.getTrackHeaderBox();
                if (headerBox.getWidth() != 0 && headerBox.getHeight() != 0) {
                    trackHeaderBox = headerBox;
                    originalBitrate = bitrate = (int) (trackBitrate / 100000 * 100000);
                    if (bitrate > 900000) {
                        bitrate = 900000;
                    }
                    videoFramesSize += sampleSizes;
                } else {
                    audioFramesSize += sampleSizes;
                }
            }
            if (trackHeaderBox == null) {
                return false;
            }

            Matrix matrix = trackHeaderBox.getMatrix();
            if (matrix.equals(Matrix.ROTATE_90)) {
                rotationValue = 90;
            } else if (matrix.equals(Matrix.ROTATE_180)) {
                rotationValue = 180;
            } else if (matrix.equals(Matrix.ROTATE_270)) {
                rotationValue = 270;
            }
            resultWidth = originalWidth = (int) trackHeaderBox.getWidth();
            resultHeight = originalHeight = (int) trackHeaderBox.getHeight();

            if (resultWidth > 640 || resultHeight > 640) {
                float scale = resultWidth > resultHeight ? 640.0f / resultWidth : 640.0f / resultHeight;
                resultWidth *= scale;
                resultHeight *= scale;
                if (bitrate != 0) {
                    bitrate *= Math.max(0.5f, scale);
                    videoFramesSize = (long) (bitrate / 8 * videoDuration);
                }
            }

            if (!isAvc && (resultWidth == originalWidth || resultHeight == originalHeight)) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }

        videoDuration *= 1000;

        updateVideoOriginalInfo();
        updateVideoEditedInfo();

        return true;
    }

    private int calculateEstimatedSize(float timeDelta) {
        int size = (int) ((audioFramesSize + videoFramesSize) * timeDelta);
        size += size / (32 * 1024) * 16;
        return size;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            synchronized (sync) {
                if (videoPlayer != null) {
                    try {
                        videoPlayer.stop();
                        videoPlayer.release();
                        videoPlayer = null;
                    } catch (Exception e) {

                    }
                }
            }
            if (delegate != null) {
                if (compressVideo.getVisibility() == View.GONE || compressVideo.getVisibility() == View.VISIBLE && !compressVideo.isChecked()) {
                    delegate.didFinishEditVideo(videoPath, startTime, endTime, originalWidth, originalHeight, rotationValue, originalWidth, originalHeight, originalBitrate, estimatedSize, esimatedDuration);
                } else {
                    delegate.didFinishEditVideo(videoPath, startTime, endTime, resultWidth, resultHeight, rotationValue, originalWidth, originalHeight, bitrate, estimatedSize, esimatedDuration);
                }
            }

            return true;

        }

        return super.onOptionsItemSelected(item);
    }


}

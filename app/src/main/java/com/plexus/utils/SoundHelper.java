package com.plexus.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import androidx.fragment.app.FragmentActivity;

import com.plexus.R;

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

public class SoundHelper {
    private final SoundPool mSoundPool;
    private final int mSoundID;
    private final float mVolume;
    private boolean mLoaded;

    public SoundHelper(FragmentActivity activity) {
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mVolume = (float) (audioManager != null ? audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) : 0) / (float) (audioManager != null ? audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) : 0);
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mSoundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(6).build();
        mSoundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> mLoaded = true);
        mSoundID = mSoundPool.load(activity, R.raw.pop_sound_effect, 1);
    }

    /**
     * This method is responsible for play pop sound on sending new message.
     *
     * @see SoundPool#play(int, float, float, int, int, float)
     */
    public void playSound() {
        if (mLoaded) mSoundPool.play(mSoundID, mVolume, mVolume, 1, 0, 3f);
    }
}

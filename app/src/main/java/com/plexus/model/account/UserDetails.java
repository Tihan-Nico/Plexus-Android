package com.plexus.model.account;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.plexus.database.UserDatabase;
import com.plexus.wallpaper.ChatWallpaper;

import java.util.List;

public class UserDetails {

    final String id;
    final String name;
    final String surname;
    final String username;
    final String country;
    final String bio;
    final String imageurl;
    final String profile_cover;
    final String account_type;
    final boolean verified;
    final boolean user;
    final boolean company;
    final boolean artist;
    final String birthday;
    final String gender;
    final String website;
    final String feeling;
    final String registration_time;
    final boolean active;
    final String online_presence;
    final boolean private_account;
    final String blocked_timestamp;
    final String blocked_platform;
    final String nickname;
    final boolean banned;
    final String token;
    final long online;
    final Uri messageRingtone;
    final Uri callRingtone;
    final long mutedUntil;
    UserDatabase.VibrateState messageVibrateState;
    UserDatabase.VibrateState callVibrateState;
    final boolean blocked;
    final boolean isSelf;
    String notificationChannel;
    final UserDatabase.MentionSetting mentionSetting;
    final ChatWallpaper wallpaper;

    public UserDetails(boolean isSelf, @NonNull UserDatabase.RecipientSettings settings) {
        this.id = null;
        this.name = null;
        this.surname = null;
        this.username = settings.getUsername();
        this.country = null;
        this.bio = null;
        this.imageurl = null;
        this.profile_cover = null;
        this.account_type = null;
        this.verified = false;
        this.user = false;
        this.company = false;
        this.artist = false;
        this.birthday = null;
        this.gender = null;
        this.website = null;
        this.feeling = null;
        this.registration_time = null;
        this.active = true;
        this.online_presence = null;
        this.private_account = false;
        this.blocked_timestamp = null;
        this.blocked_platform = null;
        this.nickname = null;
        this.banned = false;
        this.token = null;
        this.online = 0;
        this.messageRingtone = settings.getMessageRingtone();
        this.callRingtone = settings.getCallRingtone();
        this.mutedUntil = settings.getMuteUntil();
        this.messageVibrateState = settings.getMessageVibrateState();
        this.callVibrateState = settings.getCallVibrateState();
        this.blocked = settings.isBlocked();
        this.isSelf = isSelf;
        this.notificationChannel = settings.getNotificationChannel();
        this.mentionSetting = settings.getMentionSetting();
        this.wallpaper = settings.getWallpaper();
    }

    /**
     * Only used for {@link User#UNKNOWN}.
     */
    UserDetails() {
        this.id = null;
        this.name = null;
        this.surname = null;
        this.username = null;
        this.country = null;
        this.bio = null;
        this.imageurl = null;
        this.profile_cover = null;
        this.account_type = null;
        this.verified = false;
        this.user = false;
        this.company = false;
        this.artist = false;
        this.birthday = null;
        this.gender = null;
        this.website = null;
        this.feeling = null;
        this.registration_time = null;
        this.active = true;
        this.online_presence = null;
        this.private_account = false;
        this.blocked_timestamp = null;
        this.blocked_platform = null;
        this.nickname = null;
        this.banned = false;
        this.token = null;
        this.online = 0;
        this.messageRingtone = null;
        this.callRingtone = null;
        this.mutedUntil = 0;
        this.blocked = false;
        this.isSelf = false;
        this.mentionSetting = UserDatabase.MentionSetting.ALWAYS_NOTIFY;
        this.wallpaper = null;
    }
}

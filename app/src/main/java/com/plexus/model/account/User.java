package com.plexus.model.account;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.plexus.core.utils.guava.Preconditions;
import com.plexus.core.utils.logging.Log;
import com.plexus.dependecies.PlexusDependencies;
import com.plexus.keyvalue.PlexusStore;
import com.plexus.keyvalue.WallpaperValues;
import com.plexus.wallpaper.ChatWallpaper;

import java.util.Objects;

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

public class User {

    private static final String TAG = Log.tag(User.class);

    public static final User UNKNOWN = new User(UserId.UNKNOWN, null, true);

    public String username;
    public String name;
    public String surname;
    public String country;
    public String bio;
    private UserId id;
    private String imageurl;
    private String profile_cover;
    private String account_type;
    private boolean verified;
    private boolean user;
    private boolean company;
    private boolean artist;
    private String birthday;
    private String gender;
    private String website;
    private String feeling;
    private String registration_time;
    private boolean active;
    private String online_presence;
    private boolean private_account;
    private boolean blocked;
    private String blocked_timestamp;
    private String blocked_platform;
    private String nickname;
    private boolean banned;
    private String token;
    private long online;
    private boolean verifiedBefore;
    public static ChatWallpaper wallpaper;
    private final boolean                resolving;

    //Error
    private boolean encryption_error_fixed;

    /**
     * Returns a {@link LiveUser}, which contains a {@link User} that may or may not be
     * populated with data. However, you can observe the value that's returned to be notified when the
     * {@link User} changes.
     */
    @AnyThread
    public static @NonNull LiveUser live(@NonNull  UserId user) {
        Preconditions.checkNotNull(user, "ID cannot be null.");
        return PlexusDependencies.getRecipientCache().getLive(user);
    }

    /**
     * Returns a fully-populated {@link User}. May hit the disk, and therefore should be
     * called on a background thread.
     */
    @WorkerThread
    public static @NonNull User resolved(@NonNull UserId id) {
        Preconditions.checkNotNull(id, "ID cannot be null.");
        return live(id).resolve();
    }

    User(@NonNull UserId id) {
        this.id                          = id;
        this.resolving                   = true;
        this.username                    = null;
        this.blocked                     = false;
        this.wallpaper                   = null;
    }

    public User(@NonNull UserId id, @NonNull UserDetails details, boolean resolved) {
        this.id                          = id;
        this.resolving                   = !resolved;
        this.username                    = details.username;
        this.blocked                     = details.blocked;
        this.wallpaper                   = details.wallpaper;
    }

    public UserId getId() {
        return id;
    }

    public void setId(UserId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isUser() {
        return user;
    }

    public void setUser(boolean user) {
        this.user = user;
    }

    public boolean isCompany() {
        return company;
    }

    public void setCompany(boolean company) {
        this.company = company;
    }

    public boolean isArtist() {
        return artist;
    }

    public void setArtist(boolean artist) {
        this.artist = artist;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    public String getRegistration_time() {
        return registration_time;
    }

    public void setRegistration_time(String registration_time) {
        this.registration_time = registration_time;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getProfile_cover() {
        return profile_cover;
    }

    public void setProfile_cover(String profile_cover) {
        this.profile_cover = profile_cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBlocked_timestamp() {
        return blocked_timestamp;
    }

    public void setBlocked_timestamp(String blocked_timestamp) {
        this.blocked_timestamp = blocked_timestamp;
    }

    public String getBlocked_platform() {
        return blocked_platform;
    }

    public void setBlocked_platform(String blocked_platform) {
        this.blocked_platform = blocked_platform;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isPrivate_account() {
        return private_account;
    }

    public void setPrivate_account(boolean private_account) {
        this.private_account = private_account;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOnline_presence() {
        return online_presence;
    }

    public void setOnline_presence(String online_presence) {
        this.online_presence = online_presence;
    }

    public long getOnline() {
        return online;
    }

    public void setOnline(long online) {
        this.online = online;
    }

    public boolean isVerifiedBefore() {
        return verifiedBefore;
    }

    public void setVerifiedBefore(boolean verifiedBefore) {
        this.verifiedBefore = verifiedBefore;
    }

    public boolean isEncryption_error_fixed() {
        return encryption_error_fixed;
    }

    public void setEncryption_error_fixed(boolean encryption_error_fixed) {
        this.encryption_error_fixed = encryption_error_fixed;
    }

    public static User from(@NonNull String id) {
        return User.from(id);
    }

    public @Nullable
    static ChatWallpaper getWallpaper() {
        if (wallpaper != null) {
            return wallpaper;
        } else {
            return PlexusStore.wallpaper().getWallpaper();
        }
    }

    public static boolean hasOwnWallpaper() {
        return wallpaper != null;
    }

    /**
     * A cheap way to check if wallpaper is set without doing any unnecessary proto parsing.
     */
    public boolean hasWallpaper() {
        return wallpaper != null || PlexusStore.wallpaper().hasWallpaperSet();
    }

    /**
     * If this recipient is missing crucial data, this will return a populated copy. Otherwise it
     * returns itself.
     */
    public @NonNull User resolve() {
        if (resolving) {
            return live().resolve();
        } else {
            return this;
        }
    }

    public boolean isResolving() {
        return resolving;
    }

    public @NonNull LiveUser live() {
        return PlexusDependencies.getRecipientCache().getLive(id);
    }

}

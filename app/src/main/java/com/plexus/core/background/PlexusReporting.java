package com.plexus.core.background;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.github.tntkhang.gmailsenderlibrary.GMailSender;
import com.github.tntkhang.gmailsenderlibrary.GmailListener;
import com.plexus.R;

public class PlexusReporting {

    @SuppressLint("HardwareIds")
    public static void sendMail(Context mContext, String reporterID, String reporterEmail, String userID, String contentID, String reporterInput) throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
        GMailSender.withAccount("tihannicopaxton2@gmail.com", "Chocolates123")
                .withTitle("Report - Plexus Android")
                .withBody("Report"
                        + "\n"
                        + "\n" + reporterInput
                        + "\n"
                        + "\n Plexus User Information"
                        + "\n"
                        + "\n Users User ID: " + reporterID
                        + "\n Users Email: " + reporterEmail
                        + "\n"
                        + "\n Reported User ID" + userID
                        + "\n Content ID: " + contentID
                        + "\n"
                        + "\n Plexus Application Information"
                        + "\n"
                        + "\n APP Package Name: " + mContext.getPackageName()
                        + "\n APP Version Name: " + pInfo.versionName
                        + "\n APP Version Code: " + pInfo.versionCode
                        + "\n"
                        + "\n Device Information"
                        + "\n"
                        + "\n OS Version: " + System.getProperty("os.version") + " (" + android.os.Build.VERSION.INCREMENTAL + ")"
                        + "\n Device: " + android.os.Build.DEVICE
                        + "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")"
                        + "\n Manufacturer: " + android.os.Build.MANUFACTURER
                        + "\n Other TAGS: " + android.os.Build.TAGS
                )
                .withSender(mContext.getString(R.string.app_name))
                .toEmailAddress("support@plexus.dev, tihan-nico@plexus.dev, andrew-moore@plexus.dev")
                .withListenner(new GmailListener() {
                    @Override
                    public void sendSuccess() {
                        Toast.makeText(mContext, "Reported", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void sendFail(String err) {
                        Toast.makeText(mContext, "Failed: " + err, Toast.LENGTH_SHORT).show();
                    }
                })
                .send();
    }

}

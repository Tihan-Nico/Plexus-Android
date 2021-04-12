package com.plexus;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.plexus.core.utils.logging.Log;
import com.plexus.utils.AppStartup;
import com.plexus.utils.tracing.Tracer;

import java.util.Locale;

public class TracerActivity extends BaseActivity {

    public static final String LOCALE_EXTRA      = "locale_extra";
    public static final String NEXT_INTENT_EXTRA = "next_intent";

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        Tracer.getInstance().start(Log.tag(getClass()) + "#onCreate()");
        AppStartup.getInstance().onCriticalRenderEventStart();
        onPreCreate();

        super.onCreate(savedInstanceState);

        if (!isFinishing()) {
            onCreate(savedInstanceState, true);
        }

        AppStartup.getInstance().onCriticalRenderEventEnd();
        Tracer.getInstance().end(Log.tag(getClass()) + "#onCreate()");
    }

    protected void onPreCreate() {}
    protected void onCreate(Bundle savedInstanceState, boolean ready) {}

    protected <T extends Fragment> T initFragment(@IdRes int target,
                                                  @NonNull T fragment)
    {
        return initFragment(target, fragment, null);
    }

    protected <T extends Fragment> T initFragment(@IdRes int target,
                                                  @NonNull T fragment,
                                                  @Nullable Locale locale)
    {
        return initFragment(target, fragment, locale, null);
    }

    protected <T extends Fragment> T initFragment(@IdRes int target,
                                                  @NonNull T fragment,
                                                  @Nullable Locale locale,
                                                  @Nullable Bundle extras)
    {
        Bundle args = new Bundle();
        args.putSerializable(LOCALE_EXTRA, locale);

        if (extras != null) {
            args.putAll(extras);
        }

        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(target, fragment)
                .commitAllowingStateLoss();
        return fragment;
    }

    /**
     * Puts an extra in {@code intent} so that {@code nextIntent} will be shown after it.
     */
    public static @NonNull
    Intent chainIntent(@NonNull Intent intent, @NonNull Intent nextIntent) {
        intent.putExtra(NEXT_INTENT_EXTRA, nextIntent);
        return intent;
    }

}

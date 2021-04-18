package com.plexus.posts.activity.comment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.plexus.R;
import com.plexus.components.emoji.EmojiImageView;
import com.plexus.posts.reactions.ReactionsViewModel;
import com.plexus.utils.ThemeUtil;
import com.plexus.utils.ViewUtil;

import java.util.Objects;

public class CommentBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private static final String ARGS_POST_ID = "comments.args.post.id";

    private String postid;

    private Callback callback;

    public static DialogFragment create(String postid) {
        Bundle args     = new Bundle();
        DialogFragment fragment = new CommentBottomSheetDialogFragment();

        args.putString(ARGS_POST_ID, postid);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        /*if (ThemeUtil.isDarkTheme(requireContext())) {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Signal_BottomSheetDialog_Fixed_ReactWithAny);
        } else {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Signal_Light_BottomSheetDialog_Fixed_ReactWithAny);
        }*/

        super.onCreate(savedInstanceState);
    }

    @Override
    public @Nullable
    View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reactions_bottom_sheet_dialog_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            FrameLayout container = requireDialog().findViewById(R.id.container);
            LayoutInflater layoutInflater = LayoutInflater.from(requireContext());
            View statusBarShader = layoutInflater.inflate(R.layout.react_with_any_emoji_status_fade, container, false);
            TabLayout emojiTabs = (TabLayout) layoutInflater.inflate(R.layout.reactions_bottom_sheet_dialog_fragment_tabs, container, false);

            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewUtil.getStatusBarHeight(container));

            statusBarShader.setLayoutParams(params);

            container.addView(statusBarShader, 0);
            container.addView(emojiTabs);

            ViewCompat.setOnApplyWindowInsetsListener(container, (v, insets) -> insets.consumeSystemWindowInsets());
        }

        setUpViewModel();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        postid          = requireArguments().getString(ARGS_POST_ID);


        LoaderManager.getInstance(requireActivity()).initLoader(Integer.parseInt(postid), null, null);
    }

    private void setUpViewModel() {
        /*CommentsViewModel.Factory factory = new CommentsViewModel.Factory(reactionsLoader);

        viewModel = ViewModelProviders.of(this, factory).get(ReactionsViewModel.class);

        viewModel.getEmojiCounts().observe(getViewLifecycleOwner(), emojiCounts -> {
            if (emojiCounts.size() <= 1) dismiss();

            recipientsAdapter.submitList(emojiCounts);
        });*/
    }

    @Override
    public void onDestroyView() {
       /* LoaderManager.getInstance(requireActivity()).destroyLoader((int) postid);*/
        super.onDestroyView();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        callback.onCommentsDialogDismissed();
    }

    public interface Callback {
        void onCommentsDialogDismissed();
    }

}

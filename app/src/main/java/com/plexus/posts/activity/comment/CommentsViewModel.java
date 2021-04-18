package com.plexus.posts.activity.comment;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.plexus.model.posts.Comment;

import java.util.List;

public class CommentsViewModel extends ViewModel {

    private final Repository repository;

    public CommentsViewModel(Repository repository) {
        this.repository = repository;
    }

    interface Repository {
        LiveData<List<Comment>> getComments();
    }

    static final class Factory implements ViewModelProvider.Factory {

        private final Repository repository;

        Factory(@NonNull Repository repository) {
            this.repository = repository;
        }

        @Override
        public @NonNull <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new CommentsViewModel(repository);
        }
    }

}

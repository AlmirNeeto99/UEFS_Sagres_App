package com.forcetower.uefs.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by João Paulo on 08/01/2018.
 */

public class MediatorResource<T> {
    public static final int SUCCESS = 0;
    public static final int LOADING = 1;
    public static final int ERROR = 2;

    @NonNull
    public final Integer status;
    @Nullable
    public final T data;
    @Nullable
    public final String message;
    @Nullable
    public final String loadMessage;

    public MediatorResource(@NonNull Integer status, @Nullable T data, @Nullable String message, @Nullable String loadMessage) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.loadMessage = loadMessage;
    }

    public static <T> MediatorResource<T> success(@NonNull T data) {
        return new MediatorResource<>(SUCCESS, data, null, null);
    }

    public static <T> MediatorResource<T> error(String msg, @Nullable T data) {
        return new MediatorResource<>(ERROR, data, msg, null);
    }

    public static <T> MediatorResource<T> loading(@Nullable T data, @Nullable String loadMessage) {
        return new MediatorResource<>(LOADING, data, null, loadMessage);
    }

    public static <T> MediatorResource<T> loading(@Nullable T data) {
        return loading(data, null);
    }

}

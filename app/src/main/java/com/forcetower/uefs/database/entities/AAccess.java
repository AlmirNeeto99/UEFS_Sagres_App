package com.forcetower.uefs.database.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Entity
public class AAccess {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    @NonNull
    private String username;
    @NonNull
    private String password;

    public AAccess(@NonNull String username, @NonNull String password) {
        this.username = username;
        this.password = password;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AAccess) {
            AAccess o = (AAccess) obj;
            return o.username.equalsIgnoreCase(username);
        }
        return false;
    }
}

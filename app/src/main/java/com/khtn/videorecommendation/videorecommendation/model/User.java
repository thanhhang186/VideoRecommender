package com.khtn.videorecommendation.videorecommendation.model;

public class User {
        private String id;
        private String uid;
        private String email;

        public User() {
        }

    public User(String id, String uid, String email) {
        this.id = id;
        this.uid = uid;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

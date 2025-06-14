package com.dish.app;


public class Post {
    public String title;
    public String username;
    public String time;
    public String instructions;
    public long timestamp;

    public Post() {}

    public Post(String title, String username, String time, String instructions, long timestamp) {
        this.title = title;
        this.username = username;
        this.time = time;
        this.instructions = instructions;
        this.timestamp = timestamp;
    }
}
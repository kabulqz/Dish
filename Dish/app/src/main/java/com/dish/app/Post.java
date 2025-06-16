package com.dish.app;


public class Post {
    public String title;
    public String username;
    public String time;
    public String instructions;
    public long timestamp;
    public String imageBase64;

    public Post() {}

    public Post(String title, String username, String time, String instructions, long timestamp, String imageBase64) {
        this.title = title;
        this.username = username;
        this.time = time;
        this.instructions = instructions;
        this.timestamp = timestamp;
        this.imageBase64 = imageBase64;
    }
}
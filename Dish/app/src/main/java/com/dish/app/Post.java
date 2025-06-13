package com.dish.app;


public class Post {
    public String title;
    public String username;
    public String time;
    public String instructions;

    public Post() {}

    public Post(String title, String username, String time, String instructions) {
        this.title = title;
        this.username = username;
        this.time = time;
        this.instructions = instructions;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public String getTime() {
        return time;
    }

    public String getInstructions() {
        return instructions;
    }
}
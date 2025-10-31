package com.example.deimos_events;

public class Result {
    private Boolean cond;
    private String message;

    public Result(Boolean cond, String message){
        this.cond = cond;
        this.message = message;
    }
    public Boolean getCond() {
        return cond;
    }
    public String getMessage() {
        return message;
    }
}

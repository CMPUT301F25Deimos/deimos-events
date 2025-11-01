package com.example.deimos_events;

import java.util.ArrayList;
import java.util.List;

public class Result {
    private Boolean cond;
    private String message;

    private final List<ResultListener> listeners = new ArrayList<>();

    public Result(Boolean cond, String message){
        this.cond = cond;
        this.message = message;
    }

    public void addListener(ResultListener rl){
        listeners.add(rl);
    }
    public void removeListener(ResultListener rl){
        listeners.remove(rl);
    }

    public void set(Boolean cond, String message){
        this.cond = cond;
        this.message = message;
        notifyListeners();
    }

    public void notifyListeners() {
        for (ResultListener rl : listeners) {
            rl.onResultChanged(this);
        }
    }


    public Boolean isSuccess(){
        return (cond == Boolean.TRUE);
    }


    public Boolean getCond() {
        return cond;
    }
    public String getMessage() {
        return message;
    }
}

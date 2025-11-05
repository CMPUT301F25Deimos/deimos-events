package com.example.deimos_events;

import java.util.function.Consumer;

public class ResultCapturer implements Consumer<Result> {
    private Result result;

    @Override
    public void accept(Result r){
        this.result = r;
    }
    public Result get(){
        return result;
    }

}

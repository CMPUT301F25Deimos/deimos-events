package com.example.deimos_events;

public class Result {
    private Boolean cond;
    private String operation;
    private String message;


    private  ResultListener resultListener;

    public void addResultListener(ResultListener resultListener){
        this.resultListener = resultListener;
    }
    public void removeResultListener(){
        this.resultListener = null;
    }

    public void set(Boolean cond, String operation, String message){
        this.cond = cond;
        this.operation = operation;
        this.message = message;
        if (resultListener != null){
            resultListener.onResultChanged(this);
        }
    }

    public void clear(){
        this.cond = null;
        this.operation = null;
        this.message = null;
    }

    public Boolean isSuccess(){
        return (cond == Boolean.TRUE);
    }


    public Boolean getCond() {
        return cond;
    }

    public String getOperation() {
        return operation;
    }

    public String getMessage() {
        return message;
    }
}

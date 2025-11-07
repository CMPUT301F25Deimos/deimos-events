package com.example.deimos_events;

public class Result {
    private Boolean cond;
    private String operation;
    private String message;




    public Result(Boolean cond, String operation, String message){
        this.cond = cond;
        this.operation = operation;
        this.message = message;
    }



    public void set(Boolean cond, String operation, String message){
        this.cond = cond;
        this.operation = operation;
        this.message = message;
    }

    public void clear(){
        this.cond = null;
        this.operation = null;
        this.message = null;
    }

    public Boolean isSuccess(){
        return (cond == Boolean.TRUE);
    }

    public Boolean isNull() {
        return cond == null;
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

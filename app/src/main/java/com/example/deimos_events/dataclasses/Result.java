package com.example.deimos_events.dataclasses;
/**
 * Represents the result of an operation, including whether it succeeded,
 * which operation was performed, and an associated message.
 */
public class Result {
    /** Indicates success or failure of the operation. */
    private Boolean cond;
    /** The name or type of the operation performed. */
    private String operation;
    /** Message describing the result. */
    private String message;
    /**
     * Creates a new result object.
     * @param cond whether the operation succeeded
     * @param operation the name of the operation
     * @param message the message associated with the result
     */
    public Result(Boolean cond, String operation, String message){
        this.cond = cond;
        this.operation = operation;
        this.message = message;
    }
    /**
     * Updates the result values.
     * @param cond whether the operation succeeded
     * @param operation the name of the operation
     * @param message the associated message
     */
    public void set(Boolean cond, String operation, String message){
        this.cond = cond;
        this.operation = operation;
        this.message = message;
    }
    /**
     * Clears all stored result values.
     */
    public void clear(){
        this.cond = null;
        this.operation = null;
        this.message = null;
    }
    /**
     * Returns whether the result represents success.
     * @return true if successful, false otherwise
     */
    public Boolean isSuccess(){
        return (cond == Boolean.TRUE);
    }
    /**
     * Returns whether the result is empty (null condition).
     * @return true if condition is null
     */
    public Boolean isNull() {
        return cond == null;
    }
    /**
     * @return the result condition
     */
    public Boolean getCond() {
        return cond;
    }
    /**
     * @return the operation name
     */
    public String getOperation() {
        return operation;
    }
    /**
     * @return the result message
     */
    public String getMessage() {
        return message;
    }
}

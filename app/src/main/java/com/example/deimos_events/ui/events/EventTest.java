package com.example.deimos_events.ui.events;

/**
 * Just a class to test my listviews [can be removed]
 * description: event description
 * image: event image
 * waitingList: whether the entrant is part of the waiting list or not
 * waitingToAccept: for the notifications---sees what the entrant's answer is (-1: still on waiting list, 0: unanswered, 1: accept, 2: decline)
 * ownEvent: whether the person currently logged in owns the event or not
 */
public class EventTest {
    public String description;
    public int image;
    public boolean waitingList;
    
    public int waitingToAccept;
    public boolean ownEvent;
    
    public EventTest(String description, int image, boolean waitingList, int waitingToAccept, boolean ownEvent) {
        this.description = description;
        this.image = image;
        this.waitingList = waitingList;
        this.waitingToAccept = waitingToAccept;
        this.ownEvent = ownEvent;
    }
    
    public boolean getWaitingList() {
        return waitingList;
    }
    public void setWaitingList(boolean waiting) {
        this.waitingList = waiting;
    }
    
    public int getWaitingToAccept() {
        return waitingToAccept;
    }
    
    public void setWaitingToAccept(int answer) {
        this.waitingToAccept = answer;
    }
}

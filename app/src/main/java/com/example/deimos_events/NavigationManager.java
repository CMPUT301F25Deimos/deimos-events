package com.example.deimos_events;

import android.app.Activity;
import android.content.Intent;

import androidx.navigation.NavController;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class NavigationManager {
    private final SessionManager sessionManager;
    public NavigationManager(SessionManager sessionManager){
        this.sessionManager = sessionManager;
    }
    public void setActivity(Activity a){
        sessionManager.getSession().setActivity(a);
    }
    public Activity getActivity(){
        return sessionManager.getSession().getActivity();
    }
    public void goTo(Class<?> targetDestination, navFlags flag){

        Activity current = getActivity();
        if (current == null){
            return;
        }
        Intent intent = new Intent(current, targetDestination);

        switch (flag){
            case RETURN_TO_TASK:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                // Tries to find target activity in task stack
                // if found will remove everything above it on the task stack
                // if not found will place the activity on the top of the stack
                current.startActivity(intent);
                break;
            case RESET_TO_NEW_ROOT:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                // clear task stack and sets the target as the root of the new task stack
                current.startActivity(intent);
                current.finish();
                break;
            case NO_FLAGS:
            default:
                current.startActivity(intent);
                // normal navigation, just adds the activity to the task stack
                break;
        }
    }
    public enum navFlags{
        RETURN_TO_TASK,
        RESET_TO_NEW_ROOT,
        NO_FLAGS
    }
}

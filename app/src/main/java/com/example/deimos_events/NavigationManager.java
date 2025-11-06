package com.example.deimos_events;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;


public class NavigationManager {
    private final UserInterfaceManager userInterfaceManager;
    public NavigationManager(UserInterfaceManager userInterfaceManager){
        this.userInterfaceManager = userInterfaceManager;
    }



    public void goTo(Class<?> targetDestination, boolean clearBackStack){
        Activity current = userInterfaceManager.getActivity();
        if (current == null){
            return;
        }
        Intent intent = new Intent(current, targetDestination);
        if (clearBackStack){
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            current.finish();
        }
        current.startActivity(intent);
    }

    public void goBack(){
        Activity current = userInterfaceManager.getActivity();
        if (current != null) current.finish();
    }
}

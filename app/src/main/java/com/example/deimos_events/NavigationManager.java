package com.example.deimos_events;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class NavigationManager {
    private final UserInterfaceManager userInterfaceManager;
    public NavigationManager(UserInterfaceManager userInterfaceManager){
        this.userInterfaceManager = userInterfaceManager;
    }



    public void goTo(Class<?> targetDestination){
        Activity current = userInterfaceManager.getActivity();
        if (current != null){
            Intent intent = new Intent(current, targetDestination);
            current.startActivity(intent);
        }
    }

    public void goBack(){
        Activity current = userInterfaceManager.getActivity();
        if (current != null) current.finish();
    }
}

package com.example.deimos_events.managers;

import android.app.Activity;
import android.content.Intent;

/**
 * Controls moving between Activities within the application.
 * <p>
 * Provides methods to start Activities and clear or reset the Activity stack based on navigation flags
 *
 * <p>
 * All Navigations are done through the Session object, by manipulating its Activity attribute
 *
 */
public class NavigationManager {
    private final SessionManager sessionManager;
    public NavigationManager(SessionManager sessionManager){
        this.sessionManager = sessionManager;
    }

    /**
     * Stores the given {@link Activity} in the current {@link com.example.deimos_events.Session} instance.
     * <p>
     *
     * @param a The activity we wish to set our current activity as
     */
    public void setActivity(Activity a){
        sessionManager.getSession().setActivity(a);
    }
    /**
     * Returns the activity currently stored in the {@link com.example.deimos_events.Session}.
     * </p>
     * @return Activity
     */
    public Activity getActivity(){
        return sessionManager.getSession().getActivity();
    }

    /**
     * Navigates from the current activity to a new activity
     * @param targetDestination the activity we wish to start
     * @param flag the navigation mode we wish to use, either clears or reuses the existing activity stack
     * <p>
     * Behavior:
     * <ul>
     *             <li> If {@code RETURN_TO_TASK} is used, the existing activity stack is searched for
     *             the current activity. if it is found, all activities above it are removed. Else, creates a new
     *             activity at the top of the stack</li>
     *             <li>If {@code RESET_TO_NEW_ROOT} is used, the entire activity stack is cleared and
     *             the new activity is set as the new root</li>
     *             <li>if {@code NO_FLAGS} is used the new activity is placed at the top of the activity stack</li>
     * </ul>
     */
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

    /**
     * Flags controlling how we navigation is connected to the activity stack.
     * @see NavigationManager#goTo(Class, navFlags)
     */
    public enum navFlags{
        RETURN_TO_TASK,
        RESET_TO_NEW_ROOT,
        NO_FLAGS
    }
}

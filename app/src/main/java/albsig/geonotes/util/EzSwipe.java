package albsig.geonotes.util;


import android.view.MotionEvent;

public class EzSwipe {

    public static final int NO_EVENT_REGISTERED = 0;
    public static final int SWIPE_LEFT_TO_RIGHT = 1;
    public static final int SWIPE_RIGHT_TO_LEFT = 2;

    private static int MIN_DISTANCE = 200; //min pixels needed to register swipe;

    private static float x1;
    private static float x2;


    public EzSwipe() {
    }

    public static int getAction(MotionEvent event) {

        int result = 0;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x1 - x2;
                if (deltaX > MIN_DISTANCE) {
                    result = SWIPE_RIGHT_TO_LEFT;
                }
                break;
        }

        return result;
    }
}

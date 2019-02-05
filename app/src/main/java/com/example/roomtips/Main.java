package com.example.roomtips;

import android.content.Intent;
import android.graphics.Camera;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class Main extends AppCompatActivity {

    private static final String DEBUG_TAG_1 = "Touch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*  Detect touch events on the main activity and update accordingly.
        On tap: Go to full screen camera activity.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = event.getActionMasked();

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Log.d(DEBUG_TAG_1,"Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE) :
                Log.d(DEBUG_TAG_1,"Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP) :
                Log.d(DEBUG_TAG_1,"Action was UP");
                startActivity(new Intent(this, CameraActivity.class));
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                Log.d(DEBUG_TAG_1,"Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                Log.d(DEBUG_TAG_1,"Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }
}

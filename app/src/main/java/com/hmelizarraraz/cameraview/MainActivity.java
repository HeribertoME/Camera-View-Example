package com.hmelizarraraz.cameraview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;

public class MainActivity extends AppCompatActivity {

    CameraView camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        camera = findViewById(R.id.camera);
        camera.setLifecycleOwner(this);
        camera.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
    }
}

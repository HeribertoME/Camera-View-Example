package com.hmelizarraraz.cameraview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Size;

public class CameraControls extends LinearLayout {

    private int cameraViewId = -1;
    private CameraView cameraView;

    private int coverViewId = -1;
    private View coverView;

    ImageView facingButton;
    ImageView flashButton;
    ImageView captureImageButton;

    private Size captureNativeSize;
    private long captureStartTime;


    public CameraControls(Context context) {
        this(context, null);
    }

    public CameraControls(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraControls(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.camera_controls, this);

        facingButton = findViewById(R.id.facingButton);
        flashButton = findViewById(R.id.flashButton);
        captureImageButton = findViewById(R.id.captureImageButton);

        facingButton.setOnTouchListener(onTouchFacing);
        flashButton.setOnTouchListener(onTouchFlash);
        captureImageButton.setOnTouchListener(onTouchCaptureImage);


        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.CameraControls,
                    0, 0);

            try {
                cameraViewId = a.getResourceId(R.styleable.CameraControls_camera, -1);
                coverViewId = a.getResourceId(R.styleable.CameraControls_cover, -1);
            } finally {
                a.recycle();
            }
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (cameraViewId != -1) {
            View view = getRootView().findViewById(cameraViewId);
            if (view instanceof CameraView) {
                cameraView = (CameraView) view;
                cameraView.addCameraListener(new CameraListener() {
                    @Override
                    public void onPictureTaken(byte[] jpeg) {
                        imageCaptured(jpeg);
                    }
                });
                setFacingImageBasedOnCamera();
            }
        }

        if (coverViewId != -1) {
            View view = getRootView().findViewById(coverViewId);
            if (view != null) {
                coverView = view;
                coverView.setVisibility(GONE);
            }
        }
    }

    private void setFacingImageBasedOnCamera() {
        if (cameraView.getFacing() == Facing.FRONT) {
            facingButton.setImageResource(R.drawable.ic_facing_back);
        } else {
            facingButton.setImageResource(R.drawable.ic_facing_front);
        }
    }

    public void imageCaptured(byte[] jpeg) {
        long callbackTime = System.currentTimeMillis();

        if (captureStartTime == 0) captureStartTime = callbackTime -300;
        if (captureNativeSize == null) captureNativeSize = cameraView.getPictureSize();

        Log.d("FOTO", "image Captured");

        PreviewActivity.setImage(jpeg);
        Intent intent = new Intent(getContext(), PreviewActivity.class);
        getContext().startActivity(intent);

        captureStartTime = 0;
        captureNativeSize = null;
    }

    private View.OnTouchListener onTouchCaptureImage = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    handleViewTouchFeedback(view, motionEvent);

                    captureStartTime = System.currentTimeMillis();
                    captureNativeSize = cameraView.getPictureSize();
                    cameraView.capturePicture();
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    handleViewTouchFeedback(view, motionEvent);
                    break;
                }
            }
            return true;
        }
    };


    private View.OnTouchListener onTouchFacing = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_UP: {
                    coverView.setAlpha(0);
                    coverView.setVisibility(VISIBLE);
                    coverView.animate()
                            .alpha(1)
                            .setStartDelay(0)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    if (cameraView.getFacing() == Facing.FRONT) {
                                        cameraView.setFacing(Facing.BACK);
                                        changeViewImageResource((ImageView) view, R.drawable.ic_facing_front);
                                    } else {
                                        cameraView.setFacing(Facing.FRONT);
                                        changeViewImageResource((ImageView) view, R.drawable.ic_facing_back);
                                    }

                                    coverView.animate()
                                            .alpha(0)
                                            .setStartDelay(200)
                                            .setDuration(300)
                                            .setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    super.onAnimationEnd(animation);
                                                    coverView.setVisibility(GONE);
                                                }
                                            })
                                            .start();
                                }
                            })
                            .start();
                    break;
                }
            }
            return true;
        }
    };

    private View.OnTouchListener onTouchFlash = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_UP: {
                    if (cameraView.getFlash() == Flash.OFF) {
                        cameraView.setFlash(Flash.ON);
                        changeViewImageResource((ImageView) view, R.drawable.ic_flash_on);
                    } else {
                        cameraView.setFlash(Flash.OFF);
                        changeViewImageResource((ImageView) view, R.drawable.ic_flash_off);
                    }
                    break;
                }
            }
            return true;
        }
    };

    boolean handleViewTouchFeedback(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                touchDownAnimation(view);
                return true;
            }

            case MotionEvent.ACTION_UP: {
                touchUpAnimation(view);
                return true;
            }

            default: {
                return true;
            }
        }
    }

    void touchDownAnimation(View view) {
        view.animate()
                .scaleX(0.88f)
                .scaleY(0.88f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    void touchUpAnimation(View view) {
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    void changeViewImageResource(final ImageView imageView, @DrawableRes final int resId) {
        imageView.setRotation(0);
        imageView.animate()
                .rotationBy(360)
                .setDuration(400)
                .setInterpolator(new OvershootInterpolator())
                .start();

        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(resId);
            }
        }, 120);
    }
}

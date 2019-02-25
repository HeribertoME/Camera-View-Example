package com.hmelizarraraz.cameraview;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.otaliastudios.cameraview.CameraUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PreviewActivity extends AppCompatActivity {

    private static WeakReference<byte[]> image;

    public static void setImage(@Nullable byte[] im) {
        image = im != null ? new WeakReference<>(im) : null;
    }

    private Bitmap finalBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        final ImageView imageView = findViewById(R.id.image);
        final FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        final FloatingActionButton fabCancel = findViewById(R.id.fab_cancel);

        byte[] b = image == null ? null : image.get();

        if (b == null) {
            finish();
            return;
        }

        CameraUtils.decodeBitmap(b, 800, 1200, new CameraUtils.BitmapCallback() {
            @Override
            public void onBitmapReady(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
                finalBitmap = bitmap;
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalBitmap != null) {
                    saveImageToFile(finalBitmap);
                } else {
                    Snackbar.make(v, "No se puede salvar la imagen", Snackbar.LENGTH_LONG)
                            .setAction("Reintentar", null).show();
                }
            }
        });

        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreviewActivity.this.finish();
            }
        });

    }

    private void saveImageToFile(Bitmap finalBitmap) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "FAY_" + timeStamp + "_" + ".png";

        File directory = getDir("evidences", MODE_PRIVATE);
        File filename = new File(directory, imageFileName);

        try (FileOutputStream out = new FileOutputStream(filename)) {
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        } finally {
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

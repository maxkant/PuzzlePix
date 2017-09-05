package com.example.maxkant.puzzlepix;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.theartofdev.edmodo.cropper.CropImage;

public class MainActivity extends AppCompatActivity {

    PuzzleView puzzleView;
    Uri mCropImageUri;
    Uri puzzleUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // The main layout of the puzzle screen
        final RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.activity_main);

        // Creating the layout to hold the puzzle pieces
        puzzleView = new PuzzleView(this);
        // The number of pieces in x and y
        final int gridSize = 4;
        mainLayout.addView(puzzleView);

        Button startButton = (Button) findViewById(R.id.start_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int width = mainLayout.getWidth();

                puzzleView.setGridSize(gridSize);
                // Checking to see if the dimensions of the puzzle view are a multiple of the grid size,
                // otherwise rounding errors can cause breaks between pieces.

                // Decrease width until an even number of pieces will fit inside.
                while ((width % gridSize) != 0){ width -= 1; }

                // Set puzzle view to be correct size
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, width);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
                puzzleView.setLayoutParams(layoutParams);

                // Start pick image activity
                CropImage.startPickImageActivity(MainActivity.this);

            }
        });

    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);

            } else {
                // no permissions required or already granted, can start crop image activity
                CropImage.activity(imageUri).setAspectRatio(1, 1).setFixAspectRatio(true).start(this);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                puzzleUri = result.getUri();
                puzzleView.setPuzzleUri(puzzleUri);

            }

        }

    }

}

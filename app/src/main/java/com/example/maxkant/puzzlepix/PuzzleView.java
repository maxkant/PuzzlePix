package com.example.maxkant.puzzlepix;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.FileNotFoundException;


public class PuzzleView extends RelativeLayout {

    // Uri of the image used in the puzzle
    Uri puzzleUri;
    Bitmap puzzleBitmap;
    Activity parentActivity;
    // Number of pieces in x and y
    int nGrid;
    int vWidth;
    int vHeight;
    ImageDecoder imageDecoder;
    ImageView[][] pieces;
    int startingBlankX;
    int startingBlankY;

    public PuzzleView(Context context) {
        super(context);

        parentActivity = (Activity) context;
        imageDecoder = new ImageDecoder(parentActivity);

    }


    public void setGridSize(int gS){

        nGrid = gS;
        pieces = new ImageView[nGrid][nGrid];

    }

    public enum moveDirection{

        UP,
        RIGHT,
        LEFT,
        DOWN,
        NONE

    }

    public void setPuzzleUri(Uri uri){

        puzzleUri = uri;
        vWidth = getWidth();
        vHeight = getHeight();


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        try {
            BitmapFactory.decodeStream(parentActivity.getContentResolver().openInputStream(puzzleUri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        puzzleBitmap = imageDecoder.decode(puzzleUri, options.outWidth, options.outHeight);
        createChildren();

    }

    int pWidth;
    int pHeight;

    private void createChildren(){
        // Cuts the puzzle bitmap into pieces and sets the tile views as children

        int bWidth = puzzleBitmap.getWidth();
        int bHeight = puzzleBitmap.getHeight();

        int scaledWidth = Math.round(bWidth/nGrid);
        int scaledHeight = Math.round(bHeight/nGrid);

        pWidth = Math.round(vWidth/nGrid);
        pHeight = Math.round(vWidth/nGrid);

        for (int i = 0; i < nGrid; i++) {

            for (int j = 0; j < nGrid; j++) {

                //Number of the current piece, from the top left;
                int pN = ((j * nGrid) + i + 1);

                Bitmap map = Bitmap.createBitmap(puzzleBitmap, (i * scaledWidth), (j * scaledHeight), scaledWidth - 1, scaledHeight - 1);
                Bitmap scaleMap = Bitmap.createScaledBitmap(map, pWidth, pHeight, false);

                ImageView piece = new ImageView(parentActivity);

                piece.setImageBitmap(scaleMap);

                piece.setId(pN);

                float xCoordinate = i * pWidth;
                float yCoordinate = j * pHeight;

                piece.setX(xCoordinate);
                piece.setY(yCoordinate);

//                TextView tV = new TextView(parentActivity);
//                tV.setWidth(pWidth);
//                tV.setHeight(pHeight);
//                tV.setText("(" + String.valueOf(i) + ", " + String.valueOf(j) + ")");
//                tV.setX(xCoordinate);
//                tV.setY(yCoordinate);
//                tV.setTextSize(15);
//                tV.setGravity(Gravity.CENTER);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    tV.setElevation(1f);
//                }
//                addView(tV);


                addView(piece);


                pieces[i][j] = piece;



            }
        }

        initializePuzzle();

    }

    PuzzleTimer pTimer;

    public void initializePuzzle(){

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                float x = motionEvent.getX();
                float y = motionEvent.getY();

                int pX = (int) Math.floor(x / vWidth * nGrid);
                int pY = (int) Math.floor(y / vHeight * nGrid);


                startPuzzle(pX, pY);
                startingBlankX = pX;
                startingBlankY = pY;

                return false;

            }
        });
        pTimer = (PuzzleTimer) parentActivity.findViewById(R.id.pTimer);
        Toast startToast = Toast.makeText(parentActivity, "Tap a piece to start the puzzle!", Toast.LENGTH_LONG);
        startToast.show();

    }

    public void startPuzzle(final int pX, final int pY){

        ImageView bTest = pieces[pX][pY];
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(bTest, "alpha", 1f, 0f);
        fadeOut.setDuration(1000);
        final float sideLength = vWidth / nGrid;

        setOnTouchListener(new OnTouchListener() {@Override public boolean onTouch(View view, MotionEvent motionEvent) {return false;}});

        final Scrambler pScrambler = new Scrambler();

        pScrambler.generateMoveSequence(2032032, 100, nGrid, pX, pY);
        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                pScrambler.startScramble(pieces, sideLength, PuzzleView.this);

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        fadeOut.start();

        //pScrambler.testMove(pieces[0][0], nGrid, sideLength);



    }

    int dx;
    int dy;

    int currentBlankX;
    int currentBlankY;


    moveDirection direction;

    public moveDirection canMove(int pX, int pY){

        moveDirection dir = moveDirection.NONE;

        // Farther than one piece from the blank
        if ((Math.abs(pX - currentBlankX) > 1) || (Math.abs(pY  - currentBlankY) > 1) || (pX == currentBlankX & pY == currentBlankY)){

            //Can't move
            dir = moveDirection.NONE;

        }

        // Above or below the blank

        else if (pX == currentBlankX & (Math.abs(pY - currentBlankY) == 1)){

            // Below
            if (pY == currentBlankY + 1){

                dir = moveDirection.UP;

            }
            // Above
            else{

                dir = moveDirection.DOWN;

            }

        }

        else if (pY == currentBlankY & (Math.abs(pX - currentBlankX) == 1)){

            if (pX == currentBlankX + 1){

                dir = moveDirection.LEFT;

            }

            else {

                dir = moveDirection.RIGHT;

            }

        }

        return dir;

    }

    int duration = 50;
    public boolean unlocked = false;


    public void unlockPieces(int bX, int bY, final ImageView[][] pieceArray){

        currentBlankX = bX;
        currentBlankY = bY;
        pieces = pieceArray;
        unlocked = true;

        pTimer.startTimer();

    }

    public void solvedEvent(){

        unlocked = false;

        pTimer.pauseTimer();

        ImageView blank = pieces[startingBlankX][startingBlankY];
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(blank, "alpha", 0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.start();


    }

    ImageView piece;

    int pX = 0;
    int pY = 0;

    PointF downPT = new PointF();
    private static final int INVALID_POINTER_ID = -1;
    int action;
    private int activePointer = INVALID_POINTER_ID;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {


        // Return true to intercept touch event, to be handled by this viewGroup's onTouchEvent
        // False will allow the touch event through to the viewGroup's children

        return true;

    }

    long tapDownTime;
    long tapUpTime;
    long tapDuration;


    @Override
    public boolean onTouchEvent(MotionEvent event) {



        if (unlocked) {

            action = event.getAction();


            switch (action & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:

                    dx = 0;
                    dy = 0;

                    activePointer = event.getPointerId(0);

                    downPT.x = event.getX(activePointer);
                    downPT.y = event.getY(activePointer);
                    //float test = nGrid * event.getX() / vWidth;
                    pX = (int) Math.floor(nGrid * event.getX() / vWidth);
                    pY = (int) Math.floor(nGrid * event.getY() / vHeight);

                    direction = canMove(pX, pY);

                    piece = pieces[pX][pY];

                    tapDownTime = System.currentTimeMillis();


                    break;

                case MotionEvent.ACTION_MOVE:

                    final int activeIndex = event.findPointerIndex(activePointer);
                    PointF move = new PointF(event.getX(activeIndex) - downPT.x, event.getY(activeIndex) - downPT.y);

                    float xDisplacement = piece.getX() + move.x;
                    float yDisplacement = piece.getY() + move.y;

                    switch (direction) {


                        case NONE: {

                            break;

                        }

                        case DOWN: {

                            dy -= piece.getY();

                            if (move.y > 0) {
                                piece.setY(Math.min(yDisplacement, (pY + 1) * pHeight));
                            }

                            if (move.y < 0) {
                                piece.setY(Math.max(yDisplacement, (pY * pHeight)));
                            }

                            dy += piece.getY();

                            break;

                        }

                        case UP: {

                            dy -= piece.getY();

                            if (move.y > 0) {
                                piece.setY(Math.min(yDisplacement, pY * pHeight));
                            }

                            if (move.y < 0) {
                                piece.setY(Math.max(yDisplacement, ((pY - 1) * pHeight)));
                            }

                            dy += piece.getY();

                            break;

                        }

                        case RIGHT: {

                            dx -= piece.getX();

                            if (move.x > 0) {
                                piece.setX(Math.min(xDisplacement, (pX + 1) * pWidth));
                            }

                            if (move.x < 0) {
                                piece.setX(Math.max(xDisplacement, (pX * pWidth)));
                            }

                            dx += piece.getX();

                            break;

                        }

                        case LEFT: {

                            dx -= piece.getX();

                            if (move.x > 0) {
                                piece.setX(Math.min(xDisplacement, (pX * pWidth)));
                            }

                            if (move.x < 0) {
                                piece.setX(Math.max(xDisplacement, (pX - 1) * pWidth));
                            }

                            dx += piece.getX();

                            break;

                        }

                    }

                    downPT.x = event.getX(activeIndex);
                    downPT.y = event.getY(activeIndex);

                    break;

                case MotionEvent.ACTION_UP:

                    activePointer = INVALID_POINTER_ID;

                    tapUpTime = System.currentTimeMillis();
                    tapDuration = tapUpTime - tapDownTime;


                        switch (direction) {

                            // Above blank
                            case DOWN: {

                                if (Math.abs(dy) >= pHeight / 4) {

                                    ObjectAnimator translate = ObjectAnimator.ofFloat(piece, "translationY", ((pY + 1) * pHeight));
                                    translate.setDuration(Math.abs(Math.round(duration * (1 - Math.abs(dy) / pHeight))));
                                    translate.start();

                                    swapPieces(currentBlankX, currentBlankY, pX, pY);

                                } else {

                                    ObjectAnimator translate = ObjectAnimator.ofFloat(piece, "translationY", pY * pHeight);
                                    translate.setDuration(Math.abs(Math.round(duration * (Math.abs(dy) / pHeight))));
                                    translate.start();

                                }

                                break;

                            }

                            // Below blank

                            case UP: {

                                if (Math.abs(dy) >= pHeight / 4) {

                                    ObjectAnimator translate = ObjectAnimator.ofFloat(piece, "translationY", (pY - 1) * pHeight);
                                    translate.setDuration(Math.abs(Math.round(duration * (1 - Math.abs(dy) / pHeight))));
                                    translate.start();

                                    swapPieces(currentBlankX, currentBlankY, pX, pY);


                                } else {

                                    ObjectAnimator translate = ObjectAnimator.ofFloat(piece, "translationY", pY * pHeight);
                                    translate.setDuration(Math.abs(Math.round(duration * (Math.abs(dy) / pHeight))));
                                    translate.start();

                                }

                                break;

                            }


                            // Left of blank
                            case RIGHT: {

                                if (Math.abs(dx) >= pWidth / 4) {

                                    ObjectAnimator translate = ObjectAnimator.ofFloat(piece, "translationX", (pX + 1) * pWidth);
                                    translate.setDuration(Math.abs(Math.round(duration * (1 - Math.abs(dx) / pWidth))));
                                    translate.start();

                                    swapPieces(currentBlankX, currentBlankY, pX, pY);


                                } else {

                                    ObjectAnimator translate = ObjectAnimator.ofFloat(piece, "translationX", pX * pWidth);
                                    translate.setDuration(Math.abs(Math.round(duration * (Math.abs(dx) / pWidth))));
                                    translate.start();

                                }

                                break;

                            }

                            // Right of blank
                            case LEFT: {

                                if (Math.abs(dx) >= pWidth / 4) {

                                    ObjectAnimator translate = ObjectAnimator.ofFloat(piece, "translationX", (pX - 1) * pWidth);
                                    translate.setDuration(Math.abs(Math.round(duration * (1 - Math.abs(dx) / pWidth))));
                                    translate.start();

                                    swapPieces(currentBlankX, currentBlankY, pX, pY);


                                } else {

                                    ObjectAnimator translate = ObjectAnimator.ofFloat(piece, "translationX", pX * pWidth);
                                    translate.setDuration(Math.abs(Math.round(duration * (Math.abs(dx) / pWidth))));
                                    translate.start();

                                }

                                break;

                            }


                        }

                        break;


                case MotionEvent.ACTION_POINTER_UP: {

                    final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerID = event.getPointerId(pointerIndex);

                    if (pointerID == activePointer) {

                        final int newPointerIndex = (pointerIndex == 0 ? 1 : 0);

                        downPT.x = event.getX(newPointerIndex);
                        downPT.y = event.getY(newPointerIndex);

                        activePointer = event.getPointerId(newPointerIndex);

                    }

                    break;

                }

                case MotionEvent.ACTION_CANCEL: {

                    activePointer = INVALID_POINTER_ID;

                    break;
                }

                default:

                    break;

            }




        }

        return true;
    }

    private void swapPieces(int bX, int bY, int pX, int pY){

        ImageView blank = pieces[bX][bY];
        ImageView piece = pieces[pX][pY];

        pieces[pX][pY] = blank;
        pieces[bX][bY] = piece;

        currentBlankX = pX;
        currentBlankY = pY;

        int counter = 0;

        for (int i = 0; i < (nGrid * nGrid); i++){

            int x = (i % nGrid);
            int y = (int) Math.floor(i/nGrid);

            ImageView currentPiece = pieces[x][y];

            int id = currentPiece.getId();

            if (id == (i + 1)){

                counter += 1;

            }


        }

        if (counter == (nGrid * nGrid)){ solvedEvent();}

    }

}

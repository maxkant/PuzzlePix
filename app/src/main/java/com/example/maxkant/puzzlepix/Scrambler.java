package com.example.maxkant.puzzlepix;

/**
 * Created by maxkant on 2/5/17.
 */
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.maxkant.puzzlepix.PuzzleView.moveDirection;

import java.util.Random;

class Scrambler {

    private moveDirection[] sequence;
    private int sequenceSize;
    private int startingBlankX;
    private int startingBlankY;
    private int endingBlankX;
    private int endingBlankY;
    private float sideLength;
    private ImageView[][] pieceArray;
    private  PuzzleView puzzleView;

    void generateMoveSequence(long seed, int sSize, int gridSize, int blankX, int blankY){

        Random random = new Random(seed);
        sequenceSize = sSize;
        sequence = new moveDirection[sequenceSize];
        int rand = 0;
        int lastRand = 0;
        startingBlankX = blankX;
        startingBlankY = blankY;

        for (int i = 0; i < sequenceSize; i++ ){

            boolean stop = false;

            while(!stop) {
                rand = random.nextInt(4);

                //don't go back the same way you just came
                if(lastRand!= (rand + 2) % 4) {
                    switch (rand) {
                        case 0: {

                            stop=(blankY < (gridSize - 1));

                            if (stop) {

                                blankY += 1;
                                sequence[i] = moveDirection.DOWN;

                            }

                            break;

                        }
                        case 1: {

                            stop = (blankX < (gridSize - 1));

                            if (stop) {

                                blankX += 1;
                                sequence[i] = moveDirection.RIGHT;

                            }

                            break;

                        }
                        case 2: {

                            stop = (blankY > 0);

                            if (stop) {

                                blankY -= 1;
                                sequence[i] = moveDirection.UP;

                            }

                            break;

                        }
                        case 3: {

                            stop = (blankX > 0);

                            if (stop) {

                                blankX -= 1;
                                sequence[i] = moveDirection.LEFT;

                            }

                            break;

                        }
                    }
                }
            }

            lastRand = rand;

        }

    }

    void startScramble(ImageView[][] pieces, float sLength, PuzzleView pV) {

        pieceArray = pieces;
        sideLength = sLength;
        translatePiece(0, startingBlankX, startingBlankY);
        puzzleView = pV;

    }

    private void translatePiece(final int index, int blankX, int blankY){


        ImageView piece;
        String animationString = null;
        float startCoord = 0;
        float endCoord = 0;
        int nextBX = 0;
        int nextBY = 0;

        switch (sequence[index]){

            case UP:{

                animationString = "translationY";
                startCoord = (blankY - 1) * sideLength;
                endCoord = (blankY) * sideLength;
                nextBX = blankX;
                nextBY = blankY - 1;

                break;

            }

            case RIGHT:{

                animationString = "translationX";
                startCoord = (blankX + 1) * sideLength;
                endCoord = (blankX) * sideLength;
                nextBX = blankX + 1;
                nextBY = blankY;

                break;

            }

            case DOWN:{

                animationString = "translationY";
                startCoord = (blankY + 1) * sideLength;
                endCoord = (blankY) * sideLength;
                nextBX = blankX;
                nextBY = blankY + 1;

                break;

            }

            case LEFT:{

                animationString = "translationX";
                startCoord = (blankX - 1) * sideLength;
                endCoord = (blankX) * sideLength;
                nextBX = blankX - 1;
                nextBY = blankY;

                break;

            }

        }

        final int pX = nextBX;
        final int pY = nextBY;
        final int bX = blankX;
        final int bY = blankY;

        piece = pieceArray[nextBX][nextBY];

        if (index < sequenceSize - 1) {
            ObjectAnimator translate = ObjectAnimator.ofFloat(piece, animationString, startCoord, endCoord);
            translate.setDuration(0);
            translate.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                    swapPieces(bX, bY, pX, pY);

                }

                @Override
                public void onAnimationEnd(Animator animator) {

                    translatePiece(index + 1, pX, pY);

                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }

            });

            translate.start();

        }

        else{

            endingBlankX = bX;
            endingBlankY = bY;
            doneScrambling();

        }
    }

    private void swapPieces(int bX, int bY, int pX, int pY){

        ImageView blank = pieceArray[bX][bY];
        ImageView piece = pieceArray[pX][pY];

        pieceArray[pX][pY] = blank;
        pieceArray[bX][bY] = piece;

    }

    private void doneScrambling(){

        //Tell the puzzleView it can unlock the pieces.
        puzzleView.unlockPieces(endingBlankX, endingBlankY, pieceArray);


    }

}

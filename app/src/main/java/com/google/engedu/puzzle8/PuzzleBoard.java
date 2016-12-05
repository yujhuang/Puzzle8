package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;
    private int steps;
    private PuzzleBoard previousBoard =null;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        bitmap = Bitmap.createScaledBitmap(bitmap,parentWidth,parentWidth,false);
        tiles = new ArrayList<>();
        int counter = 1;
        int width = parentWidth/NUM_TILES;
        int x = 0;
        int y = 0;
        for(int i =0;i< NUM_TILES;i++) {
            for(int j = 0; j < NUM_TILES;j++) {
                Bitmap tile = Bitmap.createBitmap(bitmap,x,y,width,width);
                if(counter == 9) {
                    tiles.add(null);
                }else {
                    tiles.add(new PuzzleTile(tile,counter));
                }
                x += width;
                counter ++;
            }
            y += width;
            x = 0;
        }
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        steps = otherBoard.steps + 1;
        previousBoard = otherBoard;
    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    public ArrayList<PuzzleBoard> neighbours() {
        int emptyIndex = 0;
        int bound = NUM_TILES * NUM_TILES;
        ArrayList<PuzzleBoard> neighbours = new ArrayList<PuzzleBoard>();
        for(int i = 0; i< tiles.size();i++) {
            if(tiles.get(i) == null) {
                emptyIndex = i;
                break;
            }
        }
        if(emptyIndex +1< bound) {
            PuzzleBoard right = new PuzzleBoard(this);
            right.swapTiles(emptyIndex + 1, emptyIndex);
            neighbours.add(right);
        }
        if(emptyIndex + 3 < bound) {
            PuzzleBoard down = new PuzzleBoard(this);
            down.swapTiles(emptyIndex + 3, emptyIndex);
            neighbours.add(down);
        }
        if(emptyIndex -1 >= 0) {
            PuzzleBoard left = new PuzzleBoard(this);
            left.swapTiles(emptyIndex -1,emptyIndex);
            neighbours.add(left);
        }
        if(emptyIndex - 3 >= 0) {
            PuzzleBoard up = new PuzzleBoard(this);
            up.swapTiles(emptyIndex - 3,emptyIndex);
            neighbours.add(up);
        }
        return neighbours;
    }

    public int priority() {
        int manhattan = 0,position = 0;
        for(int i = 0; i < NUM_TILES;i++) {
            for(int j = 0; j < NUM_TILES; j++) {
                if(tiles.get(i + NUM_TILES*j) != null) {
                    position = tiles.get(i + NUM_TILES*j).getNumber();
                    manhattan += Math.abs((position-1) / NUM_TILES - i);
                    manhattan += Math.abs((position-1) % NUM_TILES - j);
                }
            }
        }
        return manhattan;

    }
    public PuzzleBoard getPreviousBoard() {
        return this.previousBoard;
    }

}

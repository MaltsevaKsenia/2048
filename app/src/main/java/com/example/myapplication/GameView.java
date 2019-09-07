package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;

public class GameView extends GridLayout {

    private static List<Point> emptyPoints = new ArrayList<>();
    public int[][] gameNumbers = new int[4][4];
    public int score;
    public boolean hasTouched = false;
    public static Map<Point, Card> cardMap = new HashMap<>(16);

    public GameView(Context context) {
        super(context);
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initGameView();
    }

    private void initGameView() {
        setRowCount(4);
        setColumnCount(4);
        setOnTouchListener(new Listener());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        int fieldSize = (Math.min(w, h) - 10) / 4;
        addCards(fieldSize);
        startGame();

    }

    private void addCards(int size) {
        this.removeAllViews();
        Card c;
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                c = new Card(getContext());
                c.setCardNumber(0);
                addView(c, size, size);
                cardMap.put(new Point(x, y), c);
            }
        }
    }

    private static void addRandomNum() {
        emptyPoints.clear();
        cardMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getCardNumber() == 0)
                .forEach(entry -> emptyPoints.add(entry.getKey()));
        Point point = emptyPoints.remove(getNewCardPoint());
        cardMap.get(point).setCardNumber(getTwoOrFour());
    }

    private static int getNewCardPoint() {
        return (int) (Math.random() * emptyPoints.size());
    }

    private static int getTwoOrFour() {
        return Math.random() > 0.1 ? 2 : 4;
    }

    public static void startGame() {
        MainActivity.getMainActivity().clearScore();
        cardMap.values()
                .forEach(card -> card.setCardNumber(0));
        addRandomNum();
        addRandomNum();
    }

    private void swipeLeft() {
        final AtomicBoolean wasMoved = new AtomicBoolean();
        Observable.from(cardMap.entrySet())
                .filter(entry -> entry.getValue().getCardNumber() > 0)
                .map(card -> {
                    card.getValue().getLeftPointsToMove(card.getKey())
                            .forEach(card1 ->
                                    dummyLoop(wasMoved, cardMap.get(card1), card.getValue()));
                    return wasMoved.get();
                })
                .map(entry -> wasMoved)
                .filter(AtomicBoolean::get)
                .subscribe();
    }

    private void dummyLoop(AtomicBoolean wasMoved, Card card, Card card1) {
        if (card.getCardNumber() == 0) {
            card.setCardNumber(card1.getCardNumber());
            card1.setCardNumber(0);
        } else if (card.equals(card1)) {
            card.setCardNumber(card.getCardNumber() * 2);
            card1.setCardNumber(0);
            MainActivity.getMainActivity().addScore(card.getCardNumber());
        }
    }


    private void swipeRight() {
        final AtomicBoolean wasMoved = new AtomicBoolean();
        Observable.from(cardMap.entrySet())
                .filter(entry -> entry.getValue().getCardNumber() >0)
                .map(card -> {
                    card.getValue().getRightPointsToMove(card.getKey())
                            .forEach(card1 ->
                                    dummyLoop(wasMoved, cardMap.get(card1), card.getValue()));
                    return wasMoved.get();
                })
                .map(entry -> wasMoved)
                .filter(AtomicBoolean::get)
                .subscribe(atomicBoolean -> {
//                    addRandomNum();
                    checkGameOver();
                });
    }

    private void swipeDown() {
        final AtomicBoolean wasMoved = new AtomicBoolean();
        Observable.from(cardMap.entrySet())
                .filter(entry -> entry.getValue().getCardNumber() > 0)
                .map(card -> {
                    card.getValue().getDownPointsToMove(card.getKey())
                            .forEach(card1 ->
                                    dummyLoop(wasMoved, cardMap.get(card1), card.getValue()));
                    return wasMoved.get();
                })
                .map(entry -> wasMoved)
                .filter(AtomicBoolean::get)
                .subscribe(atomicBoolean -> {
//                    addRandomNum();
                    checkGameOver();
                });
    }

    private void swipeUp() {
        final AtomicBoolean wasMoved = new AtomicBoolean();
        Observable.from(cardMap.entrySet())
                .filter(entry -> entry.getValue().getCardNumber() > 0)
                .map(card -> {
                    card.getValue().getUpPointsToMove(card.getKey())
                            .forEach(card1 ->
                                    dummyLoop(wasMoved, cardMap.get(card1), card.getValue()));
                    return wasMoved.get();
                })
                .map(entry -> wasMoved)
                .filter(AtomicBoolean::get)
                .subscribe(atomicBoolean -> {
//                    addRandomNum();
                    checkGameOver();
                });
    }

    private void checkGameOver() {
        boolean isOver = true;
        ALL:
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                Point point = new Point(x, y);
                Card card = cardMap.get(point);
                if (card.getCardNumber() == 0 ||
                        (x < 3 && card.getCardNumber() == cardMap.get(new Point(x + 1, y)).getCardNumber()) ||
                        (y < 3 && card.getCardNumber() == cardMap.get(new Point(x, y + 1)).getCardNumber())) {
                    isOver = false;
                    break ALL;
                }
            }
        }
        if (isOver) {
            new AlertDialog.Builder(getContext()).setTitle("Game over!").setMessage("Current score:" + MainActivity.score + "，Keep moving！").setPositiveButton("Click to start a new game", (dialogInterface, i) -> startGame()).show();
        }
    }

    class Listener implements View.OnTouchListener {

        private float startX, startY, offsetX, offsetY;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if (!hasTouched) {
                hasTouched = true;
            }

            score = MainActivity.score;

            for (int y = 0; y < 4; ++y) {
                for (int x = 0; x < 4; ++x) {
                    gameNumbers[y][x] = cardMap.get(new Point(x, y)).getCardNumber();
                }
            }
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = motionEvent.getX();
                    startY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    offsetX = motionEvent.getX() - startX;
                    offsetY = motionEvent.getY() - startY;

                    if (Math.abs(offsetX) > Math.abs(offsetY)) {
                        if (offsetX < -5) {
                            swipeLeft();
                            addRandomNum();

                        } else if (offsetX > 5) {
                            swipeRight();
                            addRandomNum();

                        }
                    } else {
                        if (offsetY < -5) {
                            swipeUp();
                            addRandomNum();
                        } else if (offsetY > 5) {
                            swipeDown();
                            addRandomNum();

                        }
                    }

            }
            return true;

        }

    }

}
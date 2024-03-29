package com.example.myapplication.model;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

import com.example.myapplication.activity.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import rx.Observable;

public class GameView extends GridLayout {

    public static final int FIELD_SIZE = 4;//todo add possibility to choose filed size
    private static List<Point> emptyPoints = new ArrayList<>();
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
    protected void onSizeChanged(int weight, int height, int oldWeight, int oldHeight) {
        super.onSizeChanged(weight, height, oldWeight, oldHeight);
        int fieldSize = (Math.min(weight, height) - 10) / 4;
        addCards(fieldSize);
        startGame();
    }

    private void addCards(int size) {
        this.removeAllViews();
        IntStream.range(0, 4)
                .forEach(y -> IntStream.range(0, 4)
                        .forEach(x -> {
                            Card card = new Card(getContext());
                            card.setCardNumber(0);
                            addView(card, size, size);
                            cardMap.put(new Point(x, y), card);
                        }));
    }

    private static void addRandomCardToField() {
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
        addRandomCardToField();
        addRandomCardToField();
    }

    private void swipeLeft() {
        cardMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getCardNumber() > 0)
                .forEach(card -> IntStream.range(0, card.getKey().x)
                        .boxed()
                        .map(x -> new Point(x, card.getKey().y))
                        .forEach(card2 ->
                                compareAndMerge(cardMap.get(card2), card.getValue())));
    }

    private void compareAndMerge(Card card, Card anotherCard) {
        if (card.getCardNumber() == 0) {
            card.setCardNumber(anotherCard.getCardNumber());
            anotherCard.setCardNumber(0);
        } else if (card.equals(anotherCard)) {
            card.setCardNumber(card.getCardNumber() * 2);
            anotherCard.setCardNumber(0);
            MainActivity.getMainActivity().addScore(card.getCardNumber());
        }
    }


    private void swipeRight() {
        cardMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getCardNumber() > 0)
                .forEach(entry1 -> IntStream.range(entry1.getKey().x + 1, FIELD_SIZE)
                        .boxed()
                        .map(x -> new Point(x, entry1.getKey().y))
                        .forEach(card1 ->
                                compareAndMerge(cardMap.get(card1), entry1.getValue())));
    }

    private void swipeDown() {
        cardMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getCardNumber() > 0)
                .forEach(entry1 -> IntStream.range(entry1.getKey().y + 1, 4)
                        .boxed()
                        .map(y -> new Point(entry1.getKey().x, y))
                        .forEach(card1 ->
                                compareAndMerge(cardMap.get(card1), entry1.getValue())));
    }

    private void swipeUp() {
        cardMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getCardNumber() > 0)
                .forEach(entry1 -> IntStream.range(0, entry1.getKey().y)
                        .boxed()
                        .map(y -> new Point(entry1.getKey().x, y))
                        .forEach(card1 ->
                                compareAndMerge(cardMap.get(card1), entry1.getValue())));
    }

    private void checkGameOver() {
        AtomicBoolean isOver = new AtomicBoolean(true);
        IntStream.range(0, 4)
                .forEach(y ->
                        IntStream.range(0, 4)
                                .forEach(x -> {
                                    Point point = new Point(x, y);
                                    Card card = cardMap.get(point);
                                    //todo remove if statement
                                    if (card.getCardNumber() == 0 ||
                                            (x < 3 && card.getCardNumber() == cardMap.get(new Point(x + 1, y)).getCardNumber()) ||
                                            (y < 3 && card.getCardNumber() == cardMap.get(new Point(x, y + 1)).getCardNumber())) {
                                        isOver.set(false);
                                    }
                                }));

        if (isOver.get()) {
            new AlertDialog
                    .Builder(getContext())
                    .setTitle("Game over!")
                    .setMessage("Current score:" + MainActivity.score + "，Keep moving！")
                    .setPositiveButton("Click to start a new game", (dialogInterface, i) -> startGame())
                    .show();
        }
    }

    //todo extract to outer class
    class Listener implements View.OnTouchListener {

        private float startX, startY, offsetX, offsetY;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if (!hasTouched) {
                hasTouched = true;
            }

            score = MainActivity.score;

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
                            addRandomCardToField();

                        } else if (offsetX > 5) {
                            swipeRight();
                            addRandomCardToField();

                        }
                    } else {
                        if (offsetY < -5) {
                            swipeUp();
                            addRandomCardToField();
                        } else if (offsetY > 5) {
                            swipeDown();
                            addRandomCardToField();

                        }
                    }

            }
            return true;

        }

    }

}
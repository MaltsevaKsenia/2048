package com.example.myapplication;

import android.content.Context;
import android.graphics.Point;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Card extends FrameLayout {

    private int cardNumber = 0;
    private TextView cardView;
    private List<Point> availablePointsToMove;

    public List<Point> getUpPointsToMove(Point point) {
        availablePointsToMove = new ArrayList<>();
        for (int j = 0; j < point.y; j++) {
            availablePointsToMove.add(new Point(point.x, j));
        }
        return availablePointsToMove;
    }

    public List<Point> getRightPointsToMove(Point point) {
        availablePointsToMove = new ArrayList<>();
        for (int j = point.x + 1; j < 4; j++) {
            availablePointsToMove.add(new Point(j, point.y));
        }
        return availablePointsToMove;
    }

    public List<Point> getDownPointsToMove(Point point) {
        availablePointsToMove = new ArrayList<>();
        for (int j = point.y + 1; j < 4; j++) {
            availablePointsToMove.add(new Point(point.x, j));
        }
        return availablePointsToMove;
    }

    public Card(Context context) {
        super(context);
        cardView = new TextView(getContext());
        cardView.setTextSize(32);
        cardView.setGravity(Gravity.CENTER);
        LayoutParams layoutParams = new LayoutParams(-1, -1);
        layoutParams.setMargins(10, 10, 0, 0);
        addView(cardView, layoutParams);
        setCardNumber(0);
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
        if (cardNumber > 0) {
            cardView.setText(String.valueOf(cardNumber));
        } else {
            cardView.setText("");
        }
        defineCardBackgroundColor(cardNumber);

    }

    private void defineCardBackgroundColor(int cardNumber) {
        switch (cardNumber) {
            case 0:
                cardView.setBackgroundResource(R.color.number0);
                break;
            case 2:
                cardView.setBackgroundResource(R.color.number2);
                break;
            case 4:
                cardView.setBackgroundResource(R.color.number4);
                break;
            case 8:
                cardView.setBackgroundResource(R.color.number8);
                break;
            case 16:
                cardView.setBackgroundResource(R.color.number16);
                break;
            case 32:
                cardView.setBackgroundResource(R.color.number32);
                break;
            case 64:
                cardView.setBackgroundResource(R.color.number64);
                break;
            case 128:
                cardView.setBackgroundResource(R.color.number128);
                break;
            case 256:
                cardView.setBackgroundResource(R.color.number256);
                break;
            case 512:
                cardView.setBackgroundResource(R.color.number512);
                break;
            case 1024:
                cardView.setBackgroundResource(R.color.number1024);
                break;
            default:
                cardView.setBackgroundResource(R.color.number2048);
                break;
        }
    }

    public boolean equals(Card card) {
        return getCardNumber() == card.getCardNumber();
    }

}

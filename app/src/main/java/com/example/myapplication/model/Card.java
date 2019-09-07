package com.example.myapplication.model;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.myapplication.R;

public class Card extends FrameLayout {

    private int cardNumber = 0;
    private TextView cardView;

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

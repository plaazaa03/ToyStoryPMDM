package com.example.walle3enraya;

import android.widget.ImageButton;

public class Player
{
    // Attributes
    private int humanImg;

    // Constructor
    public Player(int humanImg)
    {
        this.humanImg = humanImg;
    }

    // Getters and Setters
    public int getHumanImg() {return humanImg;}

    public void setHumanImg(int humanImg) {this.humanImg = humanImg;}

    // Methods
    public void makeMovement(ImageButton [][] panel, int row, int col)
    {
        panel[row][col].setImageResource(humanImg);
        panel[row][col].setTag("1");
    }
}

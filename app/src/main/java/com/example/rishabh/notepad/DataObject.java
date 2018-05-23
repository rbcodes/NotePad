package com.example.rishabh.notepad;

public class DataObject {
    private String mText1;
    private String mText2;
    private byte[] imageview;
    private int image_exist;


    DataObject (String text1, String text2,byte[] image, int imageexist){
        mText1 = text1;
        mText2 = text2;
        imageview = image;
        image_exist = imageexist;

    }

    public String getmText1() {
        return mText1;
    }

    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }

    public String getmText2() {
        return mText2;
    }

    public void setmText2(String mText2) {
        this.mText2 = mText2;
    }

    public void setImageview(byte[] imageview) {
        this.imageview = imageview;
    }

    public byte[] getImageview() {

        return imageview;
    }

    public void setImage_exist(int image_exist) {
        this.image_exist = image_exist;
    }

    public int getImage_exist() {

        return image_exist;
    }
}
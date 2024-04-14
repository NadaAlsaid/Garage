package com.example.garage;

public class UserModel {
    private String textfullName ;
    private String textUserName ;
    private String textEmail ;
    private String textDoB ;
    private String textMobile ;
    private String textPwd ;
    private String textGender ;
    private String userId;
    private String url;
    public String getUserId() {
        return userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTextfullName() {
        return textfullName;
    }

    public String getTextUserName() {
        return textUserName;
    }

    public String getTextEmail() {
        return textEmail;
    }

    public String getTextDoB() {
        return textDoB;
    }

    public String getTextMobile() {
        return textMobile;
    }

    public String getTextPwd() {
        return textPwd;
    }

    public String getTextGender() {
        return textGender;
    }

    public UserModel(){

    }
    public UserModel(String textfullName, String textUserName, String textEmail, String textDoB, String textGender, String textMobile, String textPwd,String userId , String url) {
        this.textfullName = textfullName;
        this.textUserName = textUserName;
        this.textEmail = textEmail;
        this.textDoB = textDoB;
        this.textMobile = textMobile;
        this.textPwd = textPwd;
        this.textGender = textGender;
        this.userId = userId ;
        this.url = url ;
    }

    public void setTextfullName(String textfullName) {
        this.textfullName = textfullName;
    }

    public void setTextUserName(String textUserName) {
        this.textUserName = textUserName;
    }

    public void setTextEmail(String textEmail) {
        this.textEmail = textEmail;
    }

    public void setTextDoB(String textDoB) {
        this.textDoB = textDoB;
    }

    public void setTextMobile(String textMobile) {
        this.textMobile = textMobile;
    }

    public void setTextPwd(String textPwd) {
        this.textPwd = textPwd;
    }

    public void setTextGender(String textGender) {
        this.textGender = textGender;
    }
}

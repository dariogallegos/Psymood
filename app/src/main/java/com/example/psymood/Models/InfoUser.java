package com.example.psymood.Models;


public class InfoUser {

    String nameUser= null;
    String emailUser = null;
    String profilePhotoUser = null;
    String psyAudio = null;
    String passwordUser= null;
    String stateUser = null;
    String audiosUser = null;



    public InfoUser(){}


    public InfoUser(String nameUser, String emailUser, String profilePhotoUser) {
        this.nameUser = nameUser;
        this.emailUser = emailUser;
        this.profilePhotoUser = profilePhotoUser;
    }

    public InfoUser(String nameUser, String emailUser, String profilePhotoUser, String stateUser, String audiosUser){
        this.nameUser = nameUser;
        this.emailUser = emailUser;
        this.profilePhotoUser = profilePhotoUser;
        this.stateUser = stateUser;
        this.audiosUser = audiosUser;
    }

    public InfoUser(String emailUser){
        this.emailUser = emailUser;
    }

    public InfoUser(String emailUser, String passwordUser){
        this.emailUser = emailUser;
        this.passwordUser = passwordUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getProfilePhotoUser() {
        return profilePhotoUser;
    }

    public void setProfilePhotoUser(String profilePhotoUser) {
        this.profilePhotoUser = profilePhotoUser;
    }

    public String getPsyAudio() {
        return psyAudio;
    }

    public void setPsyAudio(String psyAudio) {
        this.psyAudio = psyAudio;
    }

    public String getPasswordUser() {
        return passwordUser;
    }

    public void setPasswordUser(String passwordUser) {
        this.passwordUser = passwordUser;
    }
}

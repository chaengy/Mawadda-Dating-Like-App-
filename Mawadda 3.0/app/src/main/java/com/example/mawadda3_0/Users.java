package com.example.mawadda3_0;

public class Users {

    private String hobby;
    private String education;
    private String age;

    private String name;

    public Users() {
        // Default constructor required for calls to DataSnapshot.getValue(Users.class)
    }

    public Users(String hobby, String education, String age, String name) {
        this.hobby = hobby;
        this.education = education;
        this.age = age;
        this.name = name;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

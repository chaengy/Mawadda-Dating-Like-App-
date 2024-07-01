package com.example.mawadda3_0;

public class MatchesObject {
    private String hobby;
    private String name;
    private String profileImageUrl;
    private String age;
    private String education;
    private String plan;
    private String userId;
    public MatchesObject (String name, String age, String hobby, String education,String plan,String profileImageUrl, String userId){
        this.hobby = hobby;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.age = age;
        this.education = education;
        this.plan = plan;
        this.userId = userId;
    }

    public String getHobby(){
        return hobby;
    }
    public void setHobby(String hobby){
        this.hobby = hobby;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }
    public String getAge(){
        return age;
    }
    public void setAge(String age){
        this.age = age;
    }

    public String getEducation(){
        return education;
    }
    public void setEducation(String education){
        this.education = education;
    }

    public String getPlan(){
        return plan;
    }
    public void setPlan(String plan){
        this.plan = plan;
    }
    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId = userId;
    }
}

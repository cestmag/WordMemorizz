package com.websarva.wings.android.wordmemorizz;

import com.google.gson.annotations.SerializedName;

public class Server2local1 {
    @SerializedName("listName")
    private String listName;

    @SerializedName("global_id")
    private String globalId;

    @SerializedName("local_id")
    private String localId;

    @SerializedName("testdays")
    private String testDays;

    @SerializedName("torokuBi")
    private String torokuBi;

    @SerializedName("wordsNum")
    private int wordsNum;

    @SerializedName("userId")
    private String userId;

    @SerializedName("timestamp")
    private String timestamp;

    // Constructors, getters, setters, and other methods go here

    // You may also want to override toString() for debugging purposes
    @Override
    public String toString() {
        return "YourDataClass{" +
                "listName='" + listName + '\'' +
                ", globalId='" + globalId + '\'' +
                ", localId='" + localId + '\'' +
                ", testDays='" + testDays + '\'' +
                ", torokuBi='" + torokuBi + '\'' +
                ", wordsNum=" + wordsNum +
                ", userId='" + userId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

}

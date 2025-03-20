package com.example.profilemanagementapp;


/**
 * Represents a single diary entry with a title and description.
 */
public class DiaryEntryClass {


    String diaryTitle, diaryDesc;
    public DiaryEntryClass(String diaryTitle, String diaryDesc) {
        this.diaryTitle = diaryTitle;
        this.diaryDesc = diaryDesc;
    }

    public DiaryEntryClass() {
    }

    public String getDiaryTitle() {
        return diaryTitle;
    }

    public void setTitle(String diaryTitle) {
        this.diaryTitle = diaryTitle;
    }

    public String getDiaryDesc() {
        return diaryDesc;
    }

    public void setDesc(String diaryDesc) {
        this.diaryDesc = diaryDesc;
    }
}

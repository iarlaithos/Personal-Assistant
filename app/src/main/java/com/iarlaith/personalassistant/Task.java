package com.iarlaith.personalassistant;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {

    enum TaskType {
        ASSIGNMENT("ASSIGNMENT"),
        MCQ("MCQ"),
        TEST("TEST"),
        STUDY("STUDY"),
        EXAM("EXAM"),
        ESSAY("ESSAY"),
        PRESENTATION("PRESENTATION"),
        REPORT("REPORT");

        public final String taskType;

        TaskType(String taskType){
            this.taskType = taskType;
        }
    }

    private String title;
    private String taskType;
    private Date dueDate;
    private String note;

    private Boolean isChecked;

    public Task(String title, String taskType, Date dueDate, String note, Boolean isChecked) {
        this.title = title;
        this.taskType = taskType;
        this.dueDate = dueDate;
        this.note = note;
        this.isChecked = isChecked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", taskType='" + taskType + '\'' +
                ", dueDate=" + dueDate +
                ", note='" + note + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }
}

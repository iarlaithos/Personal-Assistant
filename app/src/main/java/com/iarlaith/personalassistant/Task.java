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

    public Task(String title, String taskType, Date dueDate, String note) {
        this.title = title;
        this.taskType = taskType;
        this.dueDate = dueDate;
        this.note = note;
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

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", taskType='" + taskType + '\'' +
                ", dueDate=" + dueDate +
                ", note='" + note + '\'' +
                '}';
    }
}

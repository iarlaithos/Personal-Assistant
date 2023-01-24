package com.iarlaith.personalassistant;

import java.time.LocalTime;

public class ModuleSession {

    enum Type {
        LECTURE("LECTURE"),
        TUTORIAL("TUTORIAL"),
        LAB("LAB"),
        STUDY("STUDY"),
        EXAM("EXAM");

        public final String sessionType;

        Type(String sessionType){
            this.sessionType = sessionType;
        }
    }

    enum Day {
        MONDAY ("Monday", true),
        TUESDAY("Tuesday", true),
        WEDNESDAY("Wednesday", true),
        THURSDAY("Thursday", true),
        FRIDAY("Friday", true),
        SATURDAY("Saturday", false),
        SUNDAY("Sunday", false);

        public final String dayOfWeek;
        public final boolean isWeekday;

        Day(String dayOfWeek, boolean isWeekday) {
            this.dayOfWeek  = dayOfWeek;
            this.isWeekday = isWeekday;
        }
    }

    private String location;
    private String sessionType;
    private String dayOfTheWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public ModuleSession(String location, String sessionType, String dayOfTheWeek, LocalTime startTime, LocalTime endTime) {
        this.location = location;
        this.sessionType = sessionType;
        this.dayOfTheWeek = dayOfTheWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public String getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public void setDayOfTheWeek(String dayOfTheWeek) {
        this.dayOfTheWeek = dayOfTheWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}

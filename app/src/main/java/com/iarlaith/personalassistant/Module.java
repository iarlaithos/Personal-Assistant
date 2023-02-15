package com.iarlaith.personalassistant;

import java.io.Serializable;
import java.util.List;

public class Module implements Serializable {

    enum ColourEnum {
        RED("RED"),
        ORANGE("ORANGE"),
        YELLOW("YELLOW"),
        GREEN("GREEN"),
        BLUE("BLUE"),
        PURPLE("PURPLE"),
        PINK("PINK"),
        WHITE("WHITE");

        public final String colour;

        ColourEnum(String colour){
            this.colour = colour;
        }
    }

    public String name;
    public String colour;
    public List<ModuleSession> moduleSessions;

    public List<Task> moduleTasks;

    public Module(String name, String colour, List<ModuleSession> moduleSessions, List<Task> moduleTasks) {
        this.name = name;
        this.colour = colour;
        this.moduleSessions = moduleSessions;
        this.moduleTasks = moduleTasks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public List<ModuleSession> getModuleSessions() {
        return moduleSessions;
    }

    public void setModuleSessions(List<ModuleSession> moduleSessions) {
        this.moduleSessions = moduleSessions;
    }

    public List<Task> getModuleTasks() {
        return moduleTasks;
    }

    public void setModuleTasks(List<Task> moduleTasks) {
        this.moduleTasks = moduleTasks;
    }

    @Override
    public String toString() {
        return "Module{" +
                "name='" + name + '\'' +
                ", colour='" + colour + '\'' +
                ", moduleSessions=" + moduleSessions +
                ", moduleTasks=" + moduleTasks +
                '}';
    }
}

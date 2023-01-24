package com.iarlaith.personalassistant;

import java.util.Set;

public class Module {

    enum ColourEnum {
        RED,
        ORANGE,
        YELLOW,
        GREEN,
        BLUE,
        PURPLE,
        PINK,
        LIME,
        WHITE
    }

    public String name;
    public ColourEnum colour;
    public Set<ModuleSession> moduleSessions;

    public Module(String name, ColourEnum colour, Set<ModuleSession> moduleSessions) {
        this.name = name;
        this.colour = colour;
        this.moduleSessions = moduleSessions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColourEnum getColour() {
        return colour;
    }

    public void setColour(ColourEnum colour) {
        this.colour = colour;
    }

    public Set<ModuleSession> getModuleSessions() {
        return moduleSessions;
    }

    public void setModuleSessions(Set<ModuleSession> moduleSessions) {
        this.moduleSessions = moduleSessions;
    }
}

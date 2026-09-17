package com.studyplanner.model;

import java.util.Objects;

/**
 * Domain model representing an academic subject or course.
 */
public class Subject {
    private int id;
    private String name;
    private String code;
    private int credits;
    private String description;

    public Subject() {
    }

    public Subject(int id, String name, String code, int credits, String description) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.credits = credits;
        this.description = description;
    }

    public Subject(String name, String code, int credits, String description) {
        this(0, name, code, credits, description);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return id == subject.id && Objects.equals(code, subject.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%d credits)", code, name, credits);
    }
}

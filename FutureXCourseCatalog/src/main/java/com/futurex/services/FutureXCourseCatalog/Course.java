package com.futurex.services.FutureXCourseCatalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Course {

    private Long courseid;
    private String coursename;
    private String author;

    public Course() {
    }

    public Course(Long courseid, String coursename, String author) {
        this.courseid = courseid;
        this.coursename = coursename;
        this.author = author;
    }

    // Getters and setters
    public Long getCourseid() {
        return courseid;
    }

    public void setCourseid(Long courseid) {
        this.courseid = courseid;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
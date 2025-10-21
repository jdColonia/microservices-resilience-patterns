package com.futurex.services.FutureXCourseCatalog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class CatalogController {

    @Autowired
    private ResilientCourseService resilientCourseService;

    @RequestMapping("/")
    public CompletableFuture<String> getCatalogHome() {
        return resilientCourseService.getCourseAppHome()
                .thenApply(courseAppMessage -> "Welcome to FutureX Course Catalog " + courseAppMessage);
    }

    @RequestMapping("/catalog")
    public CompletableFuture<String> getCatalog() {
        return resilientCourseService.getCourses()
                .thenApply(courses -> "Our courses are " + courses);
    }

    @RequestMapping("/firstcourse")
    public CompletableFuture<String> getSpecificCourse() {
        return resilientCourseService.getSpecificCourse(1L)
                .thenApply(courseName -> "Our first course is " + courseName);
    }

    @RequestMapping("/course/{id}")
    public CompletableFuture<String> getCourseById(@PathVariable("id") Long id) {
        return resilientCourseService.getSpecificCourse(id)
                .thenApply(courseName -> "Course " + id + " is " + courseName);
    }

    @RequestMapping("/health")
    public String health() {
        return "Catalog service is healthy";
    }
}

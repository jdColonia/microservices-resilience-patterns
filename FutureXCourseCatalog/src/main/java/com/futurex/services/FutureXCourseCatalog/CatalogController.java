package com.futurex.services.FutureXCourseCatalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

import java.util.concurrent.CompletableFuture;

@RestController
public class CatalogController {

    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);

    @Autowired
    private ResilientCourseService resilientCourseService;

    @Autowired
    Tracer tracer;

    @RequestMapping("/")
    public CompletableFuture<String> getCatalogHome() {
        logger.info("GET / - Catalog home endpoint called");
        Span span = tracer.spanBuilder("getCatalogHome").startSpan();
        try{
            return resilientCourseService.getCourseAppHome()
                .thenApply(courseAppMessage -> {
                    logger.info("Successfully retrieved catalog home");
                    return "Welcome to FutureX Course Catalog " + courseAppMessage;
                })
                .exceptionally(ex -> {
                    logger.error("Error retrieving catalog home: {}", ex.getMessage(), ex);
                    return "Welcome to FutureX Course Catalog - Service temporarily unavailable";
                });
        } finally {
            span.end();
        }
    }

    @RequestMapping("/catalog")
    public CompletableFuture<String> getCatalog() {
        logger.info("GET /catalog - Getting catalog");
        Span span = tracer.spanBuilder("getCatalog").startSpan();
        try{
            return resilientCourseService.getCourses()
                .thenApply(courses -> {
                    logger.info("Successfully retrieved catalog");
                    return "Our courses are " + courses;
                })
                .exceptionally(ex -> {
                    logger.error("Error retrieving catalog: {}", ex.getMessage(), ex);
                    return "Our courses are []";
                });
        } finally {
            span.end();
        }
    }

    @RequestMapping("/firstcourse")
    public CompletableFuture<String> getSpecificCourse() {
        logger.info("GET /firstcourse - Getting first course");
        Span span = tracer.spanBuilder("getSpecificCourse").startSpan();
        try{
            return resilientCourseService.getSpecificCourse(1L)
                .thenApply(courseName -> {
                    logger.info("Successfully retrieved first course: {}", courseName);
                    return "Our first course is " + courseName;
                })
                .exceptionally(ex -> {
                    logger.error("Error retrieving first course: {}", ex.getMessage(), ex);
                    return "Our first course is not available";
                });
        } finally {
            span.end();
        }
    }

    @RequestMapping("/course/{id}")
    public CompletableFuture<String> getCourseById(@PathVariable("id") Long id) {
        logger.info("GET /course/{} - Getting course by ID", id);
        return resilientCourseService.getSpecificCourse(id)
                .thenApply(courseName -> {
                    logger.info("Successfully retrieved course {}: {}", id, courseName);
                    return "Course " + id + " is " + courseName;
                })
                .exceptionally(ex -> {
                    logger.error("Error retrieving course {}: {}", id, ex.getMessage(), ex);
                    return "Course " + id + " is not available";
                });
    }

    @RequestMapping("/health")
    public String health() {
        logger.debug("GET /health - Health check endpoint called");
        return "Catalog service is healthy";
    }
}

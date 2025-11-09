package com.futurex.services.FutureXCourseApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@RestController
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    Tracer tracer;

    @RequestMapping("/")
    public String getCourseAppHome() {
        logger.info("GET / - Course App Home endpoint called");
        Span span = tracer.spanBuilder("getCourseAppHome").startSpan();
        try{
            logger.info("Returning Course App Home response");
            return ("Course App Home");
        } finally {
            span.end();
        }
        
    }

    @RequestMapping("/courses")
    public List<Course> getCourses() {
        logger.info("GET /courses - Getting all courses");
        Span span = tracer.spanBuilder("getCourses").startSpan();
        try{
            List<Course> courses = courseRepository.findAll();
            logger.info("Successfully retrieved {} courses", courses.size());
            return courses;
        } catch (Exception e) {
            logger.error("Error retrieving courses: {}", e.getMessage(), e);
            throw e;
        } finally {
            span.end();
        }
    }

    @RequestMapping("/{id}")
    public Course getSpecificCourse(@PathVariable("id") BigInteger id) {
        logger.info("GET /{} - Getting course by ID", id);
        try {
            Optional<Course> course = courseRepository.findById(id);
            if (course.isPresent()) {
                logger.info("Successfully retrieved course with ID: {}", id);
                return course.get();
            } else {
                logger.error("Course not found with ID: {}", id);
                throw new RuntimeException("Curso no encontrado con ID: " + id);
            }
        } catch (RuntimeException e) {
            logger.error("Error retrieving course with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @RequestMapping(method = RequestMethod.POST, value="/courses")
    public Course saveCourse(@RequestBody Course course) {
        logger.info("POST /courses - Saving new course: {}", course.getCoursename());
        try {
            Course savedCourse = courseRepository.save(course);
            logger.info("Successfully saved course with ID: {}", savedCourse.getCourseid());
            return savedCourse;
        } catch (Exception e) {
            logger.error("Error saving course: {}", e.getMessage(), e);
            throw e;
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "{id}")
    public void deleteCourse(@PathVariable BigInteger id) {
        logger.info("DELETE /{} - Deleting course by ID", id);
        try {
            if (courseRepository.existsById(id)) {
                courseRepository.deleteById(id);
                logger.info("Successfully deleted course with ID: {}", id);
            } else {
                logger.error("Course not found for deletion with ID: {}", id);
                throw new RuntimeException("Curso no encontrado con ID: " + id);
            }
        } catch (RuntimeException e) {
            logger.error("Error deleting course with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @RequestMapping("/health")
    public String health() {
        logger.debug("GET /health - Health check endpoint called");
        return "Service is healthy";
    }
}

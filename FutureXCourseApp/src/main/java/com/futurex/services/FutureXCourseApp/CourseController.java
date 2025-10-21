package com.futurex.services.FutureXCourseApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@RestController
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @RequestMapping("/")
    public String getCourseAppHome() {
        return ("Course App Home");
    }

    @RequestMapping("/courses")
    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    @RequestMapping("/{id}")
    public Course getSpecificCourse(@PathVariable("id") BigInteger id) {
        Optional<Course> course = courseRepository.findById(id);
        return course.orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + id));
    }

    @RequestMapping(method = RequestMethod.POST, value="/courses")
    public Course saveCourse(@RequestBody Course course) {
        return courseRepository.save(course);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "{id}")
    public void deleteCourse(@PathVariable BigInteger id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
        } else {
            throw new RuntimeException("Curso no encontrado con ID: " + id);
        }
    }

    @RequestMapping("/health")
    public String health() {
        return "Service is healthy";
    }
}

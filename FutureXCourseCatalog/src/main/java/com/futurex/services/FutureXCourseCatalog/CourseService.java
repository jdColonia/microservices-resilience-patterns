package com.futurex.services.FutureXCourseCatalog;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class CourseService {

    @Autowired
    private EurekaClient eurekaClient;

    private final RestTemplate restTemplate = new RestTemplate();

    @CircuitBreaker(name = "course-service", fallbackMethod = "getCourseAppHomeFallback")
    @Retry(name = "course-service")
    @TimeLimiter(name = "course-service")
    public CompletableFuture<String> getCourseAppHome() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("fx-course-service", false);
                String courseAppURL = instanceInfo.getHomePageUrl();
                return restTemplate.getForObject(courseAppURL, String.class);
            } catch (Exception e) {
                throw new RuntimeException("Error al obtener información del servicio de cursos", e);
            }
        });
    }

    @CircuitBreaker(name = "course-service", fallbackMethod = "getCoursesFallback")
    @Retry(name = "course-service")
    @TimeLimiter(name = "course-service")
    public CompletableFuture<String> getCourses() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("fx-course-service", false);
                String courseAppURL = instanceInfo.getHomePageUrl() + "/courses";
                return restTemplate.getForObject(courseAppURL, String.class);
            } catch (Exception e) {
                throw new RuntimeException("Error al obtener la lista de cursos", e);
            }
        });
    }

    @CircuitBreaker(name = "course-service", fallbackMethod = "getSpecificCourseFallback")
    @Retry(name = "course-service")
    @TimeLimiter(name = "course-service")
    public CompletableFuture<String> getSpecificCourse(Long courseId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("fx-course-service", false);
                String courseAppURL = instanceInfo.getHomePageUrl() + "/" + courseId;
                Course course = restTemplate.getForObject(courseAppURL, Course.class);
                return course != null ? course.getCoursename() : "Curso no encontrado";
            } catch (Exception e) {
                throw new RuntimeException("Error al obtener el curso específico", e);
            }
        });
    }

    // Métodos de fallback
    public CompletableFuture<String> getCourseAppHomeFallback(Exception ex) {
        return CompletableFuture.completedFuture("Servicio de cursos temporalmente no disponible");
    }

    public CompletableFuture<String> getCoursesFallback(Exception ex) {
        return CompletableFuture.completedFuture("[]"); // Lista vacía como fallback
    }

    public CompletableFuture<String> getSpecificCourseFallback(Exception ex) {
        return CompletableFuture.completedFuture("Curso no disponible temporalmente");
    }
}

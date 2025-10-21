package com.futurex.services.FutureXCourseCatalog;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
public class ResilientCourseService {

    @Autowired
    private EurekaClient eurekaClient;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private RetryRegistry retryRegistry;

    private final RestTemplate restTemplate = new RestTemplate();

    public CompletableFuture<String> getCourseAppHome() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("course-service");
        Retry retry = retryRegistry.retry("course-service");

        Supplier<String> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> {
                    return Retry.decorateSupplier(retry, () -> {
                        try {
                            InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("fx-course-service", false);
                            String courseAppURL = instanceInfo.getHomePageUrl();
                            return restTemplate.getForObject(courseAppURL, String.class);
                        } catch (Exception e) {
                            // Cualquier excepción, incluyendo ConnectException, será manejada por el Circuit Breaker
                            throw new RuntimeException("Error al obtener información del servicio de cursos: " + e.getMessage(), e);
                        }
                    }).get();
                });

        return CompletableFuture.supplyAsync(() -> {
            try {
                return decoratedSupplier.get();
            } catch (Exception e) {
                return "Servicio de cursos temporalmente no disponible";
            }
        });
    }

    public CompletableFuture<String> getCourses() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("course-service");
        Retry retry = retryRegistry.retry("course-service");

        Supplier<String> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> {
                    return Retry.decorateSupplier(retry, () -> {
                        try {
                            InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("fx-course-service", false);
                            String courseAppURL = instanceInfo.getHomePageUrl() + "/courses";
                            return restTemplate.getForObject(courseAppURL, String.class);
                        } catch (Exception e) {
                            // Cualquier excepción, incluyendo ConnectException, será manejada por el Circuit Breaker
                            throw new RuntimeException("Error al obtener la lista de cursos: " + e.getMessage(), e);
                        }
                    }).get();
                });

        return CompletableFuture.supplyAsync(() -> {
            try {
                return decoratedSupplier.get();
            } catch (Exception e) {
                return "[]"; // Lista vacía como fallback
            }
        });
    }

    public CompletableFuture<String> getSpecificCourse(Long courseId) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("course-service");
        Retry retry = retryRegistry.retry("course-service");

        Supplier<String> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> {
                    return Retry.decorateSupplier(retry, () -> {
                        try {
                            InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("fx-course-service", false);
                            String courseAppURL = instanceInfo.getHomePageUrl() + "/" + courseId;
                            Course course = restTemplate.getForObject(courseAppURL, Course.class);
                            return course != null ? course.getCoursename() : "Curso no encontrado";
                        } catch (Exception e) {
                            // Cualquier excepción, incluyendo ConnectException, será manejada por el Circuit Breaker
                            throw new RuntimeException("Error al obtener el curso específico: " + e.getMessage(), e);
                        }
                    }).get();
                });

        return CompletableFuture.supplyAsync(() -> {
            try {
                return decoratedSupplier.get();
            } catch (Exception e) {
                return "Curso no disponible temporalmente";
            }
        });
    }
}

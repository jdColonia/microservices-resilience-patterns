package com.futurex.services.FutureXCourseCatalog;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
public class ResilientCourseService {

    private static final Logger logger = LoggerFactory.getLogger(ResilientCourseService.class);

    @Autowired
    private EurekaClient eurekaClient;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private RetryRegistry retryRegistry;

    private final RestTemplate restTemplate = new RestTemplate();

    public CompletableFuture<String> getCourseAppHome() {
        logger.info("Getting course app home - initiating request");
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("course-service");
        Retry retry = retryRegistry.retry("course-service");

        Supplier<String> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> {
                    return Retry.decorateSupplier(retry, () -> {
                        try {
                            logger.info("Attempting to get course service instance from Eureka");
                            InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("fx-course-service", false);
                            String courseAppURL = instanceInfo.getHomePageUrl();
                            logger.info("Calling course service at URL: {}", courseAppURL);
                            String result = restTemplate.getForObject(courseAppURL, String.class);
                            logger.info("Successfully retrieved course app home");
                            return result;
                        } catch (Exception e) {
                            logger.error("Error al obtener información del servicio de cursos: {}", e.getMessage(), e);
                            // Cualquier excepción, incluyendo ConnectException, será manejada por el Circuit Breaker
                            throw new RuntimeException("Error al obtener información del servicio de cursos: " + e.getMessage(), e);
                        }
                    }).get();
                });

        return CompletableFuture.supplyAsync(() -> {
            try {
                String result = decoratedSupplier.get();
                logger.info("Circuit breaker call completed successfully for getCourseAppHome");
                return result;
            } catch (Exception e) {
                logger.error("Circuit breaker failed or service unavailable for getCourseAppHome: {}", e.getMessage(), e);
                logger.warn("Circuit breaker state: {}", circuitBreaker.getState());
                return "Servicio de cursos temporalmente no disponible";
            }
        });
    }

    public CompletableFuture<String> getCourses() {
        logger.info("Getting courses list - initiating request");
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("course-service");
        Retry retry = retryRegistry.retry("course-service");

        Supplier<String> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> {
                    return Retry.decorateSupplier(retry, () -> {
                        try {
                            logger.info("Attempting to get courses from course service");
                            InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("fx-course-service", false);
                            String courseAppURL = instanceInfo.getHomePageUrl() + "/courses";
                            logger.info("Calling course service at URL: {}", courseAppURL);
                            String result = restTemplate.getForObject(courseAppURL, String.class);
                            logger.info("Successfully retrieved courses list");
                            return result;
                        } catch (Exception e) {
                            logger.error("Error al obtener la lista de cursos: {}", e.getMessage(), e);
                            // Cualquier excepción, incluyendo ConnectException, será manejada por el Circuit Breaker
                            throw new RuntimeException("Error al obtener la lista de cursos: " + e.getMessage(), e);
                        }
                    }).get();
                });

        return CompletableFuture.supplyAsync(() -> {
            try {
                String result = decoratedSupplier.get();
                logger.info("Circuit breaker call completed successfully for getCourses");
                return result;
            } catch (Exception e) {
                logger.error("Circuit breaker failed or service unavailable for getCourses: {}", e.getMessage(), e);
                logger.warn("Circuit breaker state: {}", circuitBreaker.getState());
                return "[]"; // Lista vacía como fallback
            }
        });
    }

    public CompletableFuture<String> getSpecificCourse(Long courseId) {
        logger.info("Getting specific course with ID: {}", courseId);
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("course-service");
        Retry retry = retryRegistry.retry("course-service");

        Supplier<String> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> {
                    return Retry.decorateSupplier(retry, () -> {
                        try {
                            logger.info("Attempting to get course {} from course service", courseId);
                            InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("fx-course-service", false);
                            String courseAppURL = instanceInfo.getHomePageUrl() + "/" + courseId;
                            logger.info("Calling course service at URL: {}", courseAppURL);
                            Course course = restTemplate.getForObject(courseAppURL, Course.class);
                            if (course != null) {
                                logger.info("Successfully retrieved course {}: {}", courseId, course.getCoursename());
                                return course.getCoursename();
                            } else {
                                logger.warn("Course {} not found in course service", courseId);
                                return "Curso no encontrado";
                            }
                        } catch (Exception e) {
                            logger.error("Error al obtener el curso específico {}: {}", courseId, e.getMessage(), e);
                            // Cualquier excepción, incluyendo ConnectException, será manejada por el Circuit Breaker
                            throw new RuntimeException("Error al obtener el curso específico: " + e.getMessage(), e);
                        }
                    }).get();
                });

        return CompletableFuture.supplyAsync(() -> {
            try {
                String result = decoratedSupplier.get();
                logger.info("Circuit breaker call completed successfully for getSpecificCourse with ID: {}", courseId);
                return result;
            } catch (Exception e) {
                logger.error("Circuit breaker failed or service unavailable for getSpecificCourse with ID {}: {}", courseId, e.getMessage(), e);
                logger.warn("Circuit breaker state: {}", circuitBreaker.getState());
                return "Curso no disponible temporalmente";
            }
        });
    }
}

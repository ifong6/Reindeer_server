package us.reindeers.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class HealthController {

    private final DataSource dataSource;

    @GetMapping( "/health")
    public ResponseEntity<Object> healthCheckConnection() {
        try {
            Connection connection = dataSource.getConnection();
            connection.close();
            return ResponseEntity.ok()
                    .body("UserService Healthy!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(null);
        }
    }
}
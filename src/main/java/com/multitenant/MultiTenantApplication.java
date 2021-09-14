package com.multitenant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.SQLException;

@Slf4j
@SpringBootApplication
public class MultiTenantApplication {

    public static void main(String[] args) throws SQLException {
        SpringApplication.run(MultiTenantApplication.class, args);
    }
}

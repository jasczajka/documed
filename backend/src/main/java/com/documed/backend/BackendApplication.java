package com.documed.backend;

import com.documed.backend.visits.FacilityDAO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class BackendApplication {

  @Autowired
  FacilityDAO facilityDAO;

  public static void main(String[] args) {
    SpringApplication.run(BackendApplication.class, args);
  }

  @PostConstruct
  public void init() throws SQLException {
    System.out.println(facilityDAO.getAll());
  }

}



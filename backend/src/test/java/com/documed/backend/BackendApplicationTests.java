package com.documed.backend;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class BackendApplicationTests {

  @Mock private DataSource dataSource;

  @InjectMocks private BackendApplicationTests backendApplicationTests;

  @Test
  void contextLoads() {}
}

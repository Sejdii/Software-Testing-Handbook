package pl.sejdii.example.adapter.out.postgres;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class PostgresTestIT {

  protected static final PostgreSQLContainer<?> postgreSQLContainer =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

  static {
    postgreSQLContainer.start();
  }

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
  }

  @Autowired private SessionFactory sessionFactory;

  protected Statistics statistics;

  @BeforeEach
  void enableStatistics() {
    statistics = sessionFactory.getStatistics();
    statistics.clear();
    statistics.setStatisticsEnabled(true);
  }
}

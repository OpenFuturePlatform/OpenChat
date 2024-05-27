package io.openfuture.openmessanger;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
class OpenMessangerApplicationTests implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Test
    void contextLoads() {
    }

    private static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withDatabaseName("open_chat")
            .withUsername("postgres")
            .withPassword("123456");

    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {
        lazyInit();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,
                "spring.datasource.url=" + CONTAINER.getJdbcUrl(),
                "spring.datasource.username=" + CONTAINER.getUsername(),
                "spring.datasource.password=" + CONTAINER.getPassword()
        );
    }

    private void lazyInit() {
        synchronized (CONTAINER) {
            if (!CONTAINER.isRunning()) {
                CONTAINER.start();
            }
        }
    }

}

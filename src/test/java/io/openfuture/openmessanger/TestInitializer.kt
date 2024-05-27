package io.openfuture.openmessanger

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@Configuration
class TestInitializer : ApplicationContextInitializer<ConfigurableApplicationContext?> {

    private fun lazyInit() {
        synchronized(CONTAINER) {
            if (!CONTAINER.isRunning) {
                CONTAINER.start()
            }
        }
    }

    companion object {
        private val CONTAINER: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
            .withDatabaseName("open_chat")
            .withUsername("postgres")
            .withPassword("123456")
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        lazyInit()
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
            applicationContext!!,
            "spring.datasource.url=" + CONTAINER.jdbcUrl,
            "spring.datasource.username=" + CONTAINER.username,
            "spring.datasource.password=" + CONTAINER.password
        )
    }
}


package io.openfuture.openmessanger

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration


@SpringBootTest
@ContextConfiguration(initializers = [TestInitializer::class])
class OpenMessangerApplicationTests {

    @Test
    fun contextLoads() {
    }

}
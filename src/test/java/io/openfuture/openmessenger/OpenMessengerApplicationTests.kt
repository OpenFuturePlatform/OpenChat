package io.openfuture.openmessenger

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration


@SpringBootTest
@ContextConfiguration(initializers = [TestInitializer::class])
class OpenMessengerApplicationTests {

    @Test
    fun contextLoads() {
    }

}
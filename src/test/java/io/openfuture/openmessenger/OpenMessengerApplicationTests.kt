package io.openfuture.openmessenger

import org.junit.jupiter.api.Test
import org.kurento.client.KurentoClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(initializers = [TestInitializer::class])
class OpenMessengerApplicationTests {

    @MockBean
    lateinit var kurentoClient: KurentoClient

    @Test
    fun contextLoads() {
    }

}
package test.streambridge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class InterceptorTest extends InterceptorAbstractTest {
  @Test
  void test1() {
    assertThat(appender.list).isEmpty();

    streamProcessor.testProduce("message");

    await()
        .atMost(5, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              assertThat(streamProcessor.getMessageList()).hasSize(1);
            });

    assertThat(appender.list)
        .hasSize(8)
        .extracting(ILoggingEvent::getFormattedMessage)
        .containsExactlyInAnyOrder(
            "Outbound channel interceptor-test-application.testSupply-out-0 received message in topic topic1",
            "Outbound channel interceptor-test-application.testSupply-out-0 processed message in topic topic1 and message was sent",
            "Inbound channel interceptor-test-application.interceptor-in-0 received message in topic topic1, partition 0, offset 0 for consumer group interceptor",
            "Outbound channel interceptor-test-application.interceptor-out-0 received message in topic topic2",
            "Outbound channel interceptor-test-application.interceptor-out-0 processed message in topic topic2 and message was sent",
            "Inbound channel interceptor-test-application.interceptor-in-0 processed message in topic topic1, partition 0, offset 0 for consumer group interceptor",
            "Inbound channel interceptor-test-application.testConsume-in-0 received message in topic topic2, partition 0, offset 0 for consumer group test-interceptor",
            "Inbound channel interceptor-test-application.testConsume-in-0 processed message in topic topic2, partition 0, offset 0 for consumer group test-interceptor");
  }
}

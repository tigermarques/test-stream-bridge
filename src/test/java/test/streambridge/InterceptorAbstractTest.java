package test.streambridge;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.stereotype.Component;
import pt.streambridge.interceptor.InputChannelInterceptor;
import pt.streambridge.interceptor.OutputChannelInterceptor;

@SpringBootTest(classes = Application.class)
@EmbeddedKafka(
    controlledShutdown = true,
    bootstrapServersProperty = "spring.cloud.stream.kafka.binder.brokers",
    partitions = 1,
    topics = {"topic1", "topic2"})
public abstract class InterceptorAbstractTest {
  @Autowired protected StreamProcessor streamProcessor;
  protected ListAppender<ILoggingEvent> appender;

  @BeforeEach
  void setup() {
    Logger inputLogger = (Logger) LoggerFactory.getLogger(InputChannelInterceptor.class);
    Logger outputLogger = (Logger) LoggerFactory.getLogger(OutputChannelInterceptor.class);

    appender = new ListAppender<>();
    appender.start();

    // add the appender to the logger
    inputLogger.addAppender(appender);
    outputLogger.addAppender(appender);
  }

  @Component
  static class StreamProcessor {

    @Getter private final List<String> messageList = new ArrayList<>();
    @Autowired private StreamBridge streamBridge;

    public void testProduce(String message) {
      streamBridge.send("testSupply-out-0", message);
    }

    @Bean
    public Consumer<String> testConsume() {
      return messageList::add;
    }
  }
}

package pt.streambridge.interceptor;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.stream.messaging.DirectWithAttributesChannel;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

@Slf4j
public class InputChannelInterceptor implements ChannelInterceptor {
  @Override
  public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
    InterceptorProperties properties = new InterceptorProperties(message, channel);
    log.debug(
        "Inbound channel {} received message in topic {}, partition {}, offset {} for consumer group {}",
        properties.getChannelName(),
        properties.getTopicName(),
        properties.getPartition(),
        properties.getOffset(),
        properties.getGroupId());
    log.trace("Full message is {}", message);
    return ChannelInterceptor.super.preSend(message, channel);
  }

  @Override
  public void postSend(@NotNull Message<?> message, @NotNull MessageChannel channel, boolean sent) {
    InterceptorProperties properties = new InterceptorProperties(message, channel);
    log.debug(
        "Inbound channel {} processed message in topic {}, partition {}, offset {} for consumer group {}",
        properties.getChannelName(),
        properties.getTopicName(),
        properties.getPartition(),
        properties.getOffset(),
        properties.getGroupId());
    log.trace("Full message is {}", message);
    ChannelInterceptor.super.postSend(message, channel, sent);
  }

  @Data
  static class InterceptorProperties {
    private final String channelName;
    private final String topicName;
    private final String groupId;
    private final Integer partition;
    private final Long offset;

    public InterceptorProperties(Message<?> message, MessageChannel channel) {
      channelName =
          channel instanceof DirectWithAttributesChannel directWithAttributesChannel
              ? directWithAttributesChannel.getFullChannelName()
              : "";
      topicName = message.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC, String.class);
      groupId = message.getHeaders().get(KafkaHeaders.GROUP_ID, String.class);
      partition = message.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION, Integer.class);
      offset = message.getHeaders().get(KafkaHeaders.OFFSET, Long.class);
    }
  }
}

package pt.streambridge.interceptor;

import java.util.Optional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.cloud.stream.messaging.DirectWithAttributesChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

@Slf4j
@RequiredArgsConstructor
public class OutputChannelInterceptor implements ChannelInterceptor {
  private final BindingServiceProperties bindingServiceProperties;

  @Override
  public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
    InterceptorProperties properties = new InterceptorProperties(channel, bindingServiceProperties);
    log.debug(
        "Outbound channel {} received message in topic {}",
        properties.getChannelName(),
        properties.getTopicName());
    log.trace("Full message is {}", message);
    return message;
  }

  @Override
  public void postSend(@NotNull Message<?> message, @NotNull MessageChannel channel, boolean sent) {
    InterceptorProperties properties = new InterceptorProperties(channel, bindingServiceProperties);
    log.debug(
        "Outbound channel {} processed message in topic {} and message {} sent",
        properties.getChannelName(),
        properties.getTopicName(),
        sent ? "was" : "was not");
    log.trace("Full message is {}", message);
  }

  @Data
  static class InterceptorProperties {
    private final String channelName;
    private final String topicName;

    public InterceptorProperties(
        MessageChannel channel, BindingServiceProperties bindingServiceProperties) {
      if (channel instanceof DirectWithAttributesChannel directChannel) {
        channelName = directChannel.getFullChannelName();
        topicName =
            directChannel.getComponentName() != null
                ? Optional.ofNullable(
                        bindingServiceProperties.getBindingProperties(
                            directChannel.getComponentName()))
                    .map(BindingProperties::getDestination)
                    .orElse("")
                : "";
      } else {
        channelName = "";
        topicName = "";
      }
    }
  }
}

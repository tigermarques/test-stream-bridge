package pt.streambridge.config;

import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.integration.config.GlobalChannelInterceptor;
import pt.streambridge.interceptor.InputChannelInterceptor;
import pt.streambridge.interceptor.OutputChannelInterceptor;

@Configuration
public class StreamConfiguration {
  @Bean
  @GlobalChannelInterceptor(patterns = "*-in-*", order = Ordered.LOWEST_PRECEDENCE)
  public InputChannelInterceptor inputChannelInterceptor() {
    return new InputChannelInterceptor();
  }

  @Bean
  @GlobalChannelInterceptor(patterns = "*-out-*", order = Ordered.HIGHEST_PRECEDENCE)
  public OutputChannelInterceptor outputChannelInterceptor(BindingServiceProperties properties) {
    return new OutputChannelInterceptor(properties);
  }
}

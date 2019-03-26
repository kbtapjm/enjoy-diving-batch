package kr.co.pjm.diving.batch;

import java.nio.charset.Charset;
import java.util.Collections;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.Compression;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import kr.co.pjm.diving.batch.configuration.web.interceptor.LoggingClientHttpRequestInterceptor;

@SpringBootApplication(scanBasePackages = { "kr.co.pjm.diving.batch", "kr.co.pjm.diving.common" })
@EnableJpaRepositories(basePackages = { "kr.co.pjm.diving.common.repository" })
@EntityScan(basePackages = { "kr.co.pjm.diving.common.domain" })
public class Application extends SpringBootServletInitializer implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    builder.sources(Application.class);
    return builder;
  }

  @Bean
  public EmbeddedServletContainerCustomizer containerCustomizer() throws Exception {
    return (ConfigurableEmbeddedServletContainer container) -> {
      if (container instanceof TomcatEmbeddedServletContainerFactory) {
        Compression compression = new Compression();
        compression.setEnabled(true);
        compression.setMinResponseSize(2048);
        container.setCompression(compression);
      }
    };
  }

  @Bean
  public HttpMessageConverter<String> responseBodyConverter() {
    return new StringHttpMessageConverter(Charset.forName("UTF-8"));
  }

  @Bean
  public FilterRegistrationBean filterRegistrationBean() {
    FilterRegistrationBean registrationBean = new FilterRegistrationBean();
    CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
    characterEncodingFilter.setForceEncoding(true);
    characterEncodingFilter.setEncoding("UTF-8");

    registrationBean.setFilter(characterEncodingFilter);

    return registrationBean;
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    RestTemplate restTemplate = builder.setConnectTimeout(8 * 1000).setReadTimeout(8 * 1000).build();

    restTemplate.setInterceptors(Collections.singletonList(new LoggingClientHttpRequestInterceptor()));

    return restTemplate;
  }

  @Bean
  public Jackson2ObjectMapperBuilder objectMapperBuilder() {
    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
    return builder.modulesToInstall(new JavaTimeModule());
  }

  @Override
  public void run(String... args) throws Exception {
    // CommandLineRunner
  }

}

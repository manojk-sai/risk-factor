package com.manoj.risk;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

class ApplicationTest {

  @Test
  void mainBootstrapsContextWithoutMongo() {
    try (ConfigurableApplicationContext context =
        SpringApplication.run(
            Application.class,
            "--spring.main.web-application-type=none",
            "--spring.autoconfigure.exclude="
                + "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration")) {
      assertThat(context.isActive()).isTrue();
    }
  }
}

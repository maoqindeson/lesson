package io.renren.modules.generator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
@MapperScan("io.renren.modules.generator.dao")
public class LessonApplication {
	public static void main(String[] args) {
		SpringApplication.run(LessonApplication.class, args);
	}
}


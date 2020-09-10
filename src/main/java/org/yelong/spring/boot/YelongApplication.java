package org.yelong.spring.boot;

/**
 * @since 2.0.1
 */
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ResourceLoader;

public class YelongApplication extends SpringApplication {

	public YelongApplication(Class<?>... primarySources) {
		this(null, primarySources);
	}

	public YelongApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
		super(resourceLoader, primarySources);
		setBanner(YelongBanner.INSTANCE);
	}

	public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
		return run(new Class<?>[] { primarySource }, args);
	}

	public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
		return new YelongApplication(primarySources).run(args);
	}

}

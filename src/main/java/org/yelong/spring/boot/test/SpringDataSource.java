package org.yelong.spring.boot.test;

import org.yelong.core.jdbc.DataSourceProperties;
import org.yelong.core.jdbc.dialect.Dialect;
import org.yelong.core.jdbc.dialect.DialectFactory;
import org.yelong.support.yaml.YamlProperties;

/**
 * @since 2.0.1
 */
public class SpringDataSource {

	public static final String URL_NAME = "spring.datasource.url";

	public static final String USERNAME_NAME = "spring.datasource.username";

	public static final String PASSWORD_NAME = "spring.datasource.password";

	public static final String DRIVER_CLASS_NAME_NAME = "spring.datasource.driver-class-name";

	private final YamlProperties applicationYml;

	public SpringDataSource(YamlProperties applicationYml) {
		this.applicationYml = applicationYml;
	}

	public String getUrl() {
		return applicationYml.getProperty(URL_NAME);
	}

	public String getUsername() {
		return applicationYml.getProperty(USERNAME_NAME);
	}

	public String getPassword() {
		return applicationYml.getProperty(PASSWORD_NAME);
	}

	public String getDriverClassName() {
		return applicationYml.getProperty(DRIVER_CLASS_NAME_NAME);
	}

	public DataSourceProperties buildDataSourceProperties() {
		return new DataSourceProperties(getUrl(), getUsername(), getPassword(), getDriverClassName());
	}

	public Dialect buildDialect() {
		return DialectFactory.createByUrl(getUrl());
	}

}

package org.yelong.mybatis.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.yelong.core.model.ModelProperties;

@ConfigurationProperties(prefix = YelongModelProperties.PROPERTIES_PREFIX)
public class YelongModelProperties extends ModelProperties{

	public static final String PROPERTIES_PREFIX = "yelong";
	
}

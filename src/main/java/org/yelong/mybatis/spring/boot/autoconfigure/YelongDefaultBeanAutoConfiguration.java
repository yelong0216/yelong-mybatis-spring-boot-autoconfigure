/**
 * 
 */
package org.yelong.mybatis.spring.boot.autoconfigure;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.yelong.commons.lang.Strings;
import org.yelong.core.jdbc.dialect.Dialect;
import org.yelong.core.jdbc.dialect.Dialects;
import org.yelong.core.jdbc.sql.condition.support.ConditionResolver;
import org.yelong.core.jdbc.sql.condition.support.DefaultConditionResolver;
import org.yelong.core.model.ModelProperties;
import org.yelong.core.model.resolve.AnnotationModelResolver;
import org.yelong.core.model.resolve.ModelAndTableManager;
import org.yelong.core.model.resolve.ModelResolver;
import org.yelong.core.model.sql.DefaultModelSqlFragmentFactory;
import org.yelong.core.model.sql.DefaultSqlModelResolver;
import org.yelong.core.model.sql.ModelSqlFragmentFactory;
import org.yelong.core.model.sql.SqlModelResolver;

/**
 * @author PengFei
 * @since
 */
@Configuration
public class YelongDefaultBeanAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public Dialect dialect(Environment environment) {
		String databaseUrl = environment.getProperty("spring.datasource.url");
		Strings.requireNonBlank(databaseUrl, "不存在数据库配置");
		for (Dialects dialects : Dialects.values()) {
			if (databaseUrl.indexOf(":" + dialects.name().toLowerCase() + ":") != -1) {
				return dialects.getDialect();
			}
		}
		throw new NullPointerException("无效的数据库方言");
	}
	
	/**
	 * @return model 属性
	 */
	@Bean
	@ConditionalOnMissingBean
	public ModelProperties modelProperties() {
		return new ModelProperties();
	}
	
	/**
	 * @param modelProperties model 属性
	 * @return 模型解析器
	 */
	@Bean
	@ConditionalOnMissingBean
	public AnnotationModelResolver annotationModelResolver(ModelProperties modelProperties) {
		return new AnnotationModelResolver(modelProperties);
	}
	
	/**
	 * @return 模型与表的管理器
	 */
	@Bean
	@ConditionalOnMissingBean
	public ModelAndTableManager modelAndTableManager(List<ModelResolver> modelResolvers) {
		return new ModelAndTableManager(modelResolvers.get(0));
	}
	
	/**
	 * @param dialect 方言
	 * @param modelAndTableManager 模型与表管理者
	 * @return 模型sql片段工厂
	 */
	@Bean
	@ConditionalOnMissingBean
	public ModelSqlFragmentFactory modelSqlFragmentFactory(Dialect dialect,ModelAndTableManager modelAndTableManager) {
		return new DefaultModelSqlFragmentFactory(dialect, modelAndTableManager);
	}
	
	/**
	 * @param modelSqlFragmentFactory 模型sql片段工厂
	 * @return 条件解析器
	 */
	@Bean
	@ConditionalOnMissingBean
	public ConditionResolver conditionResolver(ModelSqlFragmentFactory modelSqlFragmentFactory) {
		return new DefaultConditionResolver(modelSqlFragmentFactory);
	}
	
	/**
	 * @param modelAndTableManager 模型与表管理者
	 * @param conditionResolver 条件解析器
	 * @return sqlModel 解析器
	 */
	@Bean
	@ConditionalOnMissingBean
	public SqlModelResolver sqlModelResolver(ModelAndTableManager modelAndTableManager,ConditionResolver conditionResolver) {
		return new DefaultSqlModelResolver(modelAndTableManager, conditionResolver);
	}
	
}
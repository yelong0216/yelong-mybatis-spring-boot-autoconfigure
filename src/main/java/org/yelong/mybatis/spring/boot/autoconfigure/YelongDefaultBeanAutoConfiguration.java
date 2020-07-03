/**
 * 
 */
package org.yelong.mybatis.spring.boot.autoconfigure;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.yelong.commons.lang.Strings;
import org.yelong.core.jdbc.dialect.Dialect;
import org.yelong.core.jdbc.dialect.Dialects;
import org.yelong.core.jdbc.sql.condition.support.ConditionResolver;
import org.yelong.core.jdbc.sql.condition.support.DefaultConditionResolver;
import org.yelong.core.model.property.DefaultModelProperty;
import org.yelong.core.model.property.ModelProperty;
import org.yelong.core.model.resolve.AnnotationModelResolver;
import org.yelong.core.model.resolve.ModelAndTableManager;
import org.yelong.core.model.resolve.ModelResolver;
import org.yelong.core.model.sql.DefaultModelSqlFragmentFactory;
import org.yelong.core.model.sql.DefaultSqlModelResolver;
import org.yelong.core.model.sql.ModelSqlFragmentFactory;
import org.yelong.core.model.sql.SqlModelResolver;
import org.yelong.mybatis.spring.MyBatisBaseDataBaseOperation;
import org.yelong.support.spring.ApplicationContextDecorator;

/**
 * 默认的配置
 * 
 * @author PengFei
 */
@Configuration
public class YelongDefaultBeanAutoConfiguration {

	/**
	 * @param environment 环境
	 * @return 方言
	 */
	@Bean
	@ConditionalOnMissingBean(Dialect.class)
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
	 * @return 模型解析器
	 */
	@Bean
	@ConditionalOnMissingBean(ModelResolver.class)
	public ModelResolver annotationModelResolver() {
		return new AnnotationModelResolver();
	}
	
	/**
	 * @return 模型与表的管理器
	 */
	@Bean
	@ConditionalOnMissingBean(ModelAndTableManager.class)
	public ModelAndTableManager modelAndTableManager(List<ModelResolver> modelResolvers) {
		return new ModelAndTableManager(modelResolvers.get(0));
	}
	
	/**
	 * @param dialect 方言
	 * @param modelAndTableManager 模型与表管理者
	 * @return 模型sql片段工厂
	 */
	@Bean
	@ConditionalOnMissingBean(ModelSqlFragmentFactory.class)
	public ModelSqlFragmentFactory modelSqlFragmentFactory(Dialect dialect,ModelAndTableManager modelAndTableManager) {
		return new DefaultModelSqlFragmentFactory(dialect.getSqlFragmentFactory(), modelAndTableManager);
	}
	
	/**
	 * @param modelSqlFragmentFactory 模型sql片段工厂
	 * @return 条件解析器
	 */
	@Bean
	@ConditionalOnMissingBean(ConditionResolver.class)
	public ConditionResolver conditionResolver(ModelSqlFragmentFactory modelSqlFragmentFactory) {
		return new DefaultConditionResolver(modelSqlFragmentFactory);
	}
	
	/**
	 * @param modelAndTableManager 模型与表管理者
	 * @param conditionResolver 条件解析器
	 * @return sqlModel 解析器
	 */
	@Bean
	@ConditionalOnMissingBean(SqlModelResolver.class)
	public SqlModelResolver sqlModelResolver(ModelAndTableManager modelAndTableManager,ConditionResolver conditionResolver,ModelProperty modelProperty) {
		DefaultSqlModelResolver sqlModelResolver = new DefaultSqlModelResolver(modelAndTableManager, conditionResolver);
		sqlModelResolver.setModelProperty(modelProperty);
		return sqlModelResolver;
	}
	
	/**
	 * @param sqlSession sql  session
	 * @return 数据库操作
	 */
	@Bean
	@ConditionalOnMissingBean(MyBatisBaseDataBaseOperation.class)
	public MyBatisBaseDataBaseOperation mybatisBaseDataBaseOperation(SqlSession sqlSession) {
		return new MyBatisBaseDataBaseOperation(sqlSession);
	}
	
	/**
	 * @return model 属性管理
	 */
	@Bean
	@ConditionalOnMissingBean(ModelProperty.class)
	public ModelProperty modelProperty() {
		return DefaultModelProperty.INSTANCE;
	}
	
	/**
	 * @return application context 装饰器
	 */
	@Bean
	@ConditionalOnMissingBean(ApplicationContextDecorator.class)
	public ApplicationContextDecorator ApplicationContextDecorator() {
		return new ApplicationContextDecorator();
	}
	
}

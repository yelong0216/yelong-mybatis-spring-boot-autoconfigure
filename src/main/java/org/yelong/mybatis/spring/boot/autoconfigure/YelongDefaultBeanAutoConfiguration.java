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
import org.yelong.core.jdbc.BaseDataBaseOperation;
import org.yelong.core.jdbc.dialect.Dialect;
import org.yelong.core.jdbc.dialect.Dialects;
import org.yelong.core.jdbc.record.DefaultRecordOperation;
import org.yelong.core.jdbc.record.RecordOperation;
import org.yelong.core.jdbc.sql.condition.support.ConditionResolver;
import org.yelong.core.jdbc.sql.condition.support.DefaultConditionResolver;
import org.yelong.core.jdbc.sql.ddl.DataDefinitionLanguage;
import org.yelong.core.jdbc.sql.function.DatabaseFunction;
import org.yelong.core.model.collector.ModelServiceCollectInterceptor;
import org.yelong.core.model.manage.DefaultModelManager;
import org.yelong.core.model.manage.ModelManager;
import org.yelong.core.model.map.MapModelFieldAndColumnGetStrategy;
import org.yelong.core.model.map.MapModelResolver;
import org.yelong.core.model.map.annotation.AnnotationMapModelResolver;
import org.yelong.core.model.map.support.DefaultMapModelFieldAndColumnGetStrategy;
import org.yelong.core.model.pojo.POJOModelResolver;
import org.yelong.core.model.pojo.annotation.AnnotationFieldResolver;
import org.yelong.core.model.pojo.annotation.AnnotationPOJOModelResolver;
import org.yelong.core.model.pojo.field.FieldResolver;
import org.yelong.core.model.property.DefaultModelProperty;
import org.yelong.core.model.property.ModelProperty;
import org.yelong.core.model.resolve.DefaultModelResolverManager;
import org.yelong.core.model.resolve.ModelResolver;
import org.yelong.core.model.resolve.ModelResolverManager;
import org.yelong.core.model.service.SqlModelService;
import org.yelong.core.model.service.function.MSFunctionInterceptor;
import org.yelong.core.model.sql.DefaultModelSqlFragmentFactory;
import org.yelong.core.model.sql.DefaultSqlModelResolver;
import org.yelong.core.model.sql.ModelSqlFragmentFactory;
import org.yelong.core.model.sql.SqlModelResolver;
import org.yelong.mybatis.spring.MyBatisBaseDataBaseOperation;
import org.yelong.support.spring.ApplicationContextDecorator;

/**
 * 默认的配置
 */
@Configuration
public class YelongDefaultBeanAutoConfiguration {

	// ==================================================数据库方言==================================================

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

	// ==================================================数据库操作==================================================

	@Bean
	@ConditionalOnMissingBean(MyBatisBaseDataBaseOperation.class)
	public MyBatisBaseDataBaseOperation mybatisBaseDataBaseOperation(SqlSession sqlSession) {
		return new MyBatisBaseDataBaseOperation(sqlSession);
	}

	@Bean
	@ConditionalOnMissingBean(DataDefinitionLanguage.class)
	public DataDefinitionLanguage dataDefinitionLanguage(Dialect dialect, BaseDataBaseOperation baseDataBaseOperation) {
		return dialect.createDataDefinitionLanguage(baseDataBaseOperation);
	}

	@Bean
	@ConditionalOnMissingBean(DatabaseFunction.class)
	public DatabaseFunction databaseFunction(Dialect dialect, BaseDataBaseOperation baseDataBaseOperation) {
		return dialect.createDatabaseFunction(baseDataBaseOperation);
	}

	@Bean
	@ConditionalOnMissingBean(RecordOperation.class)
	public RecordOperation recordOperation(BaseDataBaseOperation baseDataBaseOperation,Dialect dialect) {
		return new DefaultRecordOperation(baseDataBaseOperation, dialect);
	}
	
	// ==================================================模型管理==================================================

	@Bean
	@ConditionalOnMissingBean(MapModelFieldAndColumnGetStrategy.class)
	public MapModelFieldAndColumnGetStrategy mapModelFieldAndColumnGetStrategy(
			DataDefinitionLanguage dataDefinitionLanguage, DatabaseFunction dataBaseFunction) {
		return new DefaultMapModelFieldAndColumnGetStrategy(dataDefinitionLanguage, dataBaseFunction);
	}

	@Bean
	@ConditionalOnMissingBean(MapModelResolver.class)
	public MapModelResolver mapModelResolver(MapModelFieldAndColumnGetStrategy mapModelFieldAndColumnGetStrategy) {
		return new AnnotationMapModelResolver(mapModelFieldAndColumnGetStrategy);
	}

	@Bean
	public FieldResolver defaultFieldResolver() {
		return new AnnotationFieldResolver();
	}

	@Bean
	@ConditionalOnMissingBean(value = ModelResolver.class, ignored = MapModelResolver.class)
	public ModelResolver modelResolver(List<FieldResolver> fieldResolvers) {
		POJOModelResolver configurableFieldResolverModelResolver = new AnnotationPOJOModelResolver();
		fieldResolvers.forEach(configurableFieldResolverModelResolver::registerFieldResovler);
		return configurableFieldResolverModelResolver;
	}

	@Bean
	@ConditionalOnMissingBean(ModelResolverManager.class)
	public ModelResolverManager modelResolverManager(List<ModelResolver> modelResolvers) {
		DefaultModelResolverManager modelResolverManager = new DefaultModelResolverManager();
		modelResolvers.forEach(modelResolverManager::registerModelResolver);
		return modelResolverManager;
	}

	@Bean
	@ConditionalOnMissingBean(ModelManager.class)
	public ModelManager modelManager(ModelResolverManager modelResolverManager) {
		return new DefaultModelManager(modelResolverManager);
	}

	@Bean
	@ConditionalOnMissingBean(ModelSqlFragmentFactory.class)
	public ModelSqlFragmentFactory modelSqlFragmentFactory(Dialect dialect, ModelManager modelManager) {
		return new DefaultModelSqlFragmentFactory(dialect.getSqlFragmentFactory(), modelManager);
	}

	@Bean
	@ConditionalOnMissingBean(ConditionResolver.class)
	public ConditionResolver conditionResolver(ModelSqlFragmentFactory modelSqlFragmentFactory) {
		return new DefaultConditionResolver(modelSqlFragmentFactory);
	}

	@Bean
	@ConditionalOnMissingBean(SqlModelResolver.class)
	public SqlModelResolver sqlModelResolver(ModelManager modelManager, ConditionResolver conditionResolver,
			ModelProperty modelProperty) {
		DefaultSqlModelResolver sqlModelResolver = new DefaultSqlModelResolver(modelManager, conditionResolver,
				conditionResolver.getSqlFragmentFactory(), modelProperty);
		return sqlModelResolver;
	}

	@Bean
	@ConditionalOnMissingBean(ModelProperty.class)
	public ModelProperty modelProperty() {
		return DefaultModelProperty.INSTANCE;
	}

	// ==================================================ModelServiceInterceptor==================================================

	/**
	 * @return 模型服务收集器拦截器
	 * @since 2.1.4
	 */
	@Bean
	@ConditionalOnMissingBean(ModelServiceCollectInterceptor.class)
	public ModelServiceCollectInterceptor modelServiceCollectInterceptor() {
		return new ModelServiceCollectInterceptor() {
			@Override
			protected SqlModelService getInterceptorWrapAfterSqlModelService() {
				return ApplicationContextDecorator.getBean(SqlModelService.class);
			}
		};
	}

	/**
	 * @return 模型服务函数拦截器
	 * @since 2.1.4
	 */
	@Bean
	@ConditionalOnMissingBean(MSFunctionInterceptor.class)
	public MSFunctionInterceptor msFunctionInterceptor() {
		return new MSFunctionInterceptor() {
			@Override
			protected SqlModelService getInterceptorWrapAfterSqlModelService() {
				return ApplicationContextDecorator.getBean(SqlModelService.class);
			}
		};
	}

	// ==================================================SpringApplicationContext==================================================

	/**
	 * @return ApplicationContext装饰器
	 */
	@Bean
	@ConditionalOnMissingBean(ApplicationContextDecorator.class)
	public ApplicationContextDecorator ApplicationContextDecorator() {
		return new ApplicationContextDecorator();
	}

}

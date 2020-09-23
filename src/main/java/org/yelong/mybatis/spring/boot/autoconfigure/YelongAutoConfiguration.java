/**
 * 
 */
package org.yelong.mybatis.spring.boot.autoconfigure;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.yelong.core.interceptor.Interceptor;
import org.yelong.core.interceptor.InterceptorChain;
import org.yelong.core.jdbc.BaseDataBaseOperation;
import org.yelong.core.jdbc.dialect.Dialect;
import org.yelong.core.jdbc.sql.condition.support.ConditionResolver;
import org.yelong.core.model.ModelConfiguration;
import org.yelong.core.model.ModelConfigurationBuilder;
import org.yelong.core.model.ModelProperties;
import org.yelong.core.model.manage.ModelManager;
import org.yelong.core.model.property.ModelProperty;
import org.yelong.core.model.service.ModelService;
import org.yelong.core.model.service.ModelServiceInterceptor;
import org.yelong.core.model.service.SqlModelService;
import org.yelong.core.model.sql.ModelSqlFragmentFactory;
import org.yelong.core.model.sql.SqlModelResolver;
import org.yelong.mybatis.spring.MyBatisBaseDataBaseOperation;
import org.yelong.mybatis.spring.MyBatisModelService;

/**
 * @since 2.0
 */
@org.springframework.context.annotation.Configuration
@ConditionalOnClass({ MybatisAutoConfiguration.class, ModelConfiguration.class, ModelService.class,
		BaseDataBaseOperation.class })
@ConditionalOnSingleCandidate(SqlSession.class)
@EnableConfigurationProperties(YelongModelProperties.class)
@AutoConfigureAfter({ MybatisAutoConfiguration.class })
public class YelongAutoConfiguration {

	private Dialect dialect;

	private ModelProperties modelProperties;

	private ModelManager modelManager;

	private ModelProperty modelProperty;

	private ModelSqlFragmentFactory modelSqlFragmentFactory;

	private ConditionResolver conditionResolver;

	private SqlModelResolver sqlModelResolver;

	public YelongAutoConfiguration(ObjectProvider<Dialect> dialectProvider,
			ObjectProvider<ModelProperties> modelPropertiesProvider, ObjectProvider<ModelManager> modelManagerProvider,
			ObjectProvider<ModelSqlFragmentFactory> modelSqlFragmentFactoryProvider,
			ObjectProvider<ConditionResolver> conditionResolverProvider,
			ObjectProvider<SqlModelResolver> sqlModelResolverProvider,
			ObjectProvider<ModelProperty> modelPropertyProvider) {
		this.dialect = dialectProvider.getIfAvailable();
		this.modelProperties = modelPropertiesProvider.getIfAvailable();
		this.modelProperty = modelPropertyProvider.getIfAvailable();
		this.modelManager = modelManagerProvider.getIfAvailable();
		this.modelSqlFragmentFactory = modelSqlFragmentFactoryProvider.getIfAvailable();
		this.conditionResolver = conditionResolverProvider.getIfAvailable();
		this.sqlModelResolver = sqlModelResolverProvider.getIfAvailable();
	}

	@Bean
	@ConditionalOnMissingBean(ModelConfiguration.class)
	public ModelConfiguration modelConfiguration() {
		Assert.notNull(dialect, "未发现或识别失败的数据库方言");
		ModelConfigurationBuilder modelConfigurationBuilder = ModelConfigurationBuilder.create(dialect);
		modelConfigurationBuilder.setModelProperties(modelProperties);
		modelConfigurationBuilder.setConditionResolver(conditionResolver);
		modelConfigurationBuilder.setModelManager(modelManager);
		modelConfigurationBuilder.setModelSqlFragmentFactory(modelSqlFragmentFactory);
		modelConfigurationBuilder.setSqlModelResolver(sqlModelResolver);
		modelConfigurationBuilder.setModelProperty(modelProperty);
		return modelConfigurationBuilder.build();
	}

	@Bean("sourceModelService")
	@ConditionalOnMissingBean(SqlModelService.class)
	public SqlModelService modelService(ModelConfiguration modelConfiguration,
			MyBatisBaseDataBaseOperation myBatisBaseDataBaseOperation) {
		return new MyBatisModelService(modelConfiguration, myBatisBaseDataBaseOperation);
	}

	@Bean("modelService")
	@Primary
	@Transactional
	@ConditionalOnBean(Interceptor.class)
	@ConditionalOnSingleCandidate(ModelService.class)
	@SuppressWarnings("unchecked")
	@ConditionalOnProperty(prefix = YelongModelProperties.PROPERTIES_PREFIX, name = "modelServiceProxy", havingValue = "true", matchIfMissing = false)
	public <T extends ModelService> T modelServiceProxy(T modelService,
			ObjectProvider<List<ModelServiceInterceptor>> moduleServiceInterceptorsProvider) {
		List<ModelServiceInterceptor> interceptors = moduleServiceInterceptorsProvider.getIfAvailable();
		InterceptorChain interceptorChain = new InterceptorChain();
		interceptorChain.addInterceptor(interceptors);
		Class<?> targetClass = AopUtils.getTargetClass(modelService);
		if (null == targetClass) {
			return (T) interceptorChain.pluginAll(modelService);
		} else {
			return (T) interceptorChain.pluginAll(modelService, targetClass.getInterfaces());
		}
	}

}

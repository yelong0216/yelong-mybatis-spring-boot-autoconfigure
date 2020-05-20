/**
 * 
 */
package org.yelong.mybatis.spring.boot.autoconfigure;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;
import org.yelong.core.jdbc.BaseDataBaseOperation;
import org.yelong.core.jdbc.dialect.Dialect;
import org.yelong.core.jdbc.sql.condition.support.ConditionResolver;
import org.yelong.core.model.ModelConfiguration;
import org.yelong.core.model.ModelConfigurationBuilder;
import org.yelong.core.model.property.ModelProperty;
import org.yelong.core.model.resolve.ModelAndTableManager;
import org.yelong.core.model.service.ModelService;
import org.yelong.core.model.service.SqlModelService;
import org.yelong.core.model.sql.ModelSqlFragmentFactory;
import org.yelong.core.model.sql.SqlModelResolver;
import org.yelong.mybatis.spring.MyBatisBaseDataBaseOperation;
import org.yelong.mybatis.spring.MyBatisModelService;

/**
 * @author PengFei
 * @since
 */
@org.springframework.context.annotation.Configuration
@ConditionalOnClass({ MybatisAutoConfiguration.class,ModelConfiguration.class,ModelService.class,BaseDataBaseOperation.class })
@ConditionalOnSingleCandidate(SqlSession.class)
@AutoConfigureAfter({ MybatisAutoConfiguration.class,YelongDefaultBeanAutoConfiguration.class })
public class YelongMybatisSpringAutoConfiguration {

	private Dialect dialect;

	private ModelProperty modelProperty;
	
	private ModelAndTableManager modelAndTableManager;

	private ModelSqlFragmentFactory modelSqlFragmentFactory;

	private ConditionResolver conditionResolver;

	private SqlModelResolver sqlModelResolver;

	public YelongMybatisSpringAutoConfiguration(ObjectProvider<Dialect> dialectProvider,
			ObjectProvider<ModelAndTableManager> modelAndTableManagerProvider,
			ObjectProvider<ModelSqlFragmentFactory> modelSqlFragmentFactoryProvider,
			ObjectProvider<ConditionResolver> conditionResolverProvider,
			ObjectProvider<SqlModelResolver> sqlModelResolverProvider,
			ObjectProvider<ModelProperty> modelPropertyProvider) {
		this.dialect = dialectProvider.getIfAvailable();
		this.modelProperty = modelPropertyProvider.getIfAvailable();
		this.modelAndTableManager = modelAndTableManagerProvider.getIfAvailable();
		this.modelSqlFragmentFactory = modelSqlFragmentFactoryProvider.getIfAvailable();
		this.conditionResolver = conditionResolverProvider.getIfAvailable();
		this.sqlModelResolver = sqlModelResolverProvider.getIfAvailable();
	}

	@Bean
	@ConditionalOnMissingBean(ModelConfiguration.class)
	public ModelConfiguration modelConfiguration() {
		Assert.notNull(dialect,"未发现或识别失败的数据库方言");
		ModelConfigurationBuilder modelConfigurationBuilder = new ModelConfigurationBuilder();
		modelConfigurationBuilder.setDialect(dialect);
		modelConfigurationBuilder.setConditionResolver(conditionResolver);
		modelConfigurationBuilder.setModelAndTableManager(modelAndTableManager);
		modelConfigurationBuilder.setModelSqlFragmentFactory(modelSqlFragmentFactory);
		modelConfigurationBuilder.setSqlModelResolver(sqlModelResolver);
		modelConfigurationBuilder.setModelProperty(modelProperty);
		return modelConfigurationBuilder.build();
	}
	
	@Bean
	@ConditionalOnMissingBean(SqlModelService.class)
	public SqlModelService modelService(ModelConfiguration modelConfiguration,MyBatisBaseDataBaseOperation myBatisBaseDataBaseOperation) {
		return new MyBatisModelService(modelConfiguration,myBatisBaseDataBaseOperation);
	}
	
}

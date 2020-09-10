/**
 * 
 */
package org.yelong.spring.boot.test;

import java.io.FileNotFoundException;
import java.util.Arrays;

import org.yelong.commons.util.ResourcesUtils;
import org.yelong.core.jdbc.BaseDataBaseOperation;
import org.yelong.core.jdbc.DataBaseOperationType;
import org.yelong.core.jdbc.DataSourceProperties;
import org.yelong.core.jdbc.DefaultBaseDataBaseOperation;
import org.yelong.core.model.ModelConfiguration;
import org.yelong.core.model.ModelConfigurationBuilder;
import org.yelong.core.model.convertor.CaseInsensitiveModelAndMapConvertor;
import org.yelong.core.model.convertor.ModelAndMapConvertor;
import org.yelong.core.model.service.JdbcModelService;
import org.yelong.core.model.service.SqlModelService;
import org.yelong.support.yaml.YamlProperties;
import org.yelong.support.yaml.YamlWrapper;

/**
 * @since 2.0.1
 */
public class YelongTestBeanFactory {

	private YamlWrapper yamlWrapper = new YamlWrapper();

	private YamlProperties applicationYml;

	private boolean debug = true;

	private SpringDataSource springDataSource;

	public static final ModelAndMapConvertor DEFAULT_MODEL_AND_MAP_CONVERTOR = new CaseInsensitiveModelAndMapConvertor();

	public YelongTestBeanFactory() {
		this("application.yml");
	}

	public YelongTestBeanFactory(String properties) {
		try {
			applicationYml = yamlWrapper.load(ResourcesUtils.getResourceAsStream(properties));
			springDataSource = new SpringDataSource(applicationYml);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public BaseDataBaseOperation buildBaseDataBaseOperation() {
		DataSourceProperties dataSourceProperties = buildDataSourceProperties();
		try {
			if (debug) {
				return new DefaultBaseDataBaseOperation(dataSourceProperties) {
					@Override
					public Object execute(String sql, Object[] params, DataBaseOperationType operationType) {
						System.out.println("sql:" + sql);
						System.out.println("params:" + Arrays.asList(params));
						return super.execute(sql, params, operationType);
					}
				};
			} else {
				return new DefaultBaseDataBaseOperation(dataSourceProperties);
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public DataSourceProperties buildDataSourceProperties() {
		return springDataSource.buildDataSourceProperties();
	}

	public SqlModelService buildModelService() {
		return new JdbcModelService(buildModelConfiguration(), buildBaseDataBaseOperation(),
				DEFAULT_MODEL_AND_MAP_CONVERTOR);
	}

	public ModelConfiguration buildModelConfiguration() {
		ModelConfigurationBuilder modelConfigurationBuilder = ModelConfigurationBuilder
				.create(springDataSource.buildDialect());
		return modelConfigurationBuilder.build();
	}

}

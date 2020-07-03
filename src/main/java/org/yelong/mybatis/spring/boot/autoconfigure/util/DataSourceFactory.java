/**
 * 
 */
package org.yelong.mybatis.spring.boot.autoconfigure.util;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.yelong.core.jdbc.DataSourceProperties;
import org.yelong.support.orm.DataSourcePropertiesFactory;

/**
 * 数据源建造者工厂
 * 
 * @author PengFei
 */
public class DataSourceFactory {

	/**
	 * 创建数据源
	 * 
	 * @param <D>            dataSource type
	 * @param configLocation 数据库属性配置文件
	 * @return 数据源
	 * @see @see DataSourcePropertiesFactory#create(String)
	 * @throws Exception create exception
	 */
	public static <D extends DataSource> D create(String configLocation) throws Exception {
		return create(configLocation, DataSourcePropertiesFactory.DEFAULT_DATASOURCE_PREFIX);
	}

	/**
	 * 创建数据源
	 * 
	 * @param <D>            dataSource type
	 * @param configLocation 数据库属性配置文件
	 * @param prefix         数据库配置前缀
	 * @see DataSourcePropertiesFactory#create(String, String)
	 * @return 数据源
	 * @throws Exception create exception
	 */
	@SuppressWarnings("unchecked")
	public static <D extends DataSource> D create(String configLocation, String prefix) throws Exception {
		DataSourceProperties dataSourceProperties = DataSourcePropertiesFactory.create(configLocation, prefix);
		DataSourceBuilder<D> dataSourceBuilder = (DataSourceBuilder<D>) DataSourceBuilder.create();
		dataSourceBuilder.url(dataSourceProperties.getUrl());
		dataSourceBuilder.username(dataSourceProperties.getUsername());
		dataSourceBuilder.password(dataSourceProperties.getPassword());
		dataSourceBuilder.driverClassName(dataSourceProperties.getDriverClassName());
		return dataSourceBuilder.build();
	}

}

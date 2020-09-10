package org.yelong.spring.boot.test;

import org.yelong.core.model.service.SqlModelService;

/**
 * @since 2.0.1
 */
public class YelongTestBeanFactorys {

	public static SqlModelService buildModelService() {
		return new YelongTestBeanFactory().buildModelService();
	}

}

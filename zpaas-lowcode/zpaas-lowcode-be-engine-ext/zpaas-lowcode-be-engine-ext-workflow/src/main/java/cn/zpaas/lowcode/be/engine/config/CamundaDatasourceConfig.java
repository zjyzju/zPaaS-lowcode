package cn.zpaas.lowcode.be.engine.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author zjy
 *
 */
@Configuration
public class CamundaDatasourceConfig {
	@Bean(name = "camundaBpmDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.camunda")
	public DataSource getCamundaDataSource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean(name="camundaBpmTransactionManager")
    public PlatformTransactionManager camundaTransactionManager(@Qualifier("camundaBpmDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

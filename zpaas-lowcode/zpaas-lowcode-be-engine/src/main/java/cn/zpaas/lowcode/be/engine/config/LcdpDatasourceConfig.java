package cn.zpaas.lowcode.be.engine.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author zjy
 *
 */
@Configuration
@MapperScan(basePackages = "cn.zpaas.lowcode.domain.mapper", sqlSessionFactoryRef = "lcdpEngineSqlSessionFactory")
public class LcdpDatasourceConfig {
	@Bean(name = "lcdpEngineDataSource")
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource.lcdp-engine")
	public DataSource getLcdpEngineDataSource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean(name = "lcdpEngineSqlSessionFactory")
	public SqlSessionFactory lcdpEngineSqlSessionFactory(@Qualifier("lcdpEngineDataSource") DataSource dataSource) throws Exception{
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:com/zjy/study/lowcode/mapper/*.xml"));
		return bean.getObject();
	}
	
	@Bean(name = "lcdpEngineSqlSessionTemplate")
	public SqlSessionTemplate lcdpEngineSqlSessionTemplate(@Qualifier("lcdpEngineSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
	@Bean(name = "lcdpEngineJdbcTemplate")
	public JdbcTemplate lcdpJdbcTemplate(@Qualifier("lcdpEngineDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean(name="lcdpEngineTransactionManager")
	@Primary
    public PlatformTransactionManager lcdpEngineTransactionManager(@Qualifier("lcdpEngineDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

package cn.zpaas.lowcode;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author zjy
 *
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableTransactionManagement
@ServletComponentScan("cn.zpaas.lowcode.be.ide.filter")
@ComponentScan("cn.zpaas")
public class LowCodePlatformStarter {
	private static Logger logger = LoggerFactory.getLogger(LowCodePlatformStarter.class);

	public static void main(String[] args) {
		logger.info("Starting lowcode platform...");
		SpringApplication.run(LowCodePlatformStarter.class, args);
	}
}



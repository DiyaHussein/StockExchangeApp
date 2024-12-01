package upt.cebp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SpringstockexchangeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringstockexchangeApplication.class, args);
    }
}
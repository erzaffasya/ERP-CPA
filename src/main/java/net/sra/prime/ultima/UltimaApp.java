/*
 * Copyright 2017 JoinFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sra.prime.ultima;

//import com.zaxxer.hikari.HikariDataSource;
//import org.apache.tomcat.jdbc.pool.DataSource;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import io.undertow.Undertow.Builder;
import io.undertow.UndertowOptions;

/**
 *
 * @author Syamsu
 */
@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan("net.sra.prime.ultima.db.mapper") // Folder dimana Mapper MyBatis ditemukan
public class UltimaApp {
    
    private final Logger logger = LoggerFactory.getLogger(UltimaApp.class);
    
    private static DataSource ds;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        if (ds == null) {
            ds = DataSourceBuilder.create().build();
            Runtime.getRuntime()
                    .addShutdownHook(new Thread() {
                        @Override
                        public void run() {
                            ((org.apache.tomcat.jdbc.pool.DataSource) ds).close();
                        }
                    });
        }
        return ds;
    }
    
    void logDS() {
        if (ds instanceof org.apache.tomcat.jdbc.pool.DataSource) {
            org.apache.tomcat.jdbc.pool.DataSource hs = ((org.apache.tomcat.jdbc.pool.DataSource) ds);

            //logger.warn("spring.datasource.poolName = " + hs.getPoolName());
            logger.warn("spring.datasource.validationQuery = " + hs.getValidationQuery());
            logger.warn("spring.datasource.initialSize = " + hs.getInitialSize());
            logger.warn("spring.datasource.maxActive = " + hs.getMaxActive());
            logger.warn("spring.datasource.maxIdle = " + hs.getMaxIdle());
            logger.warn("spring.datasource.maxWait = " + hs.getMaxWait());
            logger.warn("spring.datasource.minIdle = " + hs.getMinIdle());
            
//            logger.warn("spring.datasource.connectionTestQuery = " + hs.getConnectionTestQuery());
//           logger.warn("spring.datasource.maximumPoolSize = " + hs.getMaximumPoolSize());
//            logger.warn("spring.datasource.minimumIdle = " + hs.getMinimumIdle());
//            logger.warn("spring.datasource.maxLifetime = " + hs.getMaxLifetime());
//            logger.warn("spring.datasource.connectionTimeout = " + hs.getConnectionTimeout());
//            logger.warn("spring.datasource.idleTimeout = " + hs.getIdleTimeout());
//            logger.warn("spring.datasource.validationTimeout = " + hs.getValidationTimeout());
//            logger.warn("spring.datasource.leakDetectionThreshold = " + hs.getLeakDetectionThreshold());
        } else {
            logger.error("Gawatt bukan hikari euy..");
        }
    }

    //PlatformTransactionManager
    
    @Bean
    public DataSourceTransactionManager transactionManager() {
        DataSourceTransactionManager bean = new DataSourceTransactionManager();
        bean.setDataSource(dataSource());
        logDS();
        return bean;
    }

    @Bean
    public ManagedTransactionFactory managedTransactionFactory() {
        return new ManagedTransactionFactory();
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        UndertowEmbeddedServletContainerFactory underTow = new UndertowEmbeddedServletContainerFactory();
        underTow.addBuilderCustomizers((UndertowBuilderCustomizer) (Builder builder) -> {
            builder.setServerOption(UndertowOptions.MAX_PARAMETERS, 10000);
        });
        underTow.setSessionTimeout(3, TimeUnit.HOURS);
        return underTow;
    }

    // ini dari web.xml yang <filter>
    @Bean
    public FilterRegistrationBean greetingFilterRegistrationBean() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new net.sra.prime.ultima.primefaces.ultima.filter.CharacterEncodingFilter());
        return bean;
    }

}

//    @Bean
//    public PlatformTransactionManager platformTransactionManager(){ 
//        return new JtaTransactionManager();
//    }
//    @Bean
//    public SqlSessionFactory sqlSessionFactory() throws Exception {
//        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
//        bean.setDataSource(dataSource());
//        bean.setTransactionFactory(managedTransactionFactory());
//        return bean.getObject();
//    }
//    @Bean
//    public EmbeddedServletContainerFactory servletContainer() {
//        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
//        tomcat.setSessionTimeout(12, TimeUnit.HOURS);
//
//        return tomcat;
//    }

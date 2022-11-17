package cn.htht.service.platform.portal.component.config;

import cn.htht.service.platform.portal.component.PageInterceptor;
import cn.htht.service.platform.portal.utils.utils.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName MybatisConfigurer
 * @Description Mybatis配置
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Configuration
@PropertySource("classpath:application.yml")
public class MybatisConfigurer implements EnvironmentAware {
    @Autowired
    private Environment env;

    @Bean
    public SqlSessionFactory sqlSessionFactoryBean(DataSource dataSource) throws Exception {
        String typeAliasesPackage = env.getProperty("mybatis.typeAliasesPackage");
        String mapperLocations = env.getProperty("mybatis.mapperLocations");
        String configLocation = env.getProperty("mybatis.configLocation");
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTypeAliasesPackage(typeAliasesPackage);
        // 添加XML目录
        factory.setMapperLocations(resolveMapperLocations(StringUtils.split(mapperLocations, ",")));
        factory.setConfigLocation(new DefaultResourceLoader().getResource(configLocation));

        PageInterceptor pageInterceptor = new PageInterceptor();
//        pageInterceptor.setProperties(properties.getProperties());
        //  添加插件
        factory.setPlugins(pageInterceptor);

        return factory.getObject();
    }

    public Resource[] resolveMapperLocations(String[] mapperLocations) {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = new ArrayList<Resource>();
        if (mapperLocations != null) {
            for (String mapperLocation : mapperLocations) {
                try {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation);
                    resources.addAll(Arrays.asList(mappers));
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
        mapperScannerConfigurer.setBasePackage("cn.htht.service.platform.portal.*.mapper");
        return mapperScannerConfigurer;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}

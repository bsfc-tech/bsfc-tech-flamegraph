package tech.bsfc.springboot.flamegraph;

import tech.bsfc.springboot.flamegraph.core.FlameGraphService;
import tech.bsfc.springboot.flamegraph.web.FlameGraphController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 火焰图自动配置类
 *
 * 用于自动配置火焰图相关的组件，包括火焰图服务和控制器。
 * 通过配置属性控制是否启用火焰图功能。
 *
 * @author bsfc.tech
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(FlameGraphProperties.class)
@ConditionalOnProperty(prefix = "flamegraph", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FlameGraphAutoConfiguration {

    @Bean(initMethod = "start")
    public FlameGraphService flameGraphService(FlameGraphProperties properties) {
        return new FlameGraphService(properties);
    }

    @Bean
    public FlameGraphController flameGraphController(FlameGraphService service) {
        return new FlameGraphController(service);
    }
}
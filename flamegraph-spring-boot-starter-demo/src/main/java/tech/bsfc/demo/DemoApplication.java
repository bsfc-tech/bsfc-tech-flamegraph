package tech.bsfc.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 火焰图演示应用启动类
 *
 * 用于启动火焰图演示应用，
 * 展示火焰图功能的基本用法。
 *
 * @author bsfc.tech
 * @version 1.0
 */
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
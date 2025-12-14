package tech.bsfc.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Random;

/**
 * 模拟控制器类
 *
 * 用于模拟CPU密集型任务，
 * 便于测试火焰图功能。
 *
 * @author bsfc.tech
 * @version 1.0
 */
@RestController
public class SimulationController {

    // 模拟 CPU 密集型任务
    @GetMapping("/work")
    public String heavyWork() {
        long start = System.currentTimeMillis();
        // 运行 500ms 的空循环计算
        while (System.currentTimeMillis() - start < 500) {
            Math.tan(new Random().nextDouble());
            subMethodA();
        }
        return "Work Done";
    }

    private void subMethodA() {
        if (new Random().nextInt(100) > 50) {
            subMethodB();
        }
    }

    private void subMethodB() {
        Math.sin(new Random().nextDouble());
    }
}
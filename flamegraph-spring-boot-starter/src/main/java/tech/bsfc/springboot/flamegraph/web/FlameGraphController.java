package tech.bsfc.springboot.flamegraph.web;

import tech.bsfc.springboot.flamegraph.core.FlameGraphService;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;

/**
 * 火焰图控制器类
 *
 * 提供火焰图相关的REST API接口，
 * 包括获取数据、重置数据、启动和停止采样等功能。
 *
 * @author bsfc.tech
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/flamegraph")
public class FlameGraphController {

    private final FlameGraphService service;

    public FlameGraphController(FlameGraphService service) {
        this.service = service;
    }

    @GetMapping(produces = "text/plain")
    public String getData() {
        return service.dumpData();
    }

    @PostMapping("/reset")
    public String reset() {
        service.reset();
        return "ok";
    }

    // --- 新增接口 ---

    @PostMapping("/start")
    public String startSampling() {
        service.startSampling();
        return "started";
    }

    @PostMapping("/stop")
    public String stopSampling() {
        service.stopSampling();
        return "stopped";
    }

    @GetMapping("/status")
    public Map<String, Boolean> getStatus() {
        return Collections.singletonMap("sampling", service.isSampling());
    }
}
package tech.bsfc.springboot.flamegraph.core;

import tech.bsfc.springboot.flamegraph.FlameGraphProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

/**
 * 火焰图服务类
 *
 * 负责火焰图数据的采集、存储和管理。
 * 提供启动、停止采样以及重置数据等功能。
 *
 * @author bsfc.tech
 * @version 1.0
 */
public class FlameGraphService {
    private static final Logger log = LoggerFactory.getLogger(FlameGraphService.class);
    private static final String SAMPLER_THREAD_NAME = "bsfc-flamegraph-sampler";

    private final FlameGraphProperties properties;
    private final ConcurrentHashMap<String, LongAdder> stackRepository = new ConcurrentHashMap<>();
    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private ScheduledExecutorService executor;

    private volatile boolean sampling = false;

    public FlameGraphService(FlameGraphProperties properties) {
        this.properties = properties;
    }

    public synchronized void start() {
        if (!properties.isEnabled() || executor != null) return;

        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, SAMPLER_THREAD_NAME);
            t.setDaemon(true);
            return t;
        });

        executor.scheduleAtFixedRate(this::sample,
                1000, properties.getSampleInterval(), TimeUnit.MILLISECONDS);

        log.info("BSFC FlameGraph sampler initialized. Ready to start.");
    }

    private void sample() {
        if (!sampling) return;

        try {
            // 获取所有线程 (不获取 Monitor/Synchronizer 以减少开销)
            ThreadInfo[] threads = threadMXBean.dumpAllThreads(false, false);
            boolean captured = false;

            for (ThreadInfo t : threads) {
                Thread.State state = t.getThreadState();

                // 核心修正：除了 RUNNABLE (CPU)，也捕获 BLOCKED (锁等待)
                // 注意：一般不捕获 WAITING/TIMED_WAITING，否则空闲时火焰图全是 sleep
                if (state != Thread.State.RUNNABLE && state != Thread.State.BLOCKED) {
                    continue;
                }

                // 排除采样线程自身
                if (SAMPLER_THREAD_NAME.equals(t.getThreadName())) continue;

                StackTraceElement[] stack = t.getStackTrace();
                if (stack == null || stack.length == 0) continue;

                String signature = collapseStack(stack);

                // 内存保护
                if (stackRepository.size() >= properties.getMaxStoredStacks()
                        && !stackRepository.containsKey(signature)) {
                    continue;
                }

                stackRepository.computeIfAbsent(signature, k -> new LongAdder()).increment();
                captured = true;
            }

            // 简单的心跳日志（仅在 Debug 开启且确实捕获到数据时打印，避免刷屏）
            if (captured && log.isDebugEnabled()) {
                log.debug("Captured stacks in this cycle.");
            }

        } catch (Exception e) {
            log.error("Sampling error", e);
        }
    }

    public void startSampling() {
        this.sampling = true;
        log.info("FlameGraph sampling STARTED.");
    }

    public void stopSampling() {
        this.sampling = false;
        log.info("FlameGraph sampling STOPPED. Total unique stacks: {}", stackRepository.size());
    }

    public boolean isSampling() {
        return this.sampling;
    }

    private String collapseStack(StackTraceElement[] stack) {
        StringBuilder sb = new StringBuilder();
        int depth = Math.min(stack.length, properties.getMaxDepth());
        // 倒序：栈底 -> 栈顶
        for (int i = depth - 1; i >= 0; i--) {
            StackTraceElement e = stack[i];
            sb.append(e.getClassName()).append(".").append(e.getMethodName());
            if (i > 0) sb.append(";");
        }
        return sb.toString();
    }

    public String dumpData() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, LongAdder> entry : stackRepository.entrySet()) {
            sb.append(entry.getKey()).append(" ").append(entry.getValue().sum()).append("\n");
        }
        return sb.toString();
    }

    public void reset() {
        stackRepository.clear();
        log.info("FlameGraph data cleared.");
    }
}
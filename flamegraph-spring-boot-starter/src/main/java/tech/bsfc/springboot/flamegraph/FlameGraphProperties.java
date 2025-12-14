package tech.bsfc.springboot.flamegraph;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 火焰图配置属性类
 *
 * 用于定义和管理火焰图相关的配置参数，
 * 包括开关、采样间隔、堆栈深度等设置。
 *
 * @author bsfc.tech
 * @version 1.0
 */
@ConfigurationProperties(prefix = "flamegraph")
public class FlameGraphProperties {
    /** 开关 */
    private boolean enabled = true;
    /** 采样间隔(ms)，建议 >= 20ms */
    private long sampleInterval = 50;
    /** 堆栈最大深度 */
    private int maxDepth = 100;
    /** 内存保护：最大允许存储的堆栈路径数量 */
    private int maxStoredStacks = 10000;

    // Getters and Setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public long getSampleInterval() { return sampleInterval; }
    public void setSampleInterval(long sampleInterval) { this.sampleInterval = sampleInterval; }
    public int getMaxDepth() { return maxDepth; }
    public void setMaxDepth(int maxDepth) { this.maxDepth = maxDepth; }
    public int getMaxStoredStacks() { return maxStoredStacks; }
    public void setMaxStoredStacks(int maxStoredStacks) { this.maxStoredStacks = maxStoredStacks; }
}
package com.xiepuhuan.reptile.downloader.constants;

/**
 * @author xiepuhuan
 */
public enum SelectionStrategyEnum {

    /**
     * 随机从代理池中选择一个代理
     */
    RANDOM("random"),
    /**
     * 按照添加顺序从代理池中选择一个代理
     */
    ORDER("order");

    private String name;

    SelectionStrategyEnum(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }
}

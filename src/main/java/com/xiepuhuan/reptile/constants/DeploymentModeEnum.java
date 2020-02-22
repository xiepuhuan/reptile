package com.xiepuhuan.reptile.constants;

/**
 * 部署模式
 * @author xiepuhuan
 */
public enum DeploymentModeEnum {

    /**
     * 随机从代理池中选择一个代理
     */
    SINGLE("single"),
    /**
     * 按照添加顺序从代理池中选择一个代理
     */
    Distributed("distributed");

    private String name;

    DeploymentModeEnum(String name) {
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

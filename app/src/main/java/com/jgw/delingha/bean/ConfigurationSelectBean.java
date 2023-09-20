package com.jgw.delingha.bean;

public class ConfigurationSelectBean {

    public long configId;
    public boolean selected;
    public String dataTime;
    public String productName;
    public String wareHouseName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurationSelectBean that = (ConfigurationSelectBean) o;
        return configId == that.configId;
    }

}

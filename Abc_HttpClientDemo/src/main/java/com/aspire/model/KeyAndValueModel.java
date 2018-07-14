package com.aspire.model;

/**
 * 键值对模型，用于接收键值对数据
 *
 * @author JustryDeng
 * @date 2018年7月14日 上午2:16:53
 */
public class KeyAndValueModel {

    /** 键 */
    private String key;
    
    /** 值 */
    private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/* 
	 * 重写toString()
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "key = " + key + ",value = " + value;
	}
    
    
}

package com.mt.help;

public class UuidAndName {
	private String uuid;
	private String name;
	private boolean select_flag;
	
	public UuidAndName(String name, String uuid) {
		this.uuid = uuid;
		this.name = name;
		this.select_flag = false;
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isSelect_flag() {
		return select_flag;
	}

	public void setSelect_flag(boolean select_flag) {
		this.select_flag = select_flag;
	}

	@Override
	public String toString() {
		return this.name;
	}
	
}

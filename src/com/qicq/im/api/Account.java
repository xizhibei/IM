package com.qicq.im.api;

public class Account {
	private String name;
	private String pwd;
	
	public Account(String name,String pwd){
		this.setName(name);
		this.setPwd(pwd);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	

}

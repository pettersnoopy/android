package com.egeio.common.mo;

import java.io.Serializable;

public class Contact implements Serializable{
	
	public Contact() {}
	
	public Contact (long ID, String Name) {
		id = ID;
		name = Name;
	}
	
	/*
	 * 
	 */
	public long id;
	
	public String name;
	
	public String login;
	
	public String phone;
	
	public String space_total;
	
	public String space_used;
	
	public String profile_pic_url;

}

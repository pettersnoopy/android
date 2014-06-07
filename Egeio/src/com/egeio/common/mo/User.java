package com.egeio.common.mo;

import java.io.Serializable;

public class User implements Serializable{
	
	/*
	 * id of user
	 */
	public long id;
	
	/*
	 * full name of user
	 */
	public String name;
	
	/*
	 *  email of user
	 */
	public String login;
	
	/*
	 * phone of the user
	 */
	public String phone;
	
	/*
	 * total space amount available in # of bytes
	 */
	public String space_total;
	
	/*
	 * total space used in # of bytes
	 */
	public String space_used;
	
	
	public String profile_pic_url;

}

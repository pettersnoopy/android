package com.egeio.common.mo;

import java.io.Serializable;

public class Message implements Serializable{
	
	public long id;
	
	public String item_type;
	
	public long item_id;
	
	public String content;
	
	public String created_at;
	
	public boolean is_voice;
	
	public String voice_storage_path_mp3;
	
	public String voice_storage_path_ogg;
	
	public int voice_length;
	
	public User user;

}

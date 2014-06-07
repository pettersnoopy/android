package com.egeio.common.mo;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class Item implements Serializable{
	
	public static enum permissions {item_create_collab, item_create_tag, item_upload, item_download, item_delete, item_view, 
		item_preview, item_restore, item_share, edit_properties, permanent_delete, item_create_comment}
	
	public Item() {}

	/*
	 * id of the folder
	 */
	private long id;
	
	/*
	 * type
	 */
	private String type;
	
	/*
	 * name of the folder
	 */
	private String name;
	
	/*
	 * 
	 */
	private String sha1;
	
	/*
	 *  size of the folder
	 */
	private String size;
	
	/*
	 * datetime of folder creation "2012-12-12T10:53:43-08:00"
	 */
	private String created_at;
	
	/*
	 * datetime of folder last modification "2012-12-12T10:53:43-08:00"
	 */
	private String modified_at;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSha1() {
		return sha1;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getModified_at() {
		return modified_at;
	}

	public void setModified_at(String modified_at) {
		this.modified_at = modified_at;
	}
}

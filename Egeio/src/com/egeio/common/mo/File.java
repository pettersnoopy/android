package com.egeio.common.mo;

import java.io.Serializable;

public class File extends Item implements Serializable{

	/*
	 *  all ancestor folder entries
	 */
	private Item path;
	
	/*
	 * description of the folder
	 */
	private String description;
	
	/*
	 * user info who owns the folder
	 */
	private Contact owned_by;
	
	/*
	 * shared link info for this folder
	 */
	private DataTypes.SharedLink shared_link;
	
	/*
	 * parent folder info
	 */
	private Item parent;

	public Item getPath() {
		return path;
	}

	public void setPath(Item path) {
		this.path = path;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Contact getOwned_by() {
		return owned_by;
	}

	public void setOwned_by(Contact owned_by) {
		this.owned_by = owned_by;
	}

	public DataTypes.SharedLink getShared_link() {
		return shared_link;
	}

	public void setShared_link(DataTypes.SharedLink shared_link) {
		this.shared_link = shared_link;
	}

	public Item getParent() {
		return parent;
	}

	public void setParent(Item parent) {
		this.parent = parent;
	}
}

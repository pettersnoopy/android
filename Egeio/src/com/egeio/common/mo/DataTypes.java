package com.egeio.common.mo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.egeio.common.ConstValues;

public class DataTypes {
	
	public static enum Permissions {download, preview, upload};
	
	static final Map<String, String> AccMap = new HashMap<String, String>();
	static {
		AccMap.put("open", "public");
		AccMap.put("company", "company");
		AccMap.put("collaborators", "collaborators");
	}
	
	public static enum Access {open /*public*/, company, collaborators};
	
	public static enum SortBy {date, name, size};
	
	public static enum SortDirection {asc, desc};
	
	public static class LoginResponse implements Serializable {
		
		public String auth_token;
		
		public String auth_success;
		
		public Contact user;
	}
	
	public static class BaseItems implements Serializable , Cloneable{

		public int children_count;
		
		public Item[] items;
		
		public static BaseItems wrapList (List<Item> list) {
			BaseItems items = new BaseItems();
			items.children_count = list.size();
			items.items = new Item[items.children_count];
			for (int i=0; i<list.size(); i++) {
				items.items[i] = list.get(i);
			}
			return items;
		}
		
		public BaseItems cloneItem () {
			try {
				return (BaseItems)super.clone();
			} catch (Exception e) {
				
			}
			return null;
		}
	}
	
	public static class SearchResultBundle implements Serializable {
		
		public int num_found;
		
		public Item[] items;
		
		public int offset;
		
		public int limit;
		
		public String sort_by;
		
		public String sort_direction;
	}
	
	public static class FolderChildren extends BaseItems implements Serializable {

		public int offset;
		
		public int limit;
	}
	
	public static class SharedLink implements Serializable {

		public String url;

		public String access;

		public DataTypes.Permissions[] permissions;

	}
	
	public static class FolderItemBundle extends BaseItems implements Serializable {
		
		public Item[] path;
	}
	
	public static class ContactsItemBundle implements Serializable {
		
		public int contacts_count;
		
		public Contact[] contacts;
	}
	
	public static class CommentItemBundle implements Serializable {
		
		public int comment_count;
		
		public Message[] comments;
		
		public static boolean isVoice (Message message) {
			return message.content == null || "".equals(message.content);
		}
		
	}
	
	public static class ShareAction implements Serializable {
		
		public Access access;
		
		public int disable_download; 
		
		public String alias;
		
		public String password;
		
		public String due_time;
	}
	
	public static class ShareResopnse implements Serializable {
		
		public String uniq_name;
		
		public String getShareUrl () {
			return ConstValues.SERVER + ConstValues.SHARE_SHOW + "/" + uniq_name;
		}
	}
	
	public static class FolderCreate implements Serializable {
		public long parent_id;
		
		public String description;
		
		public String name;
	}
	
	public static String getUniqName (String url){
		String uniqName = null;
		
		if (url != null) {
			String[] tmp = url.split("/");
			uniqName = tmp[tmp.length];
		}
		
		return uniqName;
	}
}

package com.egeio.utils;

import com.egeio.ui.R;

public class FileUtils {
	
	public static enum FileTypes {others, folder, doc, jpg, png, pdf, rar, exe, rm, avi, tmp, xls, mdf, txt};
	
	private static int[] fileIcon = {R.drawable.icon_other ,  /* others*/
								R.drawable.file_director, 	/* folder*/
								R.drawable.icon_word, 		/* word*/
								R.drawable.icon_excel,        /* excel*/
								};
	
	public static FileTypes getFileTypes (String fileName) {
		int offset = fileName.lastIndexOf(".");
		
		FileTypes type = FileTypes.folder;
		
		if (offset > 0) {
			String endwith = fileName.substring(offset+1);
			
			endwith = endwith.trim();
			try {
				type = FileTypes.valueOf(endwith);
			} catch (IllegalArgumentException e) {
				
			}
			if (type == null) {
				type = FileTypes.others;
			} 
		}
		
		return type;
	}
	
	public static int getFileTypeIcon (FileTypes type) {
		int pos = type.ordinal();
		if ( pos >= fileIcon.length ) {
			return fileIcon[0];
		}
		
		return fileIcon[pos];
	}
}

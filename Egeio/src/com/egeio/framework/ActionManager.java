package com.egeio.framework;

import java.util.HashMap;
import java.util.Map;

import com.egeio.common.ConstValues;
import com.egeio.common.Queue;
import com.egeio.common.mo.DataTypes;
import com.egeio.common.mo.Folder;
import com.egeio.common.mo.Item;
import com.egeio.common.mo.Message;
import com.egeio.network.NetworkManager;
import com.egeio.ui.activity.ShareTaskActivity;

import android.content.Intent;
import android.os.Bundle;

public class ActionManager {
	
	public static final int ACTION_ADDFOLDER = 0;
	
	public static final int ACTION_SENDREVIEW = 1;
	
	public static final int ACTION_VIEWER = 2;
	
	public static final int ACTION_DELETE = 3;
	
	public static final int ACTION_MARK = 4;
	
	public static final int ACTION_SHARED = 5;
	
	public static final int ACTION_SENDSHARE = 6;
	
	public static final int ACTION_RENAME = 7;
	
	public static final int ACTION_DOWNLOAD = 8;
	
	public static final int ACTION_REFERSH_FILE_LIST = 9;
	
	public static final int ACTION_UPLOAD = 10;
	
	public static final int ACTION_UPLOAD_PIC = 11;
	
	public static final int ACTION_MORE = 12;
	
	public static final int ACTION_CREATEFOLDER = 13;
	
	public static final int ACTION_COPY = 14;
	
	public static final int ACTION_MSG_HOS = 15;
	
	public static final int ACTION_COMMENT = 16;
	
	public static final int ACTION_CREATE_COMMENT = 17;
	
	public static class OnDoingTaskListener {
		
		private BaseActivity mContext;
		
		public OnDoingTaskListener (BaseActivity context) {
			mContext = context;
		}
		
		public void ready (int code, Bundle args) {}
		
		public void doing (int code, Bundle args) {}
		
		public boolean done (int code, Bundle result) {
			if (mContext != null) {
				mContext.onTaskDone(code, result);
			}
			return false;
		}
		
		public boolean fault (int code, Bundle args) {
			return false;
		}
	}
	
	public static class ActionBeen {
		int code;
		OnDoingTaskListener listener;
		Bundle args;
		
		ActionBeen (int code, Bundle args, OnDoingTaskListener listener) {
			this.code = code;
			this.args = args;
			this.listener = listener;
		}
	}
	
	private Map<Integer, Queue<ActionBeen>> mActionListener = new HashMap<Integer, Queue<ActionBeen>>();
	
	private static ActionManager mActionManager;
	
	private Map<Integer, TaskHandler> mDoingTask = new HashMap<Integer, TaskHandler>();
	
	public static interface ActionListener {
		public void onStartAction(int code, Bundle bundle);
	}
	
	public static ActionManager getInstance() {
		if (mActionManager == null) {
			mActionManager = new ActionManager();
		}
		return mActionManager;
	}
	
	public void startAction (BaseActivity context, int code, Bundle args) {
		
		startAction(context, code, args, new OnDoingTaskListener(context));
		
	}
	
	public void startAction (BaseActivity context, int code, Bundle args, OnDoingTaskListener listener) {
		
		
		Bundle bundle = new Bundle(args);
		bundle.putInt(ConstValues.ACTIONCODE, code);
		
		// 仅仅只是跳转，不做事件处理
		if (ACTION_SHARED == code || ACTION_VIEWER == code) {
			redirect(context, bundle);
			return;
		}
		
		addEvent(code, args, listener);
		
		if (mDoingTask.get(code) == null) { // new task
			notifyTaskDone(context, bundle);
		}
	}
	
	public void addEvent (int code, Bundle args, OnDoingTaskListener listener) {
		if (!mActionListener.containsKey(code)) {
			mActionListener.put(code, new Queue<ActionBeen>());
		}
		// 消化list成为消息队列
		if (args.containsKey(ConstValues.ITEM_LIST)) {
			DataTypes.BaseItems items = ((DataTypes.BaseItems)args.getSerializable(ConstValues.ITEM_LIST)).cloneItem();
			args.remove(ConstValues.ITEM_LIST);
			for (int i=0; i<items.children_count; i++) {
				Bundle bundle = new Bundle(args);
				bundle.putSerializable(ConstValues.ITEMINFO, items.items[i]);
				bundle.putLong(ConstValues.FOLDER_ID, items.items[i].getId());
				mActionListener.get(code).put(new ActionBeen(code, bundle, listener));
			}
		} else {
			mActionListener.get(code).put(new ActionBeen(code, args, listener));
		}
	}
	
	public ActionBeen getEvent (int code) {
		if (mActionListener.containsKey(code)) {
			return mActionListener.get(code).get();
		}
		return null;
	}
	
	public Queue<ActionBeen> getQueue (int code) {
		Queue<ActionBeen> queue = mActionListener.get(code);
		mActionListener.remove(code);
		return queue;
	}
	
	public boolean isEmpty(int actionCode) {
		return mActionListener.get(actionCode) == null ? true : mActionListener.get(actionCode).isEmpty();
	}
	
	public void redirect (BaseActivity context, Bundle bundle) {
		int action = bundle.getInt(ConstValues.ACTIONCODE);
		switch (action) {
		case ACTION_SENDREVIEW:
			sendRevier (context, bundle);
			break;
			
		case ACTION_SHARED:
			Intent intent = new Intent(context, ShareTaskActivity.class);
			intent.putExtras(bundle);
			context.startActivity(intent);
			break;	
		}
	}
	
	public void notifyTaskDone (BaseActivity context, Bundle bundle) {
		int action = bundle.getInt(ConstValues.ACTIONCODE);
		
		// 任务请求处理完毕
		if (isEmpty(action)) {
			mDoingTask.remove(action);
			return;
		}
		
		switch (action) {
		case ACTION_ADDFOLDER:
			TaskHandler addFolder = new TaskHandler(context) {
				@Override
				public Bundle doAction(ActionBeen action) {
					return null;
				}
			};
			addFolder.start(bundle);
			addTaskHandler (bundle, addFolder);
			break;
			
		case ACTION_VIEWER:
			viewer(context, bundle);
			break;
			
		case ACTION_DELETE:
			TaskHandler deleteFolder = new TaskHandler(context) {
				@Override
				public Bundle doAction(ActionBeen action) {
					String[] ids = action.args.getStringArray(ConstValues.ITEM_IDS);
					NetworkManager.getInstance(mContext).deleteFile(ids);
					return action.args;
				}
			};
			deleteFolder.start(bundle);
			addTaskHandler (bundle, deleteFolder);
			break;
			
		case ACTION_MARK:
			mark (context, bundle);
			break;
			
		case ACTION_RENAME:
			rename (context, bundle);
			break;
			
		case ACTION_DOWNLOAD:
			download (context, bundle);
			break;
			
		case ACTION_UPLOAD:
			TaskHandler upload = new TaskHandler(context) {
				@Override
				public Bundle doAction(ActionBeen action) {
					Bundle result = new Bundle(action.args);
					String path = result.getString(ConstValues.PATH);
					Long id = result.getLong(ConstValues.FOLDER_ID);
					String downloadurl = NetworkManager.getInstance(mContext).uploadFile(id, path);
					result.putSerializable(ConstValues.DOWNLOAD_URL, downloadurl);
					return result;
				}
			};
			addTaskHandler (bundle, upload);
			upload.start(bundle);
			break;
			
		case ACTION_UPLOAD_PIC:
			TaskHandler uploadPic = new TaskHandler(context) {
				@Override
				public Bundle doAction(ActionBeen action) {
					Bundle result = new Bundle(action.args);
					String path = result.getString(ConstValues.PATH);
					String downloadurl = NetworkManager.getInstance(mContext).uploadPic(path);
					result.putSerializable(ConstValues.DOWNLOAD_URL, downloadurl);
					return result;
				}
			};
			addTaskHandler (bundle, uploadPic);
			uploadPic.start(bundle);
			break;
		case ACTION_REFERSH_FILE_LIST:
			TaskHandler fileRefersh = new TaskHandler(context) {
				@Override
				public Bundle doAction(ActionBeen action) {
					Bundle result = new Bundle(action.args);
					Long folderID = result.getLong(ConstValues.FOLDER_ID);
					DataTypes.FolderItemBundle items = NetworkManager.getInstance(mContext).getFileList(folderID);
					result.putSerializable(ConstValues.ITEM_LIST, items);
					return result;
				}
			};
			addTaskHandler (bundle, fileRefersh);
			fileRefersh.start(bundle);
			break;
			
		case ACTION_SENDSHARE:
			// 消化Bundle成为任务队列
			TaskHandler sendShare = new TaskHandler(context) {
				@Override
				public Bundle doAction(ActionBeen action) {
					Bundle result = new Bundle(action.args);
					long forderID = result.getLong(ConstValues.FOLDER_ID);
					DataTypes.ShareAction shareAction = (DataTypes.ShareAction) result.getSerializable(ConstValues.SHAREACTION);
					DataTypes.ShareResopnse response = NetworkManager.getInstance(mContext).sendShare(forderID, shareAction);
					result.putSerializable(ConstValues.SHARERESPONSE, response);
					return result;
				}
			};
			addTaskHandler (bundle, sendShare);
			sendShare.start(bundle);
			break;
			
		case ACTION_CREATEFOLDER:
			TaskHandler createFolder = new TaskHandler(context) {
				@Override
				public Bundle doAction(ActionBeen action) {
					Bundle result = new Bundle(action.args);
					DataTypes.FolderCreate folderCreate = (DataTypes.FolderCreate) result.getSerializable(ConstValues.FOLDERACTION);
					Folder folder = NetworkManager.getInstance(mContext).createFolder(folderCreate);
					result.putSerializable(ConstValues.FOLDERINFO, folder);
					return result;
				}
			};
			addTaskHandler (bundle, createFolder);
			createFolder.start(bundle);
			break;
			
		case ACTION_COMMENT:
			TaskHandler getComment = new TaskHandler(context) {
				@Override
				public Bundle doAction(ActionBeen action) {
					Bundle result = new Bundle(action.args);
					int folderID = result.getInt(ConstValues.FOLDER_ID);
					DataTypes.CommentItemBundle messageList = NetworkManager.getInstance(mContext).getMessage(folderID);
					result.putSerializable(ConstValues.MESSAGELIST, messageList);
					return result;
				}
			};
			addTaskHandler (bundle, getComment);
			getComment.start(bundle);
			break;
		case ACTION_CREATE_COMMENT:
			TaskHandler createComment = new TaskHandler(context) {
				@Override
				public Bundle doAction(ActionBeen action) {
					Bundle result = new Bundle(action.args);
					Item item = (Item)result.getSerializable(ConstValues.ITEMINFO);
					String content = result.getString(ConstValues.content);
					Message message = null;
					if (content == null || "".equals(content)) {
						String voice_data = result.getString(ConstValues.voice_data);
						message = NetworkManager.getInstance(mContext).createVoiceComment(item.getId(), voice_data);
					} else {
						message = NetworkManager.getInstance(mContext).createTextComment(item.getId(), content);
					}
					
					result.putSerializable(ConstValues.MESSAGE, message);
					return result;
				}
			};
			addTaskHandler (bundle, createComment);
			createComment.start(bundle);
			break;
		}
			
	}
	
	private void sendRevier (BaseActivity context, Bundle bundle) {
		
	}
	
	private void mark (BaseActivity context, Bundle bundle) {
		
	}
	
	private void rename (BaseActivity context, Bundle bundle) {
		
	}
	
	private void download (BaseActivity context, Bundle bundle) {
		
	}
	
	private void viewer (BaseActivity context, Bundle bundle) {
		
	}
	
	// 保持当前执行的同一类人物只有一个
	private void addTaskHandler (Bundle bundle, TaskHandler handler) {
		int code = bundle.getInt(ConstValues.ACTIONCODE);
		if (mDoingTask.containsKey(code)) {
			mDoingTask.remove(code);
		}
		mDoingTask.put(code, handler);
	}
}

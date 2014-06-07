package com.egeio.framework;

import android.os.Bundle;

import com.egeio.common.ConstValues;
import com.egeio.common.Queue;

public abstract class TaskHandler extends BackgroundTask{
	
	private Queue<ActionManager.ActionBeen> mTaskQueue = null;
	
	private ActionManager mActionManager;
	
	private BaseActivity mActivity;
	
	public TaskHandler(BaseActivity activity) {
		super(activity);
		mActivity = activity;
	}
	
	public abstract Bundle doAction(ActionManager.ActionBeen action);

	@Override
	protected Object doInBackground(Bundle bundle) {
		
		int code = bundle.getInt(ConstValues.ACTIONCODE);
		mActionManager = ActionManager.getInstance();
		
		if (mTaskQueue == null || mTaskQueue.isEmpty()) {
			mTaskQueue = mActionManager.getQueue(code);
		}
		
		if (mTaskQueue!= null && !mTaskQueue.isEmpty()) {
			ActionManager.ActionBeen action = null;
			while ((action = mTaskQueue.get()) != null) {
				
				if (action.listener != null) {
					action.listener.doing(action.code, bundle);
				}
				
				Bundle result = doAction(action);
				
				if (result!=null && action.listener != null) {
					action.listener.done(action.code, result);
				} else if (action.listener != null) {
					action.listener.fault(action.code, result);
				}
			}
		}
		
		return bundle;
	}

	@Override
	protected void onPostExecute(Object result) {
		Bundle bundle = (Bundle) result;
		int code = bundle.getInt(ConstValues.ACTIONCODE);
		mActionManager.notifyTaskDone(mActivity, bundle);
	}

}

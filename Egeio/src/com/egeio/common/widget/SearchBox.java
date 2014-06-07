package com.egeio.common.widget;

import java.util.List;

import com.egeio.adapter.FileListFilterAdapter;
import com.egeio.common.mo.Item;
import com.egeio.ui.R;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

public class SearchBox extends FrameLayout{
	
	private Context mContext;
	
	private LayoutInflater mInflater;
	
	private EditText mSearchbox;
	
	private ImageButton deletesearch, search;
	
	private FileListFilterAdapter mFilterAdapter; 
	
	private ListView mListView;
	
	private TextWatcher mFilterTextWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			mFilterAdapter.getFilter().filter(s);
		}

	};

	public SearchBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initUi(context);
	}

	public SearchBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		initUi(context);
	}

	public SearchBox(Context context) {
		super(context);
		initUi(context);
	}
	
	private void initUi(Context context) {
		mContext = context;
		
		mInflater = LayoutInflater.from(context);
		View view = mInflater.inflate(R.layout.searchview, this);
		mSearchbox = (EditText)view.findViewById(R.id.searchkeyword);
		deletesearch = (ImageButton) view.findViewById(R.id.deletesearch);
		search = (ImageButton) view.findViewById(R.id.btnsearch);
		mSearchbox.addTextChangedListener(mFilterTextWatcher);
		mListView = (ListView) view.findViewById(android.R.id.list);
		if (mFilterAdapter == null) {
			mFilterAdapter = new FileListFilterAdapter(mContext);
		}
		mListView.setAdapter(mFilterAdapter);
	}

}

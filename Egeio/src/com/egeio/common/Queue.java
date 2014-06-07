package com.egeio.common;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

public class Queue<T> implements Serializable{

	LinkedList<T> linkedList = new LinkedList<T>();

	// 队尾插
	public void put(T o) {
		linkedList.addLast(o);
	}

	// 队头取 取完并删除
	public T get() {
		if (!linkedList.isEmpty())
			return linkedList.removeFirst();
		else
			return null;
	}
	
	public boolean hasElement(T o) {
		for (T obj : linkedList) { 
			if (obj.equals(o)) {
				return true;
			}
		}
		return false;
	}

	public boolean isEmpty() {
		return linkedList.isEmpty();
	}

	public int size() {
		return linkedList.size();
	}

	public void clear() {
		linkedList.clear();
	}

}

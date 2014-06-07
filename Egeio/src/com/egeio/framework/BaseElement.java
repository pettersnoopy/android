package com.egeio.framework;

import java.io.Serializable;

public class BaseElement implements Serializable{
	public Long Id;
	public String Name;
	
	public BaseElement() {} ;
	
	public BaseElement (Long id, String name) {
		Id = id;
		Name = name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof BaseElement) {
			return Id == ((BaseElement)o).Id;
		}
		return super.equals(o);
	}
}
/*
 * Created on 11.5.2004
 */
package com.idega.builder.dynamicpagetrigger.util;


/**
 * Title: KeyAndValue
 * Description:
 * Copyright: Copyright (c) 2004
 * Company: idega Software
 * @author 2004 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version 1.0
 */
public class KeyAndValue {

	
	public Object key;
	public Object value;
	
	public KeyAndValue(Object key, Object value){
		this.key = key;
		this.value=value;
	}
	
	public Object getKey(){
		return this.key;
	}
	
	public Object getValue(){
		return this.value;
	}

}

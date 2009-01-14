package com.idega.builder.presentation;

import java.io.IOException;

import javax.faces.context.FacesContext;

import com.idega.presentation.IWBaseComponent;

public class IBRegionControl extends IWBaseComponent {

	String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		// TODO Auto-generated method stub
		super.encodeBegin(context);
		context.getResponseWriter().startElement("div", this);
		context.getResponseWriter().writeAttribute("style", "border: 1px solid red;", null);

	}

	@Override
	public void encodeChildren(FacesContext context) throws IOException {
		// TODO Auto-generated method stub
		super.encodeChildren(context);
	}

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		// TODO Auto-generated method stub
		super.encodeEnd(context);
		context.getResponseWriter().endElement("div");
	}

}

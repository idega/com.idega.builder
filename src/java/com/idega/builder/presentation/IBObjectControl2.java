package com.idega.builder.presentation;

import java.io.IOException;

import javax.faces.context.FacesContext;

import com.idega.presentation.IWBaseComponent;

public class IBObjectControl2 extends IWBaseComponent {

	String componentId;
	String componentClass;
	
	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getComponentClass() {
		return componentClass;
	}

	public void setComponentClass(String componentClass) {
		this.componentClass = componentClass;
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		// TODO Auto-generated method stub
		super.encodeBegin(context);
		context.getResponseWriter().startElement("div", this);
		context.getResponseWriter().writeAttribute("style", "border: 1px solid yellow;", null);

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

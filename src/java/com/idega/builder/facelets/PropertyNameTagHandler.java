package com.idega.builder.facelets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletException;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;
import javax.faces.view.facelets.TextHandler;

import javax.faces.view.facelets.FaceletHandler;
import javax.faces.view.facelets.CompositeFaceletHandler;

/**
 * <p>
 * Class handle the root tag in IBXML
 * </p>
 * 
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson </a>
 * 
 * Last modified: $Date: 2009/01/14 15:07:18 $ by $Author: tryggvil $
 * @version 1.0
 */
public class PropertyNameTagHandler extends LegacyTagHandler {

	String name;
	
	public PropertyNameTagHandler(TagConfig config) {
		super(config);
	}

	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {
		//Does nothing except forward to the sub handlers
		this.nextHandler.apply(ctx, parent);
        Iterator itr = this.findNextByType(TextHandler.class);
        while (itr.hasNext()) {
        	TextHandler nameChild = (TextHandler) itr.next();
        	setName(nameChild.getText());
            //if (log.isLoggable(Level.FINE)) {
            //    log.fine(tag + " found PropertyNameTagHandler[" + nameChild + "]");
            //}
        }
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

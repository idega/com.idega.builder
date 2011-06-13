package com.idega.builder.facelets;

import java.io.IOException;
import java.util.Iterator;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletException;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;
import javax.faces.view.facelets.TextHandler;

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
public class PropertyTypeTagHandler extends LegacyTagHandler {

	String value;
	
	public PropertyTypeTagHandler(TagConfig config) {
		super(config);
        Iterator itr = this.findNextByType(TextHandler.class);
        while (itr.hasNext()) {
        	TextHandler nameChild = (TextHandler) itr.next();
        	setValue(nameChild.getText());
            //if (log.isLoggable(Level.FINE)) {
            //    log.fine(tag + " found PropertyNameTagHandler[" + nameChild + "]");
            //}
        }
	}

	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {
		//Does nothing except forward to the sub handlers
		this.nextHandler.apply(ctx, parent);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}

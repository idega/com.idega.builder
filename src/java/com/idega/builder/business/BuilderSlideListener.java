package com.idega.builder.business;


import org.apache.slide.event.ContentEvent;
import com.idega.business.IBOService;
import com.idega.slide.business.IWSlideChangeListener;

public interface BuilderSlideListener extends IBOService, IWSlideChangeListener {

	/**
	 * @see com.idega.builder.business.BuilderSlideListenerBean#onSlideChange
	 */
	public void onSlideChange(ContentEvent contentEvent);
}
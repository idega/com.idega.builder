var ORIGINAL = null;
var PARENT_CONTAINER_ID = null;
var CLONE = null;

function registerBuilderDragDropActions() {
	$$('div.moduleName, div.moduleWrapper').each(
		function(element) {
			registerDragAndDropActionsForModuleNameElement(element);
		}
	);
	
	$$('div.moduleDropArea').each(
		function(element) {
			registerForDropSingleElement(element);
		}
	);
	
	/*$$('span.moduleNameTooltip').each(
		function(element) {
			initToolTipForElement(element);
		}
	);*/
}

function registerDragAndDropActionsForModuleNameElement(element) {
	element.addEvent('mousedown', function(e) {
		e = new Event(e).stop();

		ORIGINAL = element.getParent();
		PARENT_CONTAINER_ID = ORIGINAL.getParent().clone().id;
		CLONE = ORIGINAL.clone();
		CLONE.setStyles(ORIGINAL.getCoordinates());
		CLONE.setStyles({'opacity': 0.8, 'position': 'absolute', 'z-index': 9999});
		CLONE.addEvent('emptydrop', function() {
			this.remove();
			manageDropAreas(false);
		}).inject(document.body);
		
		manageDropAreas(true);
		var draggable = CLONE.makeDraggable({
			droppables: $$('div.moduleDropArea')
		});
		draggable.start(e);
	});
}

function registerForDropSingleElement(element) {
	element.addEvents({
		'drop': function() {
			var dropArea = getMarkupAttributeValue(element, 'insertbefore');
			var moduleContainer = element.getParent();
			
			var instanceId = ORIGINAL.getProperty('instanceid');
			var pageKey = ORIGINAL.getProperty('pageid');
			var formerParentId = ORIGINAL.getProperty('parentid');
			var newParentId = moduleContainer.getProperty('parentid');
			var changingRegions = !(formerParentId == newParentId);
			var insertAbove = dropArea == 'true';
			var neighbourInstanceId = moduleContainer.getProperty('instanceid');
			
			if (CLONE != null) {
				var parentNodeOfClone = CLONE.getParent();
				if (parentNodeOfClone != null) {
					parentNodeOfClone.removeChild(CLONE);
				}
			}
			showLoadingMessage(MOVING_LABEL);
			BuilderEngine.moveModule(instanceId, pageKey, formerParentId, newParentId, neighbourInstanceId, insertAbove, {
				callback: function(result) {
					moveModuleCallback(result, element, moduleContainer, insertAbove, changingRegions, newParentId);
				}
			});
		},
		'over': function() {
			element.addClass('draggingOverDropArea');
		},
		'leave': function() {
			removeElementStyleAfterDrop(element);
		}
	});
}

function removeElementStyleAfterDrop(element) {
	element.removeClass('draggingOverDropArea');
}

function moveModuleCallback(result, element, moduleContainer, insertAbove, changingRegions, newParentId) {
	closeAllLoadingMessages();
	
	if (!result) {
		return false;
	}
	
	removeElementStyleAfterDrop(element);
	
	//	Inserting
	if (insertAbove) {
		ORIGINAL.injectBefore(moduleContainer);
	}
	else {
		ORIGINAL.injectAfter(moduleContainer);
	}
	
	var isLastOriginalModule = ORIGINAL.getProperty('islastmodule');
	var wasLastOriginalModule = isLastOriginalModule == 'true';
	var isLastModule = moduleContainer.getProperty('islastmodule');
	var wasContainerLastModule = isLastModule == 'true';
	
	//	Setting properties
	if (wasContainerLastModule && !insertAbove) {
		markModuleContainerAsNotLast(moduleContainer, element);

		addDropAreaToTheEnd(ORIGINAL, newParentId);				//	Adding drop area to the end of the new last module
	}
	else if (wasLastOriginalModule && insertAbove) {
		ORIGINAL.setProperty('islastmodule', 'false');			//	Now not the last module
		var dropArea = getDropAreaFromElement(ORIGINAL);		//	Removing 'below' drop area
		if (dropArea != null) {
			dropArea.remove();
		}
		
		addDropAreaToTheLastModuleContainer(ORIGINAL.getParent(), newParentId);
	}
	
	if (changingRegions) {
		ORIGINAL.setProperty('parentid', newParentId);
		var firstElement = ORIGINAL.getFirst();					// Renaming region to a new one
		if (firstElement != null) {
			if (firstElement.hasClass('moduleDropArea')) {
				firstElement.setText(getTextForDropModuleContainer(newParentId));
			}
		}
		
		if (wasLastOriginalModule) {
			addDropAreaToTheLastModuleContainer($(PARENT_CONTAINER_ID), PARENT_CONTAINER_ID);
		}
	}
	
	manageDropAreas(false);
}

function addDropAreaToTheLastModuleContainer(container, regionName) {
	if (container == null) {
		return false;
	}
	
	//	Adding 'below' drop area to the last module container
	var modulesContainers = getNeededElementsFromList(container.childNodes, 'moduleContainer');
	if (modulesContainers.length > 0) {
		addDropAreaToTheEnd($(modulesContainers[modulesContainers.length - 1]), regionName);
	}
}

function getDropAreaFromElement(element) {
	if (element == null) {
		return null;
	}
	
	var theLastChild = element.getLast();
	if (theLastChild == null) {
		return null;
	}
	
	if (theLastChild.hasClass('spacer')) {
		var dropArea = theLastChild.getPrevious();
		if (dropArea == null) {
			return null;
		}
		if (dropArea.hasClass('moduleDropArea')) {	
			return dropArea;
		}
	}
	
	return null;
}

function addDropAreaToTheEnd(element, regionName) {
	if (element == null) {
		return false;
	}
	
	var theLastElement = element.getLast();
	if (theLastElement == null) {
		return false;
	}
	var dropArea = getDropAreaFromElement(element);
	if (dropArea == null) {
		element.setProperty('islastmodule', 'true');	//	Now the last module
		getDropArea(regionName).injectBefore(theLastElement);
	}
}

function manageDropAreas(needToShow) {
	$$('div.moduleDropArea').each(
		function(element) {
			var canSetStyle = true;
			var parentNode = element.getParent();
			canSetStyle = parentNode != null;
			if (canSetStyle) {
				if (ORIGINAL.id != parentNode.id) {
					if (needToShow) {
						element.setStyle('display', 'block');
						parentNode.addClass('showingDrag');
					}
					else {
						element.setStyle('display', 'none');
						parentNode.removeClass('showingDrag');
					}
				}
			}
		}
	);
}

function getDropArea(regionName) {
	var drop = new Element('div');
	drop.addClass('moduleDropArea');
	drop.setProperty('insertbefore', 'false');
	drop.appendText(getTextForDropModuleContainer(regionName));
	registerForDropSingleElement(drop);
	return drop;
}

function getTextForDropModuleContainer(regionName) {
	if (regionName == null || regionName == '') {
		regionName = 'this';
	}
	return DROP_MODULE_HERE_LABEL + ' ' + regionName + ' ' + SMALL_REGION_LABEL;
}

function markModuleContainerAsNotLast(moduleContainer, dropArea) {
	if (dropArea != null) {
		dropArea.remove();										//	Old last drop area is removed
	}
	if (moduleContainer != null) {
		moduleContainer.setProperty('islastmodule', 'false');	//	Now not the last module
	}
}
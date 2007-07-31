var ORIGINAL = null;
var PARENT_CONTAINER_ID = null;
var CLONE = null;
var DROPPABLES = new Array();

function registerBuilderDragDropActions() {
	DROPPABLES = new Array();
	$$('div.moduleName').each(
		function(element) {
			registerDragAndDropActionsForModuleNameElement(element);
		}
	);
	
	$$('div.moduleDropArea').each(
		function(element) {
			registerForDropSingleElement(element);
		}
	);
	
	$$('span.moduleNameTooltip').each(
		function(element) {
			initToolTipForElement(element);
		}
	);
}

function registerDragAndDropActionsForModuleNameElement(element) {
	element.addEvent('mousedown', function(e) {
		e = new Event(e).stop();

		ORIGINAL = element.parentNode.parentNode;
		PARENT_CONTAINER_ID = ORIGINAL.parentNode.clone().id;
		CLONE = ORIGINAL.clone();
		CLONE.setStyle('moduleContainer');
		CLONE.setStyles(ORIGINAL.getCoordinates());
		CLONE.setStyles({'opacity': 0.8, 'position': 'absolute', 'z-index': 9999});
		CLONE.addEvent('emptydrop', function() {
			this.remove();
			manageDropAreas(false);
		}).inject(document.body);
		
		manageDropAreas(true);
		var drag = CLONE.makeDraggable({
			droppables: DROPPABLES
		});
		drag.start(e);
	});
}

function registerForDropSingleElement(element) {
	var dropFx = element.effect('background-color', {wait: false});
	element.addEvents({
		'drop': function() {
			var dropArea = getMarkupAttributeValue(element, 'insertbefore');
			var moduleContainer = element.parentNode;
			
			var instanceId = ORIGINAL.getProperty('instanceid');
			var pageKey = ORIGINAL.getProperty('pageid');
			var formerParentId = ORIGINAL.getProperty('parentid');
			var newParentId = moduleContainer.getProperty('parentid');
			var changingRegions = !(formerParentId == newParentId);
			var insertAbove = dropArea == 'true';
			var neighbourInstanceId = moduleContainer.getProperty('instanceid');
			
			if (CLONE != null) {
				var parentNodeOfClone = CLONE.parentNode;
				if (parentNodeOfClone != null) {
					parentNodeOfClone.removeChild(CLONE);
				}
			}
			showLoadingMessage(MOVING_LABEL);
			BuilderEngine.moveModule(instanceId, pageKey, formerParentId, newParentId, neighbourInstanceId, insertAbove, {
				callback: function(result) {
					moveModuleCallback(result, element, moduleContainer, dropFx, insertAbove, changingRegions, newParentId);
				}
			});
		},
		'over': function() {
			dropFx.start('98B5C1');
		},
		'leave': function() {
			dropFx.start('ffffff');
		}
	});
	DROPPABLES.push(element);
}

function moveModuleCallback(result, element, moduleContainer, dropFx, insertAbove, changingRegions, newParentId) {
	closeAllLoadingMessages();
	
	if (!result) {
		return false;
	}
	
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

		addDropAreaToTheEnd(ORIGINAL);							//	Adding drop area to the end of the new last module
	}
	else if (wasLastOriginalModule && insertAbove) {
		ORIGINAL.setProperty('islastmodule', 'false');			//	Now not the last module
		var dropArea = getDropAreaFromElement(ORIGINAL);		//	Removing 'below' drop area
		if (dropArea != null) {
			dropArea.remove();
		}
		
		addDropAreaToTheLastModuleContainer(ORIGINAL.parentNode);
		dropFx.start('7389AE').chain(dropFx.start.pass('ffffff', dropFx));
	}
	else {
		dropFx.start('7389AE').chain(dropFx.start.pass('ffffff', dropFx));
	}
	
	if (changingRegions) {
		ORIGINAL.setProperty('parentid', newParentId);
		
		if (wasLastOriginalModule) {
			addDropAreaToTheLastModuleContainer(document.getElementById(PARENT_CONTAINER_ID));
		}
	}
	
	manageDropAreas(false);
}

function addDropAreaToTheLastModuleContainer(container) {
	if (container == null) {
		return false;
	}
	
	//	Adding 'below' drop area to the last module container
	var modulesContainers = getNeededElementsFromList(container.childNodes, 'moduleContainer');
	if (modulesContainers.length > 0) {
		addDropAreaToTheEnd(modulesContainers[modulesContainers.length - 1]);
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

function addDropAreaToTheEnd(element) {
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
		getDropArea().injectBefore(theLastElement);
	}
}

function manageDropAreas(needToShow) {
	$$('div.moduleDropArea').each(
		function(element) {
			var canSetStyle = true;
			var parentNode = element.parentNode;
			canSetStyle = parentNode != null;
			if (canSetStyle) {
				if (ORIGINAL.id != parentNode.id) {
					if (needToShow) {
						element.setStyle('visibility', 'visible');
					}
					else {
						element.setStyle('visibility', 'hidden');
					}
				}
			}
		}
	);
}

function getDropArea() {
	var drop = new Element('div');
	drop.addClass('moduleDropArea');
	drop.setProperty('insertbefore', 'false');
	drop.appendText(DROP_MODULE_HERE_LABEL);
	registerForDropSingleElement(drop);
	return drop;
}

function markModuleContainerAsNotLast(moduleContainer, dropArea) {
	if (dropArea != null) {
		dropArea.remove();										//	Old last drop area is removed
	}
	if (moduleContainer != null) {
		moduleContainer.setProperty('islastmodule', 'false');	//	Now not the last module
	}
}
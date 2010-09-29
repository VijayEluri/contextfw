function HierarchyWeb() {
	var handle = null;
	var updateUrl = null;
	var refreshUrl = null;
	var removeUrl = null;
}

HierarchyWeb.initPage = function(handle) {
	this.handle = handle;
	this.updateUrl = "/hierarchy-context-update/"+handle+"?";
	this.refreshUrl = "/hierarchy-context-refresh/"+handle+"?";
	this.removeUrl = "/hierarchy-context-remove/"+handle+"?";
	
	setTimeout("HierarchyWeb.refresh()", 30000);
}

HierarchyWeb.refresh = function() {

	new Ajax.Request(this.refreshUrl, {
        method:     'get',
        onSuccess: function(transport) {
			setTimeout("HierarchyWeb.refresh()", 30000);
	    },
	    onFailure: function() { 
	        //alert('Ajax-call failed')
	    }
    });
}

HierarchyWeb.remove = function() {

	new Ajax.Request(this.removeUrl, {
        method:     'get'	    
    });
}

HierarchyWeb.call = function(elId, method) {
	
	var params = {}
	
	for(var i=2; i< arguments.length; i++) {
      if (arguments[i] != null && typeof(arguments[i]) == "object") {
    	  params[elId+".p"+(i-2)] = JSON.serialize(arguments[i]);
      }
      else {
    	  params[elId+".p"+(i-2)] = arguments[i];
      }
	}
	
	url = this.updateUrl+"context:el="+elId + "&" + elId + ".method=" + method;
	
	new Ajax.Request(url, {
        method:     'post',
        parameters: params,
        onSuccess: function(transport) {
            HierarchyWeb.handleAjaxResponse(transport.responseText);
	    },
	    onFailure: function() { 
	        alert('Ajax-call failed')
	    }
    });
}

HierarchyWeb.requestUpdate = function(elId, event, params) {

    // params = new Hash();

	url = this.updateUrl+"context:el="+elId + "&" + elId + ".event=" + event;
	
	if (params == null) {
		new Ajax.Request(url, {
	        method:     'post',
	        onSuccess: function(transport) {
	            HierarchyWeb.handleAjaxResponse(transport.responseText);
		    },
		    onFailure: function() { 
		        alert('Ajax-call failed')
		    }
	    });
	}
	else {
		new Ajax.Request(url, {
	        method:     'post',
	        parameters: params,
	        onSuccess: function(transport) {
	            HierarchyWeb.handleAjaxResponse(transport.responseText);
		    },
		    onFailure: function() { 
		        alert('Ajax-call failed')
		    }
	    });
	}    
}

HierarchyWeb.requestPostUpdate2 = function(elId, event, params) {

    // params = new Hash();

	url = this.updateUrl+"context:el="+elId + "&" + elId + ".event=" + event;
	
	if (params == null) {
		new Ajax.Request(url, {
	        method:     'post',
	        onSuccess: function(transport) {
	            HierarchyWeb.handleAjaxResponse(transport.responseText);
		    },
		    onFailure: function() { 
		        alert('Ajax-call failed')
		    }
	    });
	}
	else {
		new Ajax.Request(url, {
	        method:     'post',
	        parameters: params,
	        onSuccess: function(transport) {
	            HierarchyWeb.handleAjaxResponse(transport.responseText);
		    },
		    onFailure: function() { 
		        alert('Ajax-call failed')
		    }
	    });
	}    
}

HierarchyWeb.requestGetUpdate = function(appId, event, params) {

    // params = new Hash();
	url = this.updateUrl+"context:el="+appId + "&" + appId + ".event=" + event;
	
	if (params == null) {
		new Ajax.Request(url, {
	        method:     'get',
	        onSuccess: function(transport) {
	            HierarchyWeb.handleAjaxResponse(transport.responseText);
		    },
		    onFailure: function() { 
		        alert('Ajax-call failed')
		    }
	    });
	}
	else {
		new Ajax.Request(url, {
	        method:     'get',
	        parameters: params,
	        onSuccess: function(transport) {
	            HierarchyWeb.handleAjaxResponse(transport.responseText);
		    },
		    onFailure: function() { 
		        alert('Ajax-call failed')
		    }
	    });
	}    
}
/*
HierarchyWeb.requestUpdate = function(params) {

    // params = new Hash();

    new Ajax.Request(updateURL, {
        method:     'post',
        parameters: params,
        onSuccess: function(transport) {
            HierarchyWeb.handleAjaxResponse(transport.responseText);
	    },
	    onFailure: function() { 
	        alert('Ajax-call failed')
	    }
    });
}
*/
HierarchyWeb.handleAjaxResponse = function(response) {
	
	var domDocument;
    
    try {
        if (response != null) {
            domDocument = (new DOMParser()).parseFromString(response, "text/xml");
        }
        else {
            return false;
        }
    }
    catch(err2) {       
    	alert(err2);
        return false;
    }
    
    if(!Sarissa.getParseErrorText(domDocument) == Sarissa.PARSED_OK){
        return false;
    }
    
    serializer = new XMLSerializer();
    
    // Replace inner
    
    nodes = domDocument.selectNodes("/updates/replaceInner");
   
    if (nodes.length > 0) {
    	for (c = 0; c < nodes.length; c++) {
    		
			node = nodes[c];
			txt = "";
			for (c2 = 0; c2 < node.childNodes.length; c2++) {
				txt = txt + serializer.serializeToString(node.childNodes[c2]);
			}
			
			splits = txt.split(/<cdata>|<\/cdata>/g);
			htmlTxt = "";
			for (c2 = 0; c2 < splits.length; c2++) {
				if (c2 % 2 == 1) {
					splits[c2] = splits[c2].replace(/&gt;/g, '>');
					splits[c2] = splits[c2].replace(/&lt;/g, '<');
					splits[c2] = splits[c2].replace(/&amp;/g, '&');
				}
				htmlTxt = htmlTxt + splits[c2]
			}
			// alert(txt);
			//alert($(nodes[c].attributes["id"].value).innerHTML);
			//$(nodes[c].attributes["id"].value).remove();
			//$(nodes[c].attributes["id"].value).replace(serializer.serializeToString(nodes[c].firstChild));
			/*
			if (nodes[c].attributes["updateMode"].value == "html") {
				txt = txt.replace(/&gt;/g, '>');
				txt = txt.replace(/&lt;/g, '<');
				txt = txt.replace(/&amp;/g, '&');
			}
			*/
			
			replaceInner(
				"#"+nodes[c].getAttribute("id"),
				htmlTxt,
				nodes[c].getAttribute("mode"));
		}
	}
    
    nodes = domDocument.selectNodes("/updates/replace");
    
    
    if (nodes.length > 0) {
    	for (c = 0; c < nodes.length; c++) {
    								
			node = nodes[c];
			txt = serializer.serializeToString(node.firstChild);
			splits = txt.split(/<cdata>|<\/cdata>/g);
			htmlTxt = "";
			for (c2 = 0; c2 < splits.length; c2++) {
				if (c2 % 2 == 1) {
					splits[c2] = splits[c2].replace(/&gt;/g, '>');
					splits[c2] = splits[c2].replace(/&lt;/g, '<');
					splits[c2] = splits[c2].replace(/&amp;/g, '&');
				}
				htmlTxt = htmlTxt + splits[c2]
			}
			try {
				Element.replace(nodes[c].getAttribute("id"), htmlTxt);
			}
			catch(err) {
				// Just ignore
			}
		}
	}
	
	// Handling scripts
	
	nodes = domDocument.selectNodes("//script");
	
	if (nodes.length > 0) {
		for (c = 0; c < nodes.length; c++) {
			txt = serializer.serializeToString(nodes[c].firstChild);
			txt = txt.replace(/&gt;/g, '>');
			txt = txt.replace(/&lt;/g, '<');
			txt = txt.replace(/&amp;/g, '&');
			eval(txt);
		}
	}
}

function replaceInner(id, html, mode) {
	try {
		if (mode == "fade") {
			jQuery(id).fadeOut("fast", function() {
				jQuery(id).html(html);
				jQuery(id).fadeIn("fast");
			});
		}
		else {
			jQuery(id).html(html);
		}
	}
	catch(err) {
		//alert(err);
	}
}

function getMouseOffset(target, ev){
	ev = ev || window.event;

	var docPos    = getPosition(target);
	var mousePos  = mouseCoords(ev);
	return {x:mousePos.x - docPos.x, y:mousePos.y - docPos.y};
}

function getPosition(e){
	var left = 0;
	var top  = 0;

	while (e.offsetParent){
		left += e.offsetLeft;
		top  += e.offsetTop;
		e     = e.offsetParent;
	}

	left += e.offsetLeft;
	top  += e.offsetTop;

	return {x:left, y:top};
}

function mouseMove(ev){
	ev           = ev || window.event;
	var mousePos = mouseCoords(ev);
	
	if(dragObject){
		dragObject.style.top      = (mousePos.y - mouseOffset.y) + "px";
		dragObject.style.left     = (mousePos.x - mouseOffset.x) + "px";
		window.status = (mousePos.x) + ":" + (mousePos.y - mouseOffset.y);
		return false;
	}
}
function mouseUp(){
	dragObject = null;
}

function makeDraggable(item){
	if(!item) return;
	
	dragObject  = item;
	if (dragObject.style.position != "absolute") {
		var width = 0;
		var height = 0;
		if (item.style.pixelWidth) {
			width = item.style.pixelWidth;
			height = item.style.pixelWidth;
		} else {
			width = item.offsetWidth;
			height = item.offsetHeight;
		}

		dragObject.style.width = width + "px";
		//dragObject.style.height = height + "px";
		dragObject.style.position = 'absolute';
	}
}

function mouseCoords(ev){
	if(ev.pageX || ev.pageY){
		return {x:ev.pageX, y:ev.pageY};
	}
	return {
		x:ev.clientX + document.body.scrollLeft - document.body.clientLeft,
		y:ev.clientY + document.body.scrollTop  - document.body.clientTop
	};
}


/**
 * json.js:
 * This file defines functions JSON.parse() and JSON.serialize()
 * for decoding and encoding JavaScript objects and arrays from and to
 * application/json format.
 * 
 * The JSON.parse() function is a safe parser: it uses eval() for
 * efficiency but first ensures that its argument contains only legal
 * JSON literals rather than unrestricted JavaScript code.
 *
 * This code is derived from the code at http://www.json.org/json.js
 * which was written and placed in the public domain by Douglas Crockford.
 **/
// This object holds our parse and serialize functions
var JSON = {}; 

// The parse function is short but the validation code is complex.
// See http://www.ietf.org/rfc/rfc4627.txt
JSON.parse = function(s) {
    try {
        return !(/[^,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t]/.test(
                                   s.replace(/"(\\.|[^"\\])*"/g, ''))) &&
            eval('(' + s + ')');
    }
    catch (e) {
        return false;
    }
};

// Our JSON.serialize() function requires a number of helper functions.
// They are all defined within this anonymous function so that they remain
// private and do not pollute the global namespace.
(function () {
    var m = {  // A character conversion map
            '\b': '\\b', '\t': '\\t',  '\n': '\\n', '\f': '\\f',
            '\r': '\\r', '"' : '\\"',  '\\': '\\\\'
        },
        s = { // Map type names to functions for serializing those types
            'boolean': function (x) { return String(x); },
            'null': function (x) { return "null"; },
            number: function (x) { return isFinite(x) ? String(x) : 'null'; },
            string: function (x) {
                if (/["\\\x00-\x1f]/.test(x)) {
                    x = x.replace(/([\x00-\x1f\\"])/g, function(a, b) {
                        var c = m[b];
                        if (c) {
                            return c;
                        }
                        c = b.charCodeAt();
                        return '\\u00' +
                            Math.floor(c / 16).toString(16) +
                            (c % 16).toString(16);
                    });
                }
                return '"' + x + '"';
            },
            array: function (x) {
                var a = ['['], b, f, i, l = x.length, v;
                for (i = 0; i < l; i += 1) {
                    v = x[i];
                    f = s[typeof v];
                    if (f) {
                        v = f(v);
                        if (typeof v == 'string') {
                            if (b) {
                                a[a.length] = ',';
                            }
                            a[a.length] = v;
                            b = true;
                        }
                    }
                }
                a[a.length] = ']';
                return a.join('');
            },
            object: function (x) {
                if (x) {
                    if (x instanceof Array) {
                        return s.array(x);
                    }
                    var a = ['{'], b, f, i, v;
                    for (i in x) {
                        v = x[i];
                        f = s[typeof v];
                        if (f) {
                            v = f(v);
                            if (typeof v == 'string') {
                                if (b) {
                                    a[a.length] = ',';
                                }
                                a.push(s.string(i), ':', v);
                                b = true;
                            }
                        }
                    }
                    a[a.length] = '}';
                    return a.join('');
                }
                return 'null';
            }
        };

    // Export our serialize function outside of this anonymous function
    JSON.serialize = function(o) { return s.object(o); };
})(); // Invoke the anonymous function once to define JSON.serialize()

jQuery.fn.serializeObject = function()
{
   var o = {};
   var a = this.serializeArray();
   jQuery.each(a, function() {
       if (o[this.name]) {
           if (!o[this.name].push) {
               o[this.name] = [o[this.name]];
           }
           o[this.name].push(this.value || '');
       } else {
           o[this.name] = this.value || '';
       }
   });
   return o;
};

contextfw = {	
	handle:     null,
	updateUrl:  null,
	refreshUrl: null,
	removeUrl:  null,
	serializer: new XMLSerializer(),
	
	init: function(handle) {
		this.handle = handle;
		this.updateUrl = "/contextfw-update/"+handle+"?";
		this.refreshUrl = "/contextfw-refresh/"+handle+"?";
		this.removeUrl = "/contextfw-remove/"+handle+"?";
		this.setRefresh();
	},
	
	refresh: function() {
		$.get(this.refreshUrl, null, function() {
			contextfw.setRefresh();
		}); 
	},
	
	setRefresh: function() {
		setTimeout("contextfw.refresh();", 30000);
	},
	
	unload: function() {
		$.get(this.removeUrl);
	},
	
	call: function(elId, method) {
		
		var params = {}
		
		for(var i=2; i< arguments.length; i++) {
	      if (arguments[i] != null && typeof(arguments[i]) == "object") {
	    	  params[elId+".p"+(i-2)] = JSON.serialize(arguments[i]);
	      }
	      else {
	    	  params[elId+".p"+(i-2)] = arguments[i];
	      }
		}
		
		params.el = elId;
		params.method = method;
		
		$("#body").addClass("updating");
		jQuery.post(this.updateUrl + "?check="+ Date.getMilliseconds(), params, function(data, textStatus) {
			contextfw._handleResponse(data);
			$("#body").removeClass("updating");
	    });
	},
	
	_toHtml: function(node) {
		
		var txtArray = [];
		
		for (c2 = 0; c2 < node.childNodes.length; c2++) {
			txtArray.push(this.serializer.serializeToString(node.childNodes[c2]));
		}
		
		txt = txtArray.join("");
		
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
		return htmlTxt;
	},
	
	_handleResponse: function(domDocument) {
		
	    nodes = domDocument.selectNodes("/updates/replace");
	    
	    if (nodes.length > 0) {
	    	for (c = 0; c < nodes.length; c++) {	    								
				node = nodes[c];
				
	    		html = this._toHtml(node);
	    		
				try {
					$j("#"+ node.getAttribute("id")).html(html);
				}
				catch(err) {
					// Just ignore
				}
			}
		}
	    
	    nodes = domDocument.selectNodes("/updates/replaceInner");
	    
	    if (nodes.length > 0) {
	    	for (c = 0; c < nodes.length; c++) {
				node = nodes[c];
	    		html = this._toHtml(node);
	    		
	    		this._replaceInner(
	    				"#"+node.getAttribute("id"),
	    				html,
	    				node.getAttribute("mode"));
	    	}
	    }
	    
	    this._handleScripts(domDocument);
	},
	
	_replaceInner: function replaceInner(id, html, mode) {
		try {
			if (mode == "fade") {
				$(id).fadeOut("fast", function() {
					$(id).html(html);
					$(id).fadeIn("fast");
				});
			}
			else {
				$(id).html(html);
			}
		}
		catch(err) {
			//alert(err);
		}
	},
	
	_handleScripts: function(domDocument) {
		nodes = domDocument.selectNodes("//script");
		if (nodes.length > 0) {
			for (c = 0; c < nodes.length; c++) {
				txt = this._toHtml(nodes[c]);
				txt = txt.replace(/&gt;/g, '>');
				txt = txt.replace(/&lt;/g, '<');
				txt = txt.replace(/&amp;/g, '&');
				eval(txt);
			}
		}
	}
};

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
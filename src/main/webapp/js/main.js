// Non-blocking loading function from http://www.nczonline.net/blog/2009/06/23/loading-javascript-without-blocking/
function loadScript(url, callback) {
	var script = document.createElement("script");
	script.type = "text/javascript";
	script.src = url;
	if (script.readyState) { //IE
		script.onreadystatechange = function() {
			if (script.readyState == "loaded" || script.readyState == "complete") {
				script.onreadystatechange = null;
				callback();
			}
		};
	} else { //Others
		script.onload = callback;
	}
	document.body.appendChild(script);
}


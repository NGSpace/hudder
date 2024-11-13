document.addEventListener("DOMContentLoaded", function (event) {
  basehtml = `
	<style>
		#topbar {
			background-color: #101010;
			position: fixed;
			width: 100%;
			height: 8vh;
			left: 0;
			top: 0;
		}
		#topbar img {
			position: absolute;
			padding-top: 1.2vh;
			padding-left: 1.2vh;
			height: 80%;
		}
		#topbar #title {
			color: lightgray;
			text-align: center;
			padding-top: 0.8vh;
			font-size: 6.7vh;
			font-family: consolas;
		}
		#contents {
			position: absolute;
			text-align: left;
			color: lightgray;
			font-family: "Segoe UI", "Noto Sans", Helvetica, Arial, sans-serif;
			font-size: 1.5em;
			width: calc(84% - 3px);
			left: 15%;
			padding-top: 15vh;
			padding-left: 1%;
			text-wrap: wrap;
			min-height: 85vh;
			z-index: -1;
			top: 0px;
			color:white;
		}
		#sidebar {
			text-align: left;
			color: white;
			font-family: "Segoe UI", "Noto Sans", Helvetica, Arial, sans-serif;
			font-size: 1.3vw;
			width: 15%;
			margin: 0px;
			text-wrap: wrap;
			border: none;
			position: fixed;
			top: 8vh;
			left: 0%;
			background-color: #101010;
			border-right: 3px solid gray;
			min-height: 100%;
		}
		#sidebar h1 {
			margin: 0px;
			padding-left: 20px;
			margin-bottom: 10px;
			border-bottom: 3px solid gray;
		}
		#sidebar details {
			/* I know I could have used list but I decided to hide the button very late into dev and it was not worth it*/
			pointer-events: none; /* prevents the hidden button from being clicked */
			font-size: 1vw;
		}
		#sidebar details a {
			pointer-events: all; /* prevents click events */
			position: relative;
			left: 20px;
			text-decoration: underline;
		}
		#sidebar details summary a {left: 27px;}
		#sidebar details summary {list-style: none;}
		#sidebar details ul {
			list-style: none;
			margin-top: 0px;
			margin-bottom: 0px;
			margin-left: -5px;
			font-size: 0.75vw;
		}
		#sidebar details ul a {
			color: #4f8bd1;
			text-decoration: none;
		}
		p {margin: 10px;}
		h1 {margin: 0px;margin-top: 10px;margin-bottom: 10px;scroll-margin-top: 15vh;}
		h2 {margin: 0px;margin-top: 10px;margin-bottom: 10px;scroll-margin-top: 15vh;}
		a {
			color: #0969da;
			text-decoration: none;
		}
		code {
			display: block;
			font-family: monospace;
			border-radius: 5px;
			border-color: gray;
			border-style: solid;
			border-width: 1px;
			background-color: rgb(59, 59, 59);
			text-align: left;
			padding: 5px;
			white-space: pre;
			width: fit-content;
			color: white;
			tab-size: 4;
			font-size: 1.3em;
		}
		inline-code {
			font-family: monospace;
			border-radius: 5px;
			border-color: gray;
			border-style: solid;
			border-width: 1px;
			background-color: rgb(59, 59, 59);
			text-align: left;
			padding: 0px;
			color: white;
			margin: 0px;
		}
		note {
			display: inline-block;
			border-left: 0.25em solid;
			border-color: #0969da;
			padding: 10px;
			padding-bottom: 10px;
			border-collapse: collapse;
			display: flex;
			align-items: center; /* Align vertical */
			margin-top: 10px;
			color: white;
			margin-bottom: 10px;
		}
		note img {
			padding-right: 10px;
		}
		warning {
			display: inline-block;
			border-left: 0.25em solid;
			border-color: #FF9D00;
			padding: 10px;
			padding-bottom: 10px;
			border-collapse: collapse;
			display: flex;
			align-items: center; /* Align vertical */
			margin-top: 10px;
			color: white;
			margin-bottom: 10px;
		}
		warning img {
			padding-right: 10px;
		}
		
		table td {border: 3px solid gray;}
		table th {border: 3px solid gray;}
		table {border-collapse: collapse;border: 3px solid gray;max-width: 96%;}
	</style>
	<link rel="icon" type="image/x-icon" href="favicon.ico">
	<div id="topbar">
		<img src="Images/Hudder.webp" alt="Back to main hudder wiki page" />
		<div id="title">Hudder 6.2.0 Wiki</div>
	</div>
	<div id="sidebar">
	<h1>Explore</h1>
		<details open>
			<summary>
				<a href="index.html">Home</a>
			</summary>
		</details>
		<br>
		<details open>
			<summary><a href="howto.html">Basic hud</a></summary>
			<ul>
				<li><a href="howto.html#text">Normal text</a></li>
				<li><a href="howto.html#color">Character escaping and Color Codes</a></li>
				<li><a href="howto.html#sections">Text sections</a></li>
				<li><a href="howto.html#variables">Basic variables</a></li>
				<li><a href="howto.html#conditions">Basic conditions</a></li>
				<li><a href="howto.html#adv_variables">Advanced variables and Functions</a></li>
				<li><a href="howto.html#methods">Using methods</a></li>
			</ul>
		</details>
		<details open>
			<summary>
				<a href="adv.html">Advanced hud</a>
			</summary>
			<ul>
				<li><a href="adv.html#arrays">Arrays</a></li>
				<li><a href="adv.html#adv_conds">If statements and while loops</a></li>
				<li><a href="adv.html#adv_methods">Writing your own methods</a></li>
				<li><a href="adv.html#break">The Break variable</a></li>
				<li><a href="adv.html#operators">Inc, Dec and Not operators</a></li>
			</ul>
		</details>
		<details open>
			<summary><a href="vertex.html">Vertex rendering</a></summary>
			<ul>
				<li><a href="vertex.html#varray">Vertex array</a></li>
				<li><a href="vertex.html#tarray">Texture/UV points array</a></li>
				<li><a href="vertex.html#con">"Continous" rendering</a></li>
				<li><a href="vertex.html#circle">Drawing a circle</a></li>
			</ul>
		</details>
		<details open>
			<summary><a href="javascript.html">JavaScript</a></summary>
			<ul>
				<li><a href="javascript.html#sections">Sections? Functions!</a></li>
				<li><a href="javascript.html#functions">List of functions and their descriptions</a></li>
			</ul>
		</details>
		<br>
		<details open>
			<summary>
				<a href="varlist.html">Variable list</a>
			</summary>
		</details>
		<details open>
			<summary>
				<a href="methodlist.html">Method list</a>
			</summary>
		</details>
		<details open>
			<summary>
				<a href="functions.html">Function list</a>
			</summary>
		</details>
		<details open>
			<summary>
				<a href="developers.html">Developers</a>
			</summary>
		</details>
		<br>
		<details open>
			<summary>
				<a href="russian.html">Русский</a>
			</summary>
		</details>
		<script async src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=ca-pub-7455801980495957"
		     crossorigin="anonymous"></script>
		<!-- Sidebar -->
		<ins class="adsbygoogle"
		     style="display:block"
		     data-ad-client="ca-pub-7455801980495957"
		     data-ad-slot="8917303233"
		     data-ad-format="auto"
		     data-full-width-responsive="true"></ins>
		<script>
		     (adsbygoogle = window.adsbygoogle || []).push({});
		</script>
	</div>
	<div id="contents">
	</div>`;
	contents = document.body.innerHTML;
	document.body.innerHTML = basehtml;
	document.getElementById("contents").innerHTML = contents;
	document.body.style = "background-color: #202020";
	
	var notes = document.getElementsByTagName('note');
	for (var i = 0; i < notes.length; i++) {
		let content = notes[i].innerHTML;
		notes[i].innerHTML = "<img src=\"Images/Info.png\" alt=\"INFO\" width=\"24px\">" + content;
	}
	var notes = document.getElementsByTagName('warning');
	for (var i = 0; i < notes.length; i++) {
		let content = notes[i].innerHTML;
		notes[i].innerHTML = "<img src=\"Images/Warning.png\" alt=\"INFO\" width=\"24px\">" + content;
	}
	var codes = Array.from(document.getElementsByTagName('code')).concat(Array.from(
		document.getElementsByTagName("inline-code")));
	for (var j = 0; j < codes.length; j++) {
		if (codes[j].getAttribute("language") != "js") continue;
		let content = codes[j].innerHTML;
		let result = "";
		let builder = "";
		let color = "gray";
		let state = 0;
		for (i = 0; i < content.length; i++) {
			if (content[i]=="\"") {
				for (; i < content.length; i++) {
					builder += content[i];
					if (content[i] == "\"") break;
				}
				result += "<span style=\"color:#E0E000\">" + builder + "</span>";
				builder = "";
				state = 0;
				color = "white";
				continue;
			}
			builder += content[i];
			if (content[i] === "\n"||content[i] === ";") {
				result += "<span style=\"color:" + color + "\">" + builder.substring(0,builder.length-1) + "</span>";
				result += "<span style=\"color:white\">"+content[i]+"</span>";
				color = "gray";
				state = 0;
				builder = "";
			}
			if (content[i] === "(") {
				result += "<span style=\"color:#018ea0\">" + builder.substring(0,builder.length-1) + "</span>";
				result += "<span style=\"color:white\">(</span>";
				builder = "";
			}
			if (content[i] === ")") {
				result += "<span style=\"color:white\">" + builder + "</span>";
				builder = "";
			}
			if (state == 1) {
				if (content[i] === "=") {
					result += "<span style=\"color:#018ea0\">" + builder.substring(0, builder.length - 1) + "</span>";
					result += "<span style=\"color:white\">\=</span>";
					builder = "";
					color = "white";
					state = 2;
				}
				continue;
			}
			if (state == 2) {
				if (content[i] === " ") {
					result += "<span style=\"color:#018ea0\">" + builder + "</span>";
					builder = "";
					color = "#0f770f";
					state = 0;
				}
				continue;
			}
			if (builder === "function " || builder === "return "||builder==="var "||builder==="let ") {
				result += "<span style=\"color:#ea716a\">" + builder + "</span>";
				if (builder==="var "||builder==="let ") {
					color = "#018ea0";
					state = 1;
				}
				builder = "";
			}
			if (content[i] == "\t") {
				result += "\t";
				builder = "";
			}
		}
		result += "<span color=\"" + color + "\">" + builder + "</span>";
		codes[j].innerHTML = result;
	}
	
	calchash();
});

var prevelem;
function calchash() {
	if (prevelem) {
		prevelem.style.background = "none";
		prevelem.style.filter = "none";
		prevelem = null;
	}
	if (window.location.hash) {
		var elem = document.getElementById(window.location.hash.substring(1));
		elem.style.background = "#202020";
		elem.style.filter = "invert(100%)";
		prevelem = elem;
	}
}

addEventListener("hashchange", (event) => {});
onhashchange = (event) => { calchash(); };

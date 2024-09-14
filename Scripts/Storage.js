HighestUserScore = 0;
var Storage = {
	getItem: function (key) {
		if (!(localStorage.getItem("HS")===null))
			return localStorage.getItem("HS");
	},
	setItem: function (key, newvalue) {
		localStorage.setItem("HS", newvalue);
     }
}
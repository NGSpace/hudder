//;throw, can't run worldtime.js with the hudder compiler;

//Thanks you ElectraBytes04 (https://github.com/ElectraBytes04) for allowing me to add this to Hudder!

function topleft() {
	let time;

	if (getVal("worldtime") > 24000) {
    	time = getVal("worldtime") - 24000 * Math.floor(getVal("worldtime") / 24000);
	} else {
    	time = getVal("worldtime");
	}
	
	let hour, minute;
	
	if ((Math.floor(time / 1000) + 6) > 23) {
	    hour = (Math.floor(time / 1000) + 6) - 24;
	} else {
	    hour = (Math.floor(time / 1000) + 6);
	}
	
	minute = Math.floor((time * 60) / 1000) % 60;
	
	let cansleep = false;
	
	if (hour >= 18 && minute >= 32) {
		cansleep = true;
	}

	if (cansleep === false) {
		return "\u00A79Time: \u00A7r" + hour + ":" + (minute < 10 ? "0" + minute : minute);
	} else if (cansleep === true) {
		return "\u00A79Time: \u00A7r" + hour + ":" + (minute < 10 ? "0" + minute : minute) + " - Sleep!";
	}
}
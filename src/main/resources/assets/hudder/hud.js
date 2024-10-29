//;throw, can't run hud.js with the hudder compiler;
function formatString(template, ...args) {
  return template.replace(/{(\d+)}/g, function (match, index) {
    return typeof args[index] === 'undefined' ? match : args[index];
  });
}
function topleft() {
	if (getBoolean("removegui")) compile("hotbar.js", "js");
	
	let itemsize = get("itemsize") !== null?parseInt(get("itemsize")):0.8;
	let gui = getNumber("guiscale");
	let itemscale = (itemsize/gui)*5;
	drawSlot(getNumber("selectedslot"), getNumber("width")/2-(itemscale*8)-(gui==6?0:.5),
		getNumber("height")/2+(itemscale/itemsize*gui)*2.9,itemscale,true);
	return formatString(
		"\u00A7r{0} (max. {1} avg. {2} min. {3}) {4}% of {5}MB\nX: {6}, Y: {7}, Z: {8}",
		
		getNumber("fps"), getNumber("maxfps"),getNumber("avgfps"),getNumber("minfps"),getNumber("usedram_percentage"),
		getNumber("maxram"), getNumber("x"),getNumber("y"),getNumber("z"));
}
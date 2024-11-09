/**;throw, can't run hotbar.js with the hudder compiler;
 * SO. MUCH. FUCKING. MATH. 
 */

const spacing = 24;
let prevslot = 0;
let j = 0;
let scrolly = 0;
let animate = false;//felt annoying when testing with low framerates so I set it to false by default, should still work tho
function createElements() {
	if (!getBoolean("removegui")) {throw new Error("Can not call hotbar.js when remove hotbar is not enabled!");}
	let width = getNumber("width");
	let height = getNumber("height");
	let slot = getNumber("selectedslot");
	let delta = getBoolean("limitrate")? 4:160/getNumber("fps");
	let x = width-20;
	
	if (slot!=prevslot) {
		j = 6;
	}
	let y = height/2-(4.5*spacing)-4;
	let selectedsloty = 0;
	/* The original idea was the selected item will grow big but there were some problems */
	for (let i = 0;i<9;i++) {
		let scale = 1;
		let yoffset = 0;
		let xoffset = 0;
		
		if (!exists("Textures/pointer.png")) {
			//Fallback
			drawTexture("textures/gui/sprites/recipe_book/page_backward.png",x+15, y+i*spacing+5, 6, 6);
		} else {
			drawLocalTexture("Textures/pointer.png",x+14, y+i*spacing+5, 5, 6);
		}
		
		if (slot==i) {
			let sely = y+i*spacing+yoffset-4;
			
			if (animate) {
				let sinc = sely>scrolly;
				if (scrolly==0) scrolly = sely;
				else if (scrolly!=sely) scrolly += (sinc?3:-3)*delta;
				if ((sinc&&scrolly>sely)||(!sinc&&scrolly<sely)) scrolly = sely;
			} else scrolly = sely; //Ye it's for the better...
			
			if (!exists("Textures/selection.png")) {
				//Fallback
				drawTexture("textures/gui/sprites/hud/hotbar_selection.png",x-8, scrolly, 24, 24);
			} else {
				drawLocalTexture("Textures/selection.png",x-8, scrolly, 24, 24);
			}
			
			selectedsloty = scrolly+8;
		}
		xoffset = -5 * (scale/1.6);
		drawSlot(i, x-(scale-1)*16+xoffset, y+i*spacing+yoffset, scale, true);
	}
	
	drawStatusBars(width-96, height-20);
	drawExpAndMountBars(width-96, height-20+5);
	
	let namelen = strWidth(getItem(prevslot).name); //getString("helditem_name") could also be used.
	if (j<=0) drawItemTooltip(width-namelen/2-32, selectedsloty); else j=j-1*delta;
	
	prevslot = slot;
}
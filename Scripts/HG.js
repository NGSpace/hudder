//Audio
var deathSFX = document.getElementById("dea");
var scoreSFX = document.getElementById("sco");

var crashed = false;
var code = "";

var myGamePiece;
var myObstacles = [];
var myScore;
var NHS;
var HST;
var PillerTop = new Image;
PillerTop.src = "Images/Piller_top.png";
var PillerTopUP = new Image;
PillerTopUP.src = "Images/Piller_top_upside.png";
var PillerMid = new Image;
PillerMid.src = "Images/Piller_mid.png";
var PillerBot = new Image;
PillerBot.src = "Images/Piller_bottom.png";

var Paused = true;

function mouseDown() {
    res();
    accelerate(-0.20);
}

function handleStart(evt) {
    if (crashed == true) { res(); return; }
    accelerate(-0.20);
}

function handleEnd(evt) {
    if (crashed == true) { res(); return; }
    accelerate(0.05);
}

function touchClick() {
    var el = document.getElementById("canvas");
    if (el) {
        el.addEventListener("touchstart", handleStart, false);
        el.addEventListener("touchend", handleEnd, false);
        //document.addEventListener("visibilitychange", togPau);
    }
}

//reaches
document.addEventListener("DOMContentLoaded", touchClick);

document.addEventListener('keydown', evt => {
    if (evt.key === 'Escape') {
        togPau();
    }
});

addEvent(document, "keypress", function (e) {
    e = e || window.event;
    var charcode = e.charCode;
    var char = String.fromCharCode(charcode);
    if (char == " " || char == "w") {
        if (crashed == true) { res(); }
        accelerate(-0.2);
    }

    code += char;

    if (code.includes("wdhfs")) { code = ""; console.log("RESETING GAME KEY \'HS\'"); Storage.setItem('HighestUserScore', '0'); location.reload(); }

    if (code.includes("hsloy")) {
        code = "";
        var myDiv = document.getElementById("INP");
        myDiv.type = "text";
        var myDiv2 = document.getElementById("INP2");
        myDiv2.style.display = "block";
    }

    if (code.includes("fdsat")) {
        code = "";
        myGamePiece.inv = !myGamePiece.inv;
    }

    if (code.includes("osdag")) {
        code = "";
        Crash();
    }
});

function click() {
    if (crashed == true) { res(); }
    accelerate(0.1);
}

addEvent(document, "keyup", function (e) {
    e = e || window.event;
    var charcode = e.charCode;
    var char = String.fromCharCode(charcode);
    if (e.key == " " || e.key == "w") {
        if (crashed == true) { res(); }
        accelerate(0.1);
    }
});

function addEvent(element, eventName, callback) {
    if (element.addEventListener) {
        element.addEventListener(eventName, callback, false);
    } else if (element.attachEvent) {
        element.attachEvent("on" + eventName, callback);
    } else {
        element["on" + eventName] = callback;
    }
}

(function () {
    setTimeout(arguments.callee, 250);
})();

function startGame() {

    var el = document.getElementById("canvas");

    //el.getContext("2d").scale(document.height, document.width);
    el.addEventListener("onmouseup", click, false);
    if (typeof (Storage) !== "undefined") {
        if (Number.isNaN(HighestUserScore)) { Storage.setItem('HighestUserScore', 0); }
        else { HighestUserScore = Storage.getItem(""); }
    } else {
        console.error('Storage.js was not Found');
        return;
    }

    PHS = Storage.getItem("HighestUserScore");

    CreateScreenElements();

    myGameArea.start();
}

var myGameArea = {
    canvas: document.getElementById("canvas"),
    start: function () {
        this.context = this.canvas.getContext("2d");
        this.frameNo = 0;
        this.frameSc = 0;
        this.interval = setInterval(updateGameArea, 20);
    },
    restart: function () {
        this.context = this.canvas.getContext("2d");
        this.frameNo = 0;
        this.frameSc = 0;
    },
    clear: function () {
        this.canvas.getContext("2d").clearRect(0, 0, this.canvas.width, this.canvas.height);
    }
}

ctx = document.getElementById("canvas").getContext("2d");

function CreateScreenElements() {
    myGv = new component("300px", "Consolas", "black", 325, 700, "text");
    NHS = new component("100px", "Consolas", "black", 380, 875, "text");
    SM_TE = new component("100px", "Consolas", "black", 340, 900, "text");
    
    myGamePiece = new component(30, 30 * 3.2, "red", 300, 540 + 30 * 3.2);
    myGamePiece.gravity = 0.25;
    myScore = new component("200px", "Consolas", "black", 780, 300, "text");
    HST = new component("100px", "Consolas", "black", 805, 400, "text");
    Goal = new component("16px", "Consolas", "black", 10, 60, "text");
}

function component(width, height, color, x, y, type) {
    this.type = type;
    this.score = 0;
    this.width = width;
    this.height = height;
    this.color = color;
    this.speedX = 0;
    this.speedY = 0;
    this.x = x;
    this.y = y;
    this.gravity = 0;
    this.gravitySpeed = 0;
    this.inv = false;
    this.update = function () {
        ctx = document.getElementById("canvas").getContext("2d");
        if (this.type == "text") {
            ctx.font = this.width + " " + this.height;
            ctx.fillStyle = this.color;
            ctx.fillText(this.text, this.x, this.y, ctx.measureText(this.text).width / 3.2);
        } else if (this.type == "piller") {
            ctx.fillStyle = color;
            ctx.drawImage(PillerTop, this.x, this.y, this.width, 71);
            ctx.drawImage(PillerMid, this.x, this.y + 71, this.width, this.height - 71);
        } else if (this.type == "pillertop") {
            ctx.fillStyle = color;
            ctx.drawImage(PillerTop, this.x, this.y + this.height-71, this.width, 71);
            ctx.drawImage(PillerMid, this.x, this.y, this.width, this.height - 71);
        } else {
            ctx.fillStyle = color;
            ctx.fillRect(this.x, this.y, this.width, this.height);
        }
    }
    this.newPos = function () {
        this.gravitySpeed += this.gravity;
        this.x += this.speedX;
        this.y += this.speedY + this.gravitySpeed;
        this.hitBottom();
    }
    this.rePos = function () {
        this.gravitySpeed += this.gravity;
        this.y += this.speedY + this.gravitySpeed;
        this.hitBottom();
        if (this.y < 0) {
            this.y -= this.speedY + this.gravitySpeed;
            this.speedY = 0.0;
            this.gravitySpeed = -0.05;
            this.y -= this.speedY + this.gravitySpeed;
        }
    }
    this.hitBottom = function () {
        var rockbottom = myGameArea.canvas.height - this.height;
        if (this.y > rockbottom) {
            this.y = rockbottom;
            this.gravitySpeed = 0;
        }
    }
    this.crashWith = function (otherobj) {
        if (this.inv == false) {
            var myleft = this.x;
            var myright = this.x + (this.width);
            var mytop = this.y;
            var mybottom = this.y + (this.height);
            var otherleft = otherobj.x;
            var otherright = otherobj.x + (otherobj.width);
            var othertop = otherobj.y;
            var otherbottom = otherobj.y + (otherobj.height);
            var crash = true;
            if ((mybottom < othertop) || (mytop > otherbottom) || (myright < otherleft) || (myleft > otherright)) {
                crash = false;
            } else {
                U = true;
            }
            return crash;
        } else { return false; }
    }
}

function togPau() {
    Paused = !Paused;
}

var l = 0;
var l1 = 0;
var l2 = 800;
var l3 = 0;

var TempColor = "black";
var TempTalkingText = "";

const Texts = ["OH", "I know what to do!", "", "A random NGS has appeared!!!", "", ""]

e = 300;
Speed = 2;

function updateGameArea() {

    if (crashed) return;
    if (Paused == false) { return; }

    if (document.getElementById("BG").style.backgroundPositionX == "") {
        document.getElementById("BG").style.backgroundPositionX = "1px";
    }
    //console.log((document.getElementById("BG").style.backgroundPositionX));
    document.getElementById("BG").style.backgroundPositionX = 
        parseFloat(document.getElementById("BG").style.backgroundPositionX.slice(0, -2)) - .4 + "px";

    var x, height, gap, minHeight, maxHeight, minGap, maxGap;
    for (i = 0; i < myObstacles.length; i++) {
        if (myGamePiece.crashWith(myObstacles[i])) {
            Crash();
            return;
        } else {
            myGv.text = "";
            NHS.text = "";
            SM_TE.text = "";
            SM_TE.update();
            NHS.update();
            myGv.update();
        }
    }

    var x, height, gap, minHeight, maxHeight, minGap, maxGap;
    for (i = 0; i < myObstacles.length; i++) {
        if (i != 0) {
            if (myObstacles[i - 1].x != myObstacles[i].x) {
                if (UpdateScore(myObstacles[i])) {
                    Score("Th1sIsTh3KeyT0PP");
                }
            }
        } else {
            if (UpdateScore(myObstacles[i])) {
                Score("Th1sIsTh3KeyT0PP");
            }
        }
        myObstacles[i].x += Math.max(-Speed, -2);
        myObstacles[i].update();
    }

    myGameArea.clear();
    myGameArea.frameNo+=1;
    if (myGameArea.frameNo == 1 || everyinterval(Math.max(e,150))) {
        x = myGameArea.canvas.width;
        minHeight = 200;
        maxHeight = 800;
        height = Math.floor(Math.random() * (maxHeight - minHeight + 1) + minHeight);
        minGap = 250;
        maxGap = 700;
        gap = Math.floor(Math.random() * (maxGap - minGap + 1) + minGap);
        myObstacles.push(new component(40, height, "green", x, 0, "pillertop"));
        myObstacles.push(new component(40, x * 1000 - height - gap, "green", x, height + gap, "piller"));
    }

    for (i = 0; i < myObstacles.length; i++) {
        myObstacles[i].update();
    }

    myScore.text = "SCORE: " + (myGameArea.frameSc);
    HST.text = "HIGH SCORE: 0";
    Goal.text = "Goal: Beat the HighScore";

    if (!(Number.isNaN(HighestUserScore)) && HighestUserScore != -1) {
        HST.text = "HIGH SCORE: " + HighestUserScore;
    } else {
        HST.text = "";
    }

    HST.update();
    myScore.update();

    myGamePiece.rePos();
    myGamePiece.update();
}

function Score(key) {
    myGameArea.frameSc += 0.5;
    scoreSFX.currentTime = 0;
    e -= 1;
    //Speed += 0.5;
    scoreSFX.play();
    if (myGameArea.frameSc > HighestUserScore) {
        Storage.setItem(key, String(myGameArea.frameSc));
        HST.text = "HIGH SCORE: " + HighestUserScore;
        HST.update();
    }
}

function Crash() {
    myGv.text = "Game Over";
    if (PHS < HighestUserScore) {
        NHS.text = "New High Score: " + myGameArea.frameSc;
        NHS.update();
        SM_TE.y = 1050;
    }
    SM_TE.text = "Click Again to Restart";
    SM_TE.update();
    myGv.update();
    if (crashed != true) { deathSFX.play(); }
    crashed = true;
}

function everyinterval(n) {
    if (myGameArea.frameNo >= n) {
        console.log(myGameArea.frameNo + "     " + n);
        myGameArea.frameNo = 1;
        return true;
    }
    //if ((myGameArea.frameNo / n) % 1 == 0) { return true; }
    return false;
}

function accelerate(n) {myGamePiece.gravity = n;}

document.onkeypress = function (e) {e = e || window.event;};

function UpdateScore(otherobj) {
    var myleft = myGamePiece.x;
    var myright = myGamePiece.x;
    var mytop = myGamePiece.y;
    var mybottom = myGamePiece.y + (1000);
    var otherleft = otherobj.x;
    var otherright = otherobj.x;
    var othertop = otherobj.y;
    var otherbottom = otherobj.y + (1000);
    var crash = true;
    if ((myright < otherleft) || (myleft > otherright)) {
        crash = false;
    }
    return crash;
}

function res() {
    if (crashed == true) {
        document.getElementById("BG").style.backgroundPositionX = "1px";
        crashed = false;
        myObstacles = [];
        myGamePiece.speedX = 0;
        myGamePiece.speedY = 0;
        myGamePiece.gravity = 0.05;
        CreateScreenElements();
        if (typeof (Storage) !== "undefined") {
            if (Number.isNaN(HighestUserScore)) { Storage.setItem('HighestUserScore', 0); }
            else { HighestUserScore = Storage.getItem(""); }
        } else {
            console.error('Storage.js was not Found');
            return;
        }
        PHS = HighestUserScore;
        e = 300;
        HST.text = "HIGH SCORE: " + HighestUserScore;
        SM_TE.update();
        HST.update();
        myGameArea.restart();
        accelerate(0.05);
    }
}

//Unused
function Debug() {

    Storage.setItem('HighestUserScore', '0');
    HST.text = "HIGH SCORE: " + HighestUserScore;
    HST.update();

}

function executeFunctionByName(functionName, context) { executeFunctionByName(functionName, context, null); }

function executeFunctionByName(functionName, context, args) {
    var args = Array.prototype.slice.call(arguments, 2);
    var namespaces = functionName.split(".");
    var func = namespaces.pop();
    for (var i = 0; i < namespaces.length; i++) {
        context = context[namespaces[i]];
    }
    if (context != null) {
        return context[func].apply(context, args);
    } else {
        context = window;
        return context[func].apply(context, args);
    }
}

function exeusefun(func) { executeFunctionByName(func, null, null); }

function getTextWidth(text, font) {
    // re-use canvas object for better performance
    const canvas = getTextWidth.canvas || (getTextWidth.canvas = document.createElement("canvas"));
    const context = canvas.getContext("2d");
    context.font = font;
    const metrics = context.measureText(text);
    return metrics.width;
}
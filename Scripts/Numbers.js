const defaultg = 5;
const maximum = 100;

var textBox;
var button;
var resultBox;
var guessesBox;

var number;
var guesses = defaultg;
var r = false;

document.addEventListener("DOMContentLoaded", function(event) { 
	textBox = document.getElementById("TextBox");
	button = document.getElementById("Button");
	resultBox = document.getElementById("ResultBox");
	guessesBox = document.getElementById("GuessesBox");
	r = true;
	Click();
});

function Click() {
	if (r) {
		number = getRandomInt(maximum);
		SetGuesses(defaultg);
		r = false;
		button.value = "Guess";
		resultBox.innerHTML = "";
		textBox.value = "";
		return;
	}
	var num = Number(textBox.value);
	console.log(num);
	if (num===number) {
		RightGuess();
		button.value = "Restart";
		r = true;
	} else {
		SetGuesses(guesses-1);
		resultBox.style.color = "red";
		if (!isNaN(num)) {
			if (num>number)
				resultBox.innerHTML = "TOO BIG";
			else if (num<number)
				resultBox.innerHTML = "too small";
		} else 
			resultBox.innerHTML = "Not a number";
		if (guesses===0) {
			resultBox.innerHTML = "You're A failuae!";
			button.value = "Restart";
			r = true;
		}
	}
}
function getRandomInt(max) {
  return Math.floor(Math.random() * max);
}
function RightGuess() {
	resultBox.style.color = "green";
	resultBox.innerHTML = "You're correct!";
}
function SetGuesses(num) {
	guessesBox.innerHTML = "You got " + num + " guesses";
	guesses = num;
}

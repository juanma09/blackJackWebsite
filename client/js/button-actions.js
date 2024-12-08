// This file contains key interactions that will occur after the player has clicked a button
var joinGame = async function() {
	if (playerId == -1) {
        playerId = Math.floor(Math.random() * 10000).toString(); // Generate unique ID
        localStorage.setItem("playerId", playerId);
    }

	
	playerId = document.getElementById('name').value;
	
	if (!playerId)
	{
		Materialize.toast("You need to input a name", 3000);
		return;
	}
		
	if (playerId.length < 4)
	{
		Materialize.toast("Name should be longer than 4 characters", 3000);
		return;
	}
		
	joinGameButton.hide("fade", 200);
	loadingRing.show("fade", 200);
    const requestText = { "playerId": playerId };
	console.log(requestText);
	postRequestData("join-game", requestText)
	.then((response) => {
		console.log(playerId);
		console.log(response);

		if (response.status == "error")
		{
			Materialize.toast('Game already in course', 3000);
			joinGameButton.show("fade", 200);
			loadingRing.hide("fade", 200);
			return;
		}

		hearbeat();
		$("#join-game").hide();
		$("#welcome").show("fade", 1000);
		$("#game-over").hide();
		$(".brand-logo").text("blackjack");
		$("#game-board").hide();
	})
	.catch((error) => {
		console.error("Error while joining the game:", error.message);

		joinGameButton.show("fade", 200);
		loadingRing.hide("fade", 200);
	});
}


var startGame = async function() {
	getCards();
	dealerGameBoard.empty();
	playerGameBoard.empty();
	dealerHand = [];
	dealerHandTotal = 0;
	cardsInDeck = cards;
	gameWinner = "none";
	dealerHand = [];
	dealerHandTotal = 0;
	dealerStatus = "start";
	playerHand = [];
	playerHandTotal = 0;
	playerStatus = "start";  
	playerHasAce = false;  
	splitGame = false; 
	isGameOver = false;
	playerSplitHand = [];
	playerSplitHandTotal = 0;
	playerSplitStatus = "start";

	enableButton(standButton, stand);
	enableButton(hitButton, hit);

	betRequest = {"playerId": playerId, "amount": currentWager};
	betResponse = await postRequestData("set-bet", betRequest);
	console.log(betResponse);

	if (currentWager === 0) {
		Materialize.toast("You must select a bet to play", 1000);
	} else if (currentChipBalance < 10) {
		Materialize.toast("You're out of chips! Reset the game to continue" , 2000);
	} else if (currentChipBalance < currentWager) {
		Materialize.toast("Insufficient chip balance, please select a lower bet" , 1500);
	} else {

		currentChipBalance -= currentWager;
		updateVisibleChipBalances();
		$("#join-game").hide();
		$("#welcome").hide();
		$("#game-over").hide();
		$(".brand-logo").text("blackjack"); 
		$("#game-board").show("fade", 1000);
		cardsInDeck = cards;
		cardsInDeck.sort(function() {return 0.5 - Math.random()});

		// Get player cards
		console.log(playerId);
		var requestText = {"playerId": playerId.toString()}
		pCards = await getRequestData("player-hand", requestText);
		pCards = JSON.parse(pCards);
		console.log(pCards);
		for (let i = 0; i < pCards.length; i++) {
			setTimeout(function(){
				currentTurn = "player";
				dealCard(playerHand, playerGameBoard, "player", pCards[i].full);
				//currentTurn = "dealer";
				//dealCard(dealerHand, dealerGameBoard, "dealer");
			}, i*500);
		}


		setTimeout(function(){
			currentTurn = "player";
			if (playerHand.length === 2 && playerHand[0].name === playerHand[1].name) {
				enableButton(splitButton, split);
			}
		}, 2500);
			
	}
}

var hit = async function() {
	disableButton(hitButton);
	if (currentTurn === "player") {
		var requestText = {"playerId": playerId}
		console.log(requestText);
		card = await postRequestData("hit", requestText);
		console.log(card);
		playerStatus = "hit";
		dealCard(playerHand, playerGameBoard, "player", card.card);
	}
	setTimeout(() => {
		enableButton(hitButton, hit);
	}, 1000);
}

var stand = function() {
	if (currentTurn === "player") {
		currentTurn = "dealer";
		changeHand(playerStatus);
	} 
	console.log(currentTurn);
}

var split = function() {
	splitGame = true; 
	playerHandTotal = playerHandTotal - playerHand[1].value;
	playerSplitHandTotal = playerHand[1].value;
	updateVisibleHandTotals();
	$(".split-hand-total").removeClass("inactive").show(); 
	$(playerSplitGameBoard).removeClass("inactive").show();	
	var splitCard = playerHand.pop();
	playerSplitHand.push(splitCard);
	var cardImage = $("#player-card-1").attr("id", "playerSplit-card-0");

	cardImage.hide(); // Hide it at first to allow for the transition to occur
	// This is the first card in the deck, so want to cancel out the previous offset/stacking and have it go to the initial normal spot
	cardImage.appendTo($(playerSplitGameBoard)).offset({left: 60}).css("margin-right", "auto").show();

	currentChipBalance -= currentWager; 
	currentWager = currentWager * 2;
	updateVisibleChipBalances();

	// Then, deal 1 new card for each newly split deck
	currentTurn = "player";
	dealCard(playerHand, playerGameBoard, "player");
	currentTurn = "playerSplit";
	dealCard(playerSplitHand, playerSplitGameBoard, "playerSplit");

	// Make split button no longer clickable as in this game you can only split once
	disableButton(splitButton);

	// Shrink the inactive deck to both signal what deck they are playing and to make room on the board
	setTimeout(function(){
		scaleDownDeck(playerSplitGameBoard, playerSplitHandTotalDisplay);
		currentTurn = "player"; 
	}, 1000);

}

function doubleDown() {
	if (currentChipBalance - currentWager <= 0) {
		Materialize.toast("Insufficient chip balance" , 1000);
	}
	else {
		currentChipBalance -= currentWager; //subtracts the same value again from current balance
		currentWager = currentWager * 2;
		updateVisibleChipBalances();
		disableButton(doubleDownButton);
	}
}

var startGameInterval;
async function startGameFunc() {
	postRequestData('start-game', {"playerId": playerId});
	$(loadingRingStart).show("fade", 200);
	$(startButton).hide("fade", 200);
	startGameInterval = setInterval(async () => {
		try {
			var response = await getRequestData('game-state');
			response = JSON.parse(response);
			if (response.gameState === "PLAYER_TURN") {
				console.log("Condition met!");

				$(loadingRingStart).hide("fade", 200);
				$(startButton).show("fade", 200);
				startGame();
				clearInterval(startGameInterval); // Stop the interval
			} else {
				$(loadingRingStart).show("fade", 200);
				$(startButton).hide("fade", 200);
				console.log("Condition not met yet.");
			}
		} catch (error) {
			console.error("Error checking condition:", error);
			$(loadingRingStart).show("fade", 200);
			$(startButton).hide("fade", 200);
			clearInterval(startGameInterval); // Stop interval if an error occurs
		}
	}, 1000);
	
}

function newGame()
{
	$("#game-over").hide();
	$("#welcome").show("fade", 500);
	startButton.show("fade", 200);
	loadingRingStart.hide();
	
	getCards();
	cardsInDeck = cards;
	gameWinner = "none";
	dealerHand = [];
	dealerHandTotal = 0;
	dealerStatus = "start";
	playerHand = [];
	playerHandTotal = 0;
	playerStatus = "start";  
	playerHasAce = false;  
	splitGame = false; 
	isGameOver = false;
	playerSplitHand = [];
	playerSplitHandTotal = 0;
	playerSplitStatus = "start";

	if (currentWager === 0) { 
		Materialize.toast("You must select a bet to play", 1000);
	} else {	
		$(playerSplitGameBoard).hide();
		$(".split-hand-total").hide();
		enableButton(standButton, stand);
		enableButton(hitButton, hit);
		enableButton(doubleDownButton, doubleDown);
		dealerGameBoard.empty();
		playerGameBoard.empty();
		playerSplitGameBoard.empty();
		updateVisibleHandTotals();		
	}
}

function resetGame() {
	stopHeartbeat();
	$("#game-over").hide();
	$("#welcome").show("fade", 500);
	$(".brand-logo").text("blackjack"); 
	$("#game-board").hide();
}
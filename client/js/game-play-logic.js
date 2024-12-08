// This file contains the main logic utilized during active gameplay, before the game is declared over

function dealCard(hand, location, currentTurn, cardstr) {
	//console.log(cardstr);
	var cardDrawn = cards.find(card => card.full === cardstr);
	if (!cardDrawn)
	{
		//console.log(cardstr);
		console.log("ERROR: No card found");
		return;
	}

	if (hand.includes(cardDrawn)) return;
	
	hand.push(cardDrawn);
	//removeItemAll(cardsInDeck, cardDrawn);
	
	var index = hand.length - 1;

	// Create card image for card, hide initially so it doesn't impact transition
	var cardImage = $("<img>").attr("class", "card").attr("src", "img/" + hand[index].src).hide();
	cardImage.attr("id", currentTurn + "-card-" + index);


	// To create stacked card effect
	if (index === 0) {
		cardImage.appendTo($(location)).show();
	} else if (index > 0) {
		cardImage.appendTo($(location)).offset({left: -60}).css("margin-right", -60).show();	
	} 
	if (hand[index].name === "ace" && currentTurn != "dealer") {
		playerHasAce = true;
	}
	// Note: tried to dry this out by putting totals as a param but couldn't get it working yet
	if (currentTurn === "player") {
		playerHandTotal += hand[index].value;
	} else if (currentTurn === "playerSplit") {
		playerSplitHandTotal += hand[index].value;
	} else if (currentTurn === "dealer") {
		dealerHandTotal += hand[index].value;
	}	
	// Second card only for dealer should show face down
	if (dealerHand.length === 2 && currentTurn === "dealer") {
		cardImage.attr("src", "img/card_back.png");
	}
	updateVisibleHandTotals();
	evaluateGameStatus();
}


async function evaluateGameStatus() {
	// Player can only split or double down after first 2 cards drawn
	if (playerHandTotal >= 21)
	{
		currentTurn = "dealer";
		dealerStatus = "hit";
	}

	if (currentTurn === "dealer" && dealerStatus === "hit") {
		requestText = {"playerId": playerId};
		var response = await postRequestData('turn-over', requestText);

		console.log("=== turn over ====");
		console.log(response);
		console.log("=== ========= ====");
		await dealerPlay();
	}
}


// The purpose of this function is to detect when a turn should be shifted without the player
// needing to click "stand". This is also an important step for determining what the next move
// is if there is a split deck. 
function isPlayerDone() {

}

function changeHand(currentDeckStatus) {
	currentDeckStatus = "stand";
	
	currentTurn = "dealer";
	dealerStatus = "hit";

	evaluateGameStatus(); 
}

function reviewAcesValue(hand, total) {	
	if (total > 21) {
		// If they have exactly 2 aces in the first draw, prompt them to choose to split or not
		if (hand.length === 2) {  
			enableButton(splitButton, split);
			$("#two-aces-prompt").modal("open");
		// Otherwise, just reduce the aces value so they are no longer over 21
		} else if (hand.length > 2) {
			reduceAcesValue(hand);
			isPlayerDone();
		}
	} else {
		isPlayerDone();
	}
}

function reduceAcesValue(deck) {
	for (var i = 0; i < deck.length; i++) {  
		if (deck[i].name === "ace" && deck[i].value === 11) { // Only focusing on aces that haven't been changed from 11 to 1 already
			deck[i].value = 1;
			if (currentTurn === "player") {
				playerHandTotal -= 10;
			} else if (currentTurn === "playerSplit") {
				playerSplitHandTotal -= 10;
			}
			updateVisibleHandTotals();
			Materialize.toast("Your ace value changed from 11 to 1", 1500);
		}	
	}
}

async function dealerPlay() {
	flipHiddenCard();
	disableButton(standButton);
	disableButton(hitButton);
	disableButton(splitButton);
	var response = {"isDealerTurn": "0"};

	setTimeout(async () => {
			response = await getRequestData("dealer-hand");
			dealToDealer(response.dealerHand);
		}
	, 1000);
	
	if (gameState == "DEALER_TURN")
	{
		if (dealerHandTotal >= 21) {
			setTimeout(function(){
				gameOver();
			}, 1100);
		} else if (dealerHandTotal >= 17) {
			setTimeout(function(){
				gameOver();
			}, 1100);
		}
	}
}

async function dealToDealer(dCards)
{
	if (!dCards) return;
	//console.log(dealerHand);
	
	for (let i = 0; i < dealerHand.length; i++)
	{
		if (!dCards.find(card => card.full === dealerHand[i].full))
		{
			dealerHand.splice(i,1);
		}
	}
	for (let i = 0; i < dCards.length; i++) 
	{ 
		dealCard(dealerHand, dealerGameBoard, "dealer", dCards[i].full);
	}
}

let dealerInterval;
let isDealerPlayRunning = false;

function startDealerPlay(interval = 5000) {
    dealerInterval = setInterval(async () => {
		var response = await getRequestText("game-state")
		if (response.gameState != "DEALER_TURN") return;
        if (!isDealerPlayRunning) {
            isDealerPlayRunning = true;
            await dealerPlay(); // Run the dealerPlay function
            isDealerPlayRunning = false;
        }
    }, interval);
}

function stopDealerPlay() {
    clearInterval(dealerInterval); // Stop the interval
}
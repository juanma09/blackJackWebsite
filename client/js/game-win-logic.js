// This file contains logic for what happens after the round is determined to be over

async function gameOver() {

	isGameOver = true;
	
	setTimeout(function(){
		flipHiddenCard();
	}, 750);
	disableButton(standButton);
	disableButton(hitButton);
	disableButton(splitButton);
	disableButton(doubleDownButton);
	if (dealerHandTotal === 21) {
		if (playerHandTotal === 21) {
			gameWinner = "tie";
		} else {
			gameWinner = "dealer";
		}
	} else if (dealerHandTotal > 21) {
		if (playerHandTotal <= 21) {
			gameWinner = "player";
		}  else {
			gameWinner = "tie";
		}
	} else if (dealerHandTotal < 21) {
		if (playerHandTotal === 21) {
			gameWinner = "player";
		}
		else if (playerHandTotal > dealerHandTotal)
		{
			gameWinner = "player";
		}
		 else {
			gameWinner = "dealer";
		}
	}
	stopDealerPlay();
	setTimeout(announceWinner, 2500); // Slight delay to give time to see the final cards play out
} 

async function updateChipBalance(bet, balance) {
	currentChipBalance = balance;
	//currentWager = bet;
	/*
	if (gameWinner === "player") {
		// Blackjack is 3:2 payout (and cannot occur on a split deck):
		if (splitGame === false && playerHasAce === true && playerHandTotal === 21 && playerHand.length === 2) {
			currentChipBalance += currentWager*(3/2) + currentWager;
		// Otherwise it's a 1:1 payout:
		} else {
			currentChipBalance += currentWager*2;
		}
	// If you tie, get just original wager back (no win or loss)
	} else if (gameWinner === "tie") {
		currentChipBalance += currentWager;		
	}*/
	// Note: if dealer wins, nothing happens to player chip balance as their wager was already removed from it
	updateVisibleChipBalances();
}

function announceWinner() {
	updateVisibleHandTotals();
	currentWager = 0;
	updateVisibleChipBalances();
	$("#game-board").hide();
	enlargeDeck(playerSplitGameBoard, playerSplitHandTotalDisplay);
	enlargeDeck(playerGameBoard, playerHandTotalDisplay);

	// Move betting options from welcome screen to game over screen to play again
	//$("#wager-options").appendTo($("#game-over")); 
	//$(playAgainButton).appendTo($("#wager-options")); // to move to bottom of container
	

	if (gameWinner === "player" && playerHandTotal === 21) {
		Materialize.toast("You won! Blackjack!", 2000);
	} else if (gameWinner === "player")
	{
		Materialize.toast("You won!", 2000);
	} else if (gameWinner === "dealer") {
		Materialize.toast("You won!", 2000);
	} else if (gameWinner === "tie") {
		Materialize.toast("You won!", 2000);
	}
}
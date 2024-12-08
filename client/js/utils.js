var heartbeatInterval;
var gameState;
function hearbeat()
{
    heartbeatInterval = setInterval(async () => {
        var response = await postRequestData('heartbeat', {"playerId": playerId});
        console.log(response);
        gameState = response.gameState;
        dealToDealer(response.dealerHand);
        updateChipBalance(response.bet, response.balance);
        checkWindow(response.playerState);
        checkIfCanStart(response.gameState, response.playerState);
        ManagePlayerDisplay(response.players);
        checkIfGameOver(response.gameState, response.playerState);
        checkPlayersLeave(response.players);
    }, 2000); 
}

function checkWindow(playerState)
{
  if (playerState === "LOBBY" && $("#game-board").is(":visible"))
  {
      $("#game-over").hide();
      $("#welcome").show();
      $(".brand-logo").text("blackjack"); 
      $("#game-board").hide();
  }
}

function checkIfCanStart(gameState, playerState)
{
  if (gameState === "WAITING_FOR_PLAYERS" && playerState === "LOBBY")
  {
      clearInterval(startGameInterval);
      console.log('CAN JOIN');
      $("#game-over").hide();
      $("#welcome").show();
      $(".brand-logo").text("blackjack"); 
      $("#game-board").hide();
      startButton.show();
      loadingRingStart.hide();
  }

}

function stopHeartbeat()
{
    clearInterval(heartbeatInterval);
}

function checkIfGameOver(gameState)
{
  if (gameState === "WAITING_FOR_PLAYERS")
  {
    deleteAllCards();
  }
}

function removeItemAll(arr, value) {
    var i = 0;
    while (i < arr.length) {
      if (arr[i] === value) {
        arr.splice(i, 1);
      } else {
        ++i;
      }
    }
    return arr;
  }

function ManagePlayerDisplay(players)
{
  for (let i = 0; i < players.length; i++)
  {
    if (players[i].id === document.getElementById('name').value)
        continue;

    if (!playerNames.find(player => (players[i].id === player)))
    {
      playerNames.push(players[i].id);
      addPlayerToDisplay();
    }
    var index = playerNames.indexOf(players[i].id);
    console.log(players[i].hand);
    for (let j = 0; j < players[i].hand.length; j++)
    {
      addCardToPlayer(index, players[i].hand[j].full);
    }
    updatePlayerDetails(index, players[i].id, players[i].balance);
    

  }
}


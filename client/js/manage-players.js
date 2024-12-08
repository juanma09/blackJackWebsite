// Reference to the container div
const playerHands = document.getElementById("player-hands");
var playerNames = [];

/**
 * Adds a new column to the #player-hands div
 */
function addPlayerToDisplay() {
  const col = document.createElement("div");
  col.className = "col";
  col.innerHTML = `
    <div class="player-hand"></div>
    <div class="">
      <h5 class="player-name">New Player</h5>
      <p class="player-balance">0</p>
    </div>
  `;
  playerHands.appendChild(col);
}

/**
 * Adds an image to the player-hand of a specific column
 * @param {number} columnIndex - The index of the column
 * @param {string} cardStr - The card in format "rank-suit"
 */
function addCardToPlayer(columnIndex, cardStr) {
    var cardDrawn = cards.find(card => card.full === cardStr);
    if (!cardDrawn)
    {
      console.log(cardStr);
      return;
    }
    const columns = playerHands.getElementsByClassName("col");
    
    if (columnIndex < columns.length) {
      const playerHand = columns[columnIndex].querySelector(".player-hand");
      
      const imgs = playerHand.querySelectorAll("img");
      if (imgs)
      {
        for (let i = 0; i < imgs.length; i++)
        {
          if (imgs[i].id === cardStr)
          {
            return;
          }
        }
        
      }

      // Create a new image element
      const img = document.createElement("img");
      img.className = "player-card";
      img.src = "img/" + cardDrawn.src;
  
      // Apply styles only to images added after the first one
      const currentImages = playerHand.getElementsByClassName("player-card");
      if (currentImages.length > 0) {
        img.style.marginLeft = "-30px";
        img.style.left = "-5px";
      }
      
      img.id = cardDrawn.full;
      // Append the image to the player hand
      playerHand.appendChild(img);
      setTimeout(() => {
        img.classList.add("animate"); // Apply final animation state
      }, 10); 
    } else {
      console.error("Column index out of range.");
    }
  }
  

/**
 * Updates the player name and balance of a specific column
 * @param {number} columnIndex - The index of the column
 * @param {string} playerName - New name for the player
 * @param {string} playerBalance - New balance for the player
 */
function updatePlayerDetails(columnIndex, playerName, playerBalance) {
  const columns = playerHands.getElementsByClassName("col");
  if (columnIndex < columns.length) {
    const playerNameEl = columns[columnIndex].querySelector(".player-name");
    const playerBalanceEl = columns[columnIndex].querySelector(".player-balance");

    playerNameEl.textContent = playerName;
    playerBalanceEl.textContent = playerBalance;
  } else {
    console.error("Column index out of range.");
  }
}

function deleteAllCards()
{
  const columns = playerHands.getElementsByClassName("col");

  for (let i = 0; i < columns.length; i++)
  {
      var playerHand = columns[i].querySelector(".player-hand");
      playerHand.innerHTML = "";
  }
}

function checkPlayersLeave(players)
{
  const columns = playerHands.getElementsByClassName("col");
  for (let i = 0; i < columns.length; i++)
  {
    const playerNameEl = columns[i].querySelector(".player-name");
    const playerBalanceEl = columns[i].querySelector(".player-balance");
    if (!players.find(player => (playerNameEl.textContent === player.id)))
    {
      playerNameEl.style.opacity = "0.6";
      playerBalanceEl.style.opacity = "0.6";
    }
    else
    {
      playerNameEl.style.opacity = "1";
      playerBalanceEl.style.opacity = "1";
    }
  }
}
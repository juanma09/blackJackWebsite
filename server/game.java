import java.util.ArrayList;  
import java.util.List;       
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;    


public class game {
    public enum GameState {
        WAITING_FOR_PLAYERS,
        PLACING_BETS,
        DEALING_CARDS,
        PLAYER_TURN,
        DEALER_TURN,
        GAME_OVER;
    }

    private deck deck;                
    private dealer dealer;            
    private List<player> playerSet;   
    private Scanner scanner;      
    private GameState state;    
    
    private static game instance = null;

    // Constructor: Initializes a new game object
    public game() {
        this.dealer = new dealer();    
        this.playerSet = new ArrayList<>(); 
        this.scanner = new Scanner(System.in);
        state = GameState.WAITING_FOR_PLAYERS; 
        playerSet.clear();   
        //start();
    }


    public static game getInstance() {
        if (instance == null) {
            instance = new game();
        }
        return instance;
    }

    // Adds a new player to the game
    public boolean addPlayer(String code) {
        player player = new player(500, code);
        
        for (player p : playerSet)
        {
            if (p.getId().equals(code))
            {
                return false;
            }
        } 
        playerSet.add(player);
        return true;  
    }

    public void removePlayer(String code)
    {
        for (player p: playerSet)
        {
            if (p.getId().equals(code))
            {
                playerSet.remove(p);
                return;
            }
        }
    }

    // Adds a dealer to the game
    public void addDealer() {
    	 // TODO: adds dealer to the game
    }

    // Starts the game by initializing the deck, shuffling, and distributing cards
    public void start() {
        for (player p: playerSet)
        {
            if (p.getState() != player.PlayerState.WAITING_START)
                return;
        }
        System.out.println("Game started");
        deck = new deck();           
        deck.shuffle(); 
        clearHands();
        dealer.init();               
        System.out.println(playerSet);
        state = GameState.PLAYER_TURN;
        for (player p : playerSet) {
            p.setState(player.PlayerState.PLAYING);
        }
        System.out.println("Players playing: ");
        printPlayers();
        dealer.setPlayerSet(playerSet);  
        dealer.distribute(deck);        
    }

    // Handles the turn-based gameplay logic for each player
    public void turn() {
        // Iterate over all players to let them decide to play or not
        for (player p : playerSet) {
            System.out.println("Player " + p.getId() + ". Do you want to play this round? (y/n)");
            char playRound = scanner.next().charAt(0);
            if (playRound == 'n') {
                p.setPlaying(false);  
            } else {
                p.setPlaying(true);   
            }

            // If player is playing, ask for bet
            if (p.isPlaying()) {
                int b = -1;
                while (b > p.getBalance() || b < 0) {
                    System.out.println("Balance: " + p.getBalance());
                    System.out.println("How much do you want to bet?");
                    b = scanner.nextInt();
                }
                p.setBet(b);  
            } 
        }

        // Handle the player's Gameplay actions (Hit or Stand)
        for (player p : playerSet) {
            if (!p.isPlaying()) continue;

            System.out.println("\n***** Player " + p.getId() + "'s turn *****");
            System.out.println("Player " + p.getId() + "'s hand:");
            int s = p.checkHand();  
            char c = ' ';
            while (Character.toLowerCase(c) != 's' && s < 21) {  // Player can hit or stand until they reach 21
                c = p.choice();  // Get the player's choice (hit or stand)
                if (Character.toLowerCase(c) == 'h') {
                    p.hit(deck);  // Player hits (draws a card)
                    s = p.checkHand();  
                } else if (Character.toLowerCase(c) == 's') {
                    p.stand();  // Player stands
                    s = p.checkHand();  // Check the hand after standing
                } else {
                    System.out.println("Invalid Input\n");
                }
            }

            try {
                Thread.sleep(2000);  
            } catch (InterruptedException e) {
                e.printStackTrace();  
            }
        }

        System.out.println("******Turns end!******");
        dealer.reveal();  
        dealer.checkHand();  
        
        while (dealer.getSum() < 17) {  // Dealer must hit until they reach a sum of at least 17
            try {
                Thread.sleep(1000);  
            } catch (InterruptedException e) {
                e.printStackTrace(); 
            }
            dealer.hit(deck);  // Dealer hits (draws a card)
            dealer.checkHand();  
        }
    }

    // Checks the result of the round for each player and displays the outcome
    public void checkWin() {
        state = GameState.GAME_OVER;
        int dealerHand = dealer.getSum();  

        // Iterate over all players and check who wins
        for (player p : playerSet) {
            int playerHand = p.getSum();  

            if (playerHand < 21 && dealerHand < playerHand) {
                p.setResponse("Player " + p.getId() + " wins");
                p.win();
            } else if (playerHand == 21 && dealerHand < playerHand) {
                p.setResponse("Player " + p.getId() + " wins. Blackjack!");
                p.blackJack();
            } else if (playerHand > 21) {
                p.setResponse("Player " + p.getId() + " loses. Bust!");
                p.lose();
            } else if (dealerHand > 21) {
                p.setResponse("Dealer bust");
                p.win();
            } else if (dealerHand <= 21 && dealerHand >= playerHand) {
                p.setResponse("Dealer Wins");
                p.lose();
            }
            p.setState(player.PlayerState.LOBBY);
        }
        state = GameState.WAITING_FOR_PLAYERS;
    }

    // Asks players if they want to play another round
    public boolean playAgain() {
        for (int i = 0; i < playerSet.size(); i++) {
            player p = playerSet.get(i);
            char choice = ' ';

            if (p.getBalance() <= 0) {  // Check if the player has no balance left
                System.out.println("Player " + p.getId() + " is out!");
                playerSet.remove(i);  // Remove player if they have no balance
                i--;
                continue;
            }

            // Ask if the player wants to play again
            System.out.println("Player " + p.getId() + ": Play Again? (y/n)");
            while (choice != 'y' && choice != 'n') {
                choice = scanner.next().charAt(0);
            }
            if (choice == 'n') {
                playerSet.remove(i);  // Remove player if they choose not to play again
                i--;
            }
        }
        dealer.setPlayerSet(playerSet);  
        return !playerSet.isEmpty();  
    }

    /* DEBUGGING ZONE */
    // Prints all players and their hands (for debugging purposes)
    public void printPlayers() {
        for (player p: playerSet)
        {
            System.out.println();
            System.out.print(String.format("[ID: %s, BALANCE: %d, STATE: %s]", p.getId(), p.getBalance(), p.getState()));

        }
    }

    public deck getDeck()
    {
        return deck;
    }

    public player getPlayer(String i)
    {
        for (player p : playerSet)
        {
            if (p.getId().equals(i))
                return p;
        }
        return null;
    }

    public dealer getDealer()
    {
        return dealer;
    }

    public void checkDealerTurn()
    {
        //boolean is_dealerTurn = false;
        for (player p: playerSet)
        {
            if (p.getState() != player.PlayerState.TURN_OVER)
            {
                return;
            }
        }
        state = GameState.DEALER_TURN;
        dealerTurn(); 
    }

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    public void dealerTurn() {
        executor.submit(() -> { // Run in a separate thread
            try {
                while (dealer.getSum() < 17) {
                
                    Thread.sleep(1000);  // Delay before taking action
                    dealer.hit(deck);    // Dealer hits (draws a card)
                    dealer.checkHand();  // Check the dealer's hand
                    // Notify or log the updated hand
                    System.out.println("Dealer hand updated: " + dealer.getHand());
                
                }
                System.out.println("Dealer stands with a total of " + dealer.getSum());
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();  // Reset interrupted status
                System.out.println("Dealer turn interrupted");
                // Exit loop if interrupted
            }
            checkWin();
            clearHands();

        });
    }
    

    private void clearHands()
    {
        for (player p: playerSet)
        {
            p.clearHand();
        }
    }

    public GameState getState()
    {
        return state;
    }

    public void setState(GameState new_state)
    {
        state = new_state;
    }

    public void checkGame()
    {
        switch (state) {
            case WAITING_FOR_PLAYERS:
                start();
                break;
            case PLAYER_TURN:
                checkDealerTurn();
                break;
            case DEALER_TURN:
                
                break;
            case GAME_OVER:
            
                break;
            default:
                break;
        }
    }
    public List<player> getPlayerSet()
    {
        return playerSet;
    }
}



import java.util.ArrayList;
import java.util.Scanner;

// Class to represent a Player in a card game
public class player {
    private String id;         
    private int balance;        
    private int bet;            
    private boolean isPlaying;
    private String gameOverMessage;   
    private ArrayList<card> hand; 
    private PlayerState state;

    public enum PlayerState 
    {
        LOBBY,
        WAITING_START,
        PLAYING,
        TURN_OVER
    }

    // Constructor to initialize a Player with a balance and index
    public player(int b, String code) {
        id = code;  
        balance = b;    
        bet = 0;        
        hand = new ArrayList<>(); 
        state = PlayerState.LOBBY;
    }

    // Returns the player's index
    public String getId() {
        return id;
    }

    // Player takes a card from the deck
    public card hit(deck d) {
        card c = d.drawCard();
        hand.add(c);
        return c;  
    }

    // Player stands (chooses to take no more cards)
    public void stand() {
        // Do nothing; the player does not take any more actions
    }

    // Set the player's bet and update their balance
    public void setBet(int b) {
        if (state == PlayerState.LOBBY)
        {
            bet = b;           
            balance -= b;       
        }
    }

    // Get the current bet amount
    public int getBet() {
        return bet;
    }

    // Get the player's current balance
    public int getBalance() {
        return balance;
    }

    // Ask the player to choose between hitting or standing
    public char choice() {
        char c = ' ';
        System.out.print("Will you hit (h) or will you stand (s)? ");
        
        Scanner scanner = new Scanner(System.in); 
        c = scanner.next().charAt(0);  
        return c;       
    }

    // Calculate and display the total value of the player's hand
    public int checkHand() {
        int sum = 0;   
        System.out.println("Hand Size: " + hand.size());  
        for (card c : hand) {  
            System.out.println(c.toString());  
            int i = c.getValue();  
            if (i == 1 && sum + 11 <= 21) i = 11;  
            sum += i; 
        }
        System.out.println("Current sum: " + sum);  // Display the total hand value
        return sum;  
    }

    // Calculate the total value of the player's hand without displaying
    public int getSum() {
        int sum = 0; 
        for (card c : hand) {  
            int i = c.getValue(); 
            if (i == 1 && sum + 11 <= 21) i = 11;  
            sum += i;  
        }
        return sum;  
    }

    // Player wins and receives double their bet
    public void win() {
        balance += 2 * bet; 
        bet = 0;  
    }

    // Player wins with a Blackjack (special case with an extra reward)
    public void blackJack() {
        if (hand.size() == 2) { 
            balance += 2 * bet + bet / 2;  
        } else {
            win();  
        }
        bet = 0;  
    }

    // Player loses and their bet is forfeited
    public void lose() {
        bet = 0;  
    }

    // Reset the player's hand for a new round
    public void playAgain() {
        hand.clear();  
    }

    // Check if the player is currently playing this round
    public boolean isPlaying() {
        return isPlaying;  
    }

    // Set whether the player is participating in this round
    public void setPlaying(boolean isP) {
        isPlaying = isP;  
    }

    public ArrayList<card> getHand()
    {
        return hand;
    }

    public void setResponse(String s)
    {
        gameOverMessage = s;
    }

    public String getResponse()
    {
        return gameOverMessage;
    }

    public void clearHand()
    {
        hand.clear();
    }

    public PlayerState getState()
    {
        return state;
    }

    public void setState(PlayerState playerState)
    {
        state = playerState;
    }
}

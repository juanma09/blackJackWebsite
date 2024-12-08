import java.util.ArrayList; 
import java.util.List;      
public class dealer {

    private List<player> playerSet;  // List of players managed by the dealer
    private List<card> hand;         // List of cards in the dealer's hand

    // Constructor to initialize the dealer's player set and hand
    public dealer() {
        this.playerSet = new ArrayList<>();  
        this.hand = new ArrayList<>();       
    }

    // Initialize the dealer by clearing the player set and dealer's hand
    public void init() {
        hand.clear();     
    }

    // Get the set of players managed by the dealer
    public List<player> getPlayerSet() {
        return playerSet;  
    }

    // Set the player set (list of players) in the dealer
    public void setPlayerSet(List<player> playerSet) {
        this.playerSet = playerSet; 
    }

    // Distribute cards to the dealer and all players
    public void distribute(deck deck) {
        this.hit(deck);        
        this.hit(deck);  

        // Loop through all players in the set and give each two cards
        for (player player : playerSet) {
            player.hit(deck); 
            player.hit(deck); 
        }
    }

    // Dealer draws a card from the deck and adds it to their hand
    public void hit(deck deck) {
        hand.add(deck.drawCard()); 
    }

    // Dealer draws a card, hides it (face down), and adds it to their hand
    public void hit(deck deck, boolean hide) {
        card card = deck.drawCard(); 
        hand.add(card);              
        if (hide && !hand.isEmpty()) {
            hand.get(0).setHidden(true); 
        }
    }

    // Check the sum of the dealer's hand while ignoring hidden cards
    public int checkHand() {
        int sum = 0;  
        System.out.println("Hand Size: " + hand.size());  

        // Iterate through each card in the hand
        for (card card : hand) {
            System.out.println(card.toString());  
            if (card.isHidden()) continue;  

            int value = card.getValue();  
            
            // If the card is an Ace (value of 1), adjust its value to 11 if it doesn't bust the hand
            if (value == 1 && sum + 11 <= 21) value = 11;
            sum += value; 
        }

        System.out.println("\nCurrent sum: " + sum);  // Display the current hand value
        return sum;  
    }

    // Reveal the hidden card(s) in the dealer's hand
    public void reveal() {
        for (card card : hand) {
            card.setHidden(false);  
        }
    }

    // Calculate and return the total sum of the dealer's hand (ignores hidden status)
    public int getSum() {
        int sum = 0;  

        // Iterate through each card in the hand and sum their values
        for (card card : hand) {
            int value = card.getValue();  
            
            if (value == 1 && sum + 11 <= 21) value = 11;
            sum += value;  // Add the card's value to the sum
        }

        return sum;  
    }

    public List<card> getHand()
    {
        return hand;
    }
}

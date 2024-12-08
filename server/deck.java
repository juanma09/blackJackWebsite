import java.util.ArrayList;  
import java.util.Collections; 
import java.util.List;       
import java.util.NoSuchElementException; 

public class deck {

    private List<card> cards;  // List to hold the deck of cards

    // Constructor: Initializes a deck of 52 cards (4 suits, 13 ranks)
    public deck() {
        cards = new ArrayList<>();  

        // Arrays for the suits and ranks of the cards
        String[] suits = {"hearts", "diamonds", "clubs", "spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king", "ace"};
        
        // Array for the values of the cards corresponding to the ranks
        int[] values = {2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 1}; // Face cards and Ace have special values

        // Nested loops to create each combination of rank and suit
        for (String suit : suits) {
            for (int i = 0; i < 13; i++) {  
                String rank = ranks[i];  
                int value = values[i];   
                card card = new card(rank, suit, value);  
                cards.add(card);  
            }
        }
    }

    // Shuffle the deck using the Collections.shuffle method
    public void shuffle() {
        Collections.shuffle(cards);  
    }

    // Draw a card from the deck (returns the drawn card)
    public card drawCard() {
        if (!cards.isEmpty()) { 
            return cards.remove(cards.size() - 1);  
        } else {
            throw new NoSuchElementException("No more cards in the deck");  
        }
    }

    // Check if the deck is empty (returns true if empty, false otherwise)
    public boolean isEmpty() {
        return cards.isEmpty(); 
    }
}

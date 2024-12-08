public class card {
    private String suit;  
    private String rank;  
    private String full;
    private int value;    
    private boolean hidden; 

    // Constructor: Initializes a card with a suit, rank, and value
    public card(String rank, String suit, int value) {
        this.suit = suit;   
        this.rank = rank;   
        this.value = value; 
        this.hidden = false; 
        this.full = rank + "-" + suit;
    }

    // Function to get the suit of the card
    public String getSuit() {
        return suit;
    }

    // Function to get the rank of the card
    public String getRank() {
        return rank;
    }

    // Function to convert the card to a string for display
    // Returns "Hidden" if the card is face down, otherwise "Rank of Suit"
    public String toString() {
        return rank + "-" + suit;
        /*if (hidden)
            return "hidden";  
        else
            return rank + "-" + suit;  */
    }

    // Function to set whether the card is hidden (face down)
    public void setHidden(boolean hidden) {
        this.hidden = hidden;  
    }

    // Function to check if the card is hidden (face down)
    public boolean isHidden() {
        return hidden; 
    }

    // Function to get the value of the card (e.g., 10 for face cards, 11 for Ace)
    public int getValue() {
        return value;  
    }
}


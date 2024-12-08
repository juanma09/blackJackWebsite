import java.util.Scanner;  

public class blackjack {


    public static void main(String[] args) {
        clear(); 
        int n = numOfPlayers(); 
        clear(); 

        // Main game loop
        while (true) {
            game game = new game(); 

            // Add players to the game
            for (int i = 0; i < 4; i++)
                game.addPlayer(); 

            // Game round loop
            while (true) {
                game.start();     
                game.turn();      
                game.checkWin();  

                // Check if players want to play again
                if (!game.playAgain()) {
                    System.out.println("All players are out!"); 
                    new Scanner(System.in).nextLine(); 
                    break; 
                }
            }

            // Ask if the players want to reset the game
            if (!choice("")) {
                clear();
                System.out.println("Thanks For Playing!");
                break; 
            }
        }
    }

    // Function to get the number of players, limiting the input between 1 and 5
    static int numOfPlayers() {
        Scanner sc = new Scanner(System.in);
        int x;
        System.out.println("How many players?");
        x = sc.nextInt();
        x = Math.min(x, 5); 
        x = Math.max(1, x); 
        return x;
    }

    // Function to ask the user a yes/no question and return a boolean
    static boolean choice(String message) {
        Scanner sc = new Scanner(System.in);
        char choice = ' ';
        // Repeat until valid input ('y' or 'n') is received
        while (choice != 'y' && choice != 'n') {
            clear();  // Clear the screen
            System.out.print("Do you want to play again? (y/n): ");
            choice = sc.next().charAt(0); 
        }
        return choice == 'y'; 
    }

    // Overloaded choice function, optionally clears the screen before asking
    static boolean choice(String message, boolean clr) {
        if (clr) clear(); 
        return choice(message); 
    }

    // Function to clear the console screen
    static void clear() {
        
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}



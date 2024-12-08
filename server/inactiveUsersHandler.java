import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class inactiveUsersHandler {
    private static inactiveUsersHandler instance;
    Map<String, Long> clientLastActive = new HashMap<>();

    public static inactiveUsersHandler getInstance() {
        if (instance == null) {
            instance = new inactiveUsersHandler();
        }
        return instance;
    }

    private inactiveUsersHandler()
    {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        
        // Schedule checkInactiveClients to run every 5 seconds
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkInactiveClients();
            } catch (Exception e) {
                System.err.println("Error in checkInactiveClients: " + e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void handleHeartbeat(String clientId) {
        clientLastActive.put(clientId, System.currentTimeMillis());
    }

    public void checkInactiveClients() {
        long currentTime = System.currentTimeMillis();
        long timeout = 10000; // 10 seconds

        clientLastActive.forEach((clientId, lastActive) -> {
            if (currentTime - lastActive > timeout) {
                System.out.println("Client " + clientId + " is unresponsive.");
                game.getInstance().removePlayer(clientId);
                clientLastActive.remove(clientId); // Optionally, remove the client
                game.getInstance().checkGame();
            }
        });

        if (game.getInstance().getPlayerSet().size() == 0)
        {
            game.getInstance().setState(game.GameState.WAITING_FOR_PLAYERS);
        }
    }
}

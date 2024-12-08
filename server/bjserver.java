import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import com.google.gson.Gson;

import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class bjserver {
    public static void main(String[] args) throws IOException {
        int port = 65432; // Define the port to run the HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Create a context for the /draw-card endpoint
        server.createContext("/draw-card", new R_DealCard());
        server.createContext("/start-game", new R_StartGame());
        server.createContext("/join-game", new R_JoinGame());
        server.createContext("/set-bet", new R_SetBet());
        server.createContext("/get-balance", new R_GetBalance());
        server.createContext("/get-bet", new R_GetBet());
        server.createContext("/hit", new R_Hit());
        server.createContext("/turn-over", new R_TurnOver());
        server.createContext("/player-hand", new R_PlayerDeck());
        server.createContext("/dealer-hand", new R_DealerHand());
        server.createContext("/game-state", new R_GameState());
        server.createContext("/heartbeat", new R_HeartBeat());

        server.setExecutor(null); // Use the default executor
        System.out.println("HTTP Server is running on port " + port);
        server.start();
    }
}


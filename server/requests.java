import com.sun.net.httpserver.HttpServer;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import com.google.gson.Gson;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashMap;

class R_DealCard implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*"); // Allow all origins
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS"); // Allow methods
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type"); // Allow headers
        

        if ("GET".equals(exchange.getRequestMethod())) {
            System.out.println("Called");
            System.out.println(game.getInstance().getDeck());
            card card = game.getInstance().getDeck().drawCard();
            System.out.println("Card: " + card.toString());
            String response = card.toString();

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            
        } else {
            // Respond with a 405 Method Not Allowed for non-GET requests
            exchange.sendResponseHeaders(405, -1);
        }
    }
}


class R_StartGame extends baseHandler implements HttpHandler{
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        add_cors(exchange);

        Gson gson = new Gson();

        if ("POST".equals(exchange.getRequestMethod())) {
            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
            Map<String, Object> data = gson.fromJson(reader, Map.class);

            // Extract data
            String playerId = data.get("playerId").toString();
            game.getInstance().getPlayer(playerId).setState(player.PlayerState.WAITING_START);
            game.getInstance().start();

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("playerId", playerId);
            responseMap.put("status", "success");
            
            // Serialize the Map directly to JSON
            String response = gson.toJson(responseMap);

            send_response(exchange, response);
        } else {
            // Respond with a 405 Method Not Allowed for non-GET requests
            exchange.sendResponseHeaders(405, -1);
        }
    }
}

/* SECTION BETS */
class R_GetBet  extends baseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        add_cors(exchange);
        
        Gson gson = new Gson();

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // 204 No Content for preflight
            return;
        }

        if ("GET".equals(exchange.getRequestMethod())) {
            String query = exchange.getRequestURI().getQuery();
            System.out.print(query);
            Map<String, String> queryParams = parseQueryParams(query);
            
            String code = queryParams.get("playerId").toString();

            player p = game.getInstance().getPlayer(code); 
            int bet = p.getBet();
            // Create a Map or a simple POJO for JSON serialization
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("playerId", p.getId());
            responseMap.put("amount", bet);
            
            // Serialize the Map directly to JSON
            String response = gson.toJson(responseMap);

            // Send the response
            send_response(exchange, response);
            
        } else {
            // Respond with a 405 Method Not Allowed for non-GET requests
            exchange.sendResponseHeaders(405, -1);
        }
    }

   
}

class R_SetBet extends baseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Add CORS headers for all responses
        add_cors(exchange);

        Gson gson = new Gson();

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Respond to preflight request
            exchange.sendResponseHeaders(204, -1); // No content for OPTIONS request
            return;
        }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Parse JSON request body
            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
            Map<String, Object> data = gson.fromJson(reader, Map.class);


            // Extract data
            String playerId = data.get("playerId").toString();
            int amount = Integer.parseInt(data.get("amount").toString());

            
            // Perform game logic
            player p = game.getInstance().getPlayer(playerId);
            p.setBet(amount);
            //System.out.println("Updated player bet: " + p.getBet());
            game.getInstance().printPlayers();
            
            // JSON response
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("status", "success");
            responseMap.put("playerId", playerId);
            responseMap.put("amount", amount);

            String jsonResponse = gson.toJson(responseMap);

            // Send JSON response
            send_response(exchange, jsonResponse);
        } else {
            // Handle unsupported methods
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}

class R_GetBalance extends baseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        add_cors(exchange);
        
        Gson gson = new Gson();

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // 204 No Content for preflight
            return;
        }

        if ("GET".equals(exchange.getRequestMethod())) {
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> queryParams = parseQueryParams(query);
            
            String code = queryParams.get("playerId").toString();
            System.out.println("GET-BALANCE-REQUEST");
            player p = game.getInstance().getPlayer(code); 
            int balance = p.getBalance();
            
            // Create a Map or a simple POJO for JSON serialization
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("playerId", p.getId());
            responseMap.put("amount", balance);
            
            // Serialize the Map directly to JSON
            String response = gson.toJson(responseMap);
            
            // Send the response
            send_response(exchange, response);
            
        } else {
            // Respond with a 405 Method Not Allowed for non-GET requests
            exchange.sendResponseHeaders(405, -1);
        }
    }
}

class R_Hit extends baseHandler implements HttpHandler{
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        add_cors(exchange);
        
        Gson gson = new Gson();

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Respond to preflight request
            exchange.sendResponseHeaders(204, -1); // No content for OPTIONS request
            return;
        }

        if ("POST".equals(exchange.getRequestMethod())) {
            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
            Map<String, Object> data = gson.fromJson(reader, Map.class);

            String code = data.get("playerId").toString();

            player p = game.getInstance().getPlayer(code);
            Map<String, Object> responseMap = new HashMap<>();
            
            if (p.checkHand() < 21)
            {
                card c = p.hit(game.getInstance().getDeck());
                responseMap.put("status", "success");
                responseMap.put("card", c.toString());
            }
            else
            {
                responseMap.put("status", "error");
            }
            
            String response = gson.toJson(responseMap);

            send_response(exchange, response);
        } else {
            // Respond with a 405 Method Not Allowed for non-GET requests
            exchange.sendResponseHeaders(405, -1);
        }
    }
}

class R_PlayerDeck extends baseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        add_cors(exchange);
        
        Gson gson = new Gson();

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // 204 No Content for preflight
            return;
        }

        if ("GET".equals(exchange.getRequestMethod())) {
            //System.out.println(game.getInstance().getPlayer(0));
            // Get dealer hand
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> queryParams = parseQueryParams(query);

            String code = queryParams.get("playerId").toString();

            player p = game.getInstance().getPlayer(code);
            if (p == null)
            {
                Map<String, String> responseMap = new HashMap();
                responseMap.put("status", "error");
                responseMap.put("message", "player does not exist");
                send_response(exchange, gson.toJson(responseMap));
            }
            ArrayList<card> hand = p.getHand();

            String response = gson.toJson(hand);

            //Send hand
            send_response(exchange, response);
            
        } else {
            // Respond with a 405 Method Not Allowed for non-GET requests
            exchange.sendResponseHeaders(405, -1);
        }
    }
}

class R_DealerHand extends baseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        add_cors(exchange);
        
        Gson gson = new Gson();

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // 204 No Content for preflight
            return;
        }

        if ("GET".equals(exchange.getRequestMethod())) {
            //System.out.println(game.getInstance().getPlayer(0));
            // Get dealer hand
            List<card> hand = game.getInstance().getDealer().getHand();

            String response = gson.toJson(hand);
            
            //Send
            send_response(exchange, response);
            
        } else {
            // Respond with a 405 Method Not Allowed for non-GET requests
            exchange.sendResponseHeaders(405, -1);
        }
    }
}

class R_TurnOver extends baseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        add_cors(exchange);

        Gson gson = new Gson();

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // 204 No Content for preflight
            return;
        }
        
        if ("POST".equals(exchange.getRequestMethod())) {
            //System.out.println(game.getInstance().getPlayer(0));
            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
            Map<String, Object> data = gson.fromJson(reader, Map.class);
            
            String code = data.get("playerId").toString();
            player p = game.getInstance().getPlayer(code); 
            
            Map<String, Object> responseMap = new HashMap<>();

            if (p.getState() == player.PlayerState.PLAYING)
            {
                p.setState(player.PlayerState.TURN_OVER);
                responseMap.put("status", "success");
            }
            else
            {
                responseMap.put("status", "error");
                responseMap.put("message", "You need to be playing to end your turn"); 
            }
            

            game.getInstance().checkDealerTurn();
            String response = gson.toJson(responseMap);
            //Send
            send_response(exchange, response);
            
        } else {
            // Respond with a 405 Method Not Allowed for non-GET requests
            exchange.sendResponseHeaders(405, -1);
        }
    }
}

class R_JoinGame extends baseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        add_cors(exchange);
        Gson gson = new Gson();
        
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // 204 No Content for preflight
            return;
        }
        
        if ("POST".equals(exchange.getRequestMethod())) {
            //System.out.println(game.getInstance().getPlayer(0));
            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
            Map<String, Object> data = gson.fromJson(reader, Map.class);
            
            String code = data.get("playerId").toString();
            
            Map<String, Object> responseMap = new HashMap<>();
            if (game.getInstance().getState() != game.GameState.WAITING_FOR_PLAYERS)
            {
                responseMap.put("status", "error");
                responseMap.put("message", "Game already in course");
                send_response(exchange, gson.toJson(responseMap));
                return;
            }
            if (game.getInstance().addPlayer(code))
            {
                responseMap.put("status", "success");
            }
            else
            {
                responseMap.put("status", "success");
                responseMap.put("message", "Player already in game");
            }
            game.getInstance().printPlayers();
            System.out.println(code);
            //Send
            String response = gson.toJson(responseMap);
            send_response(exchange, response);
            
        } else {
            // Respond with a 405 Method Not Allowed for non-GET requests
            exchange.sendResponseHeaders(405, -1);
        }
    }
}

class R_GameState extends baseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        add_cors(exchange);
        
        Gson gson = new Gson();

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // 204 No Content for preflight
            return;
        }

        if ("GET".equals(exchange.getRequestMethod())) {
            //System.out.println(game.getInstance().getPlayer(0));
            // Get dealer hand
            game.GameState gameState = game.getInstance().getState();
            
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("status", "success");
            responseMap.put("gameState", gameState);
            
            String response = gson.toJson(responseMap);
            
            //Send
            send_response(exchange, response);
            
        } else {
            // Respond with a 405 Method Not Allowed for non-GET requests
            exchange.sendResponseHeaders(405, -1);
        }
    }
}

class R_HeartBeat extends baseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        add_cors(exchange);
        System.out.println("POST");
    
        Gson gson = new Gson();

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // 204 No Content for preflight
            return;
        }
        
        if ("POST".equals(exchange.getRequestMethod())) {
            //System.out.println(game.getInstance().getPlayer(0));
            

            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
            Map<String, Object> data = gson.fromJson(reader, Map.class);

            String code = data.get("playerId").toString();
            
            System.out.println(code);
            Map<String, Object> responseMap = new HashMap<>();
            
            inactiveUsersHandler.getInstance().handleHeartbeat(code);
            player p = game.getInstance().getPlayer(code);
            if (p == null)
            {
                responseMap.put("status", "error");
                responseMap.put("message", "Player does not exist");
                String response = gson.toJson(responseMap);
                send_response(exchange, response);
                return;
            }

            responseMap.put("playerId", code);
            responseMap.put("status", "success");
            responseMap.put("dealerHand", game.getInstance().getDealer().getHand());
            responseMap.put("playerHand", p.getHand());
            responseMap.put("balance", p.getBalance());
            responseMap.put("bet", p.getBet());
            responseMap.put("gameState", game.getInstance().getState());
            responseMap.put("playerState", p.getState());
            responseMap.put("players", game.getInstance().getPlayerSet());
            //Send
            String response = gson.toJson(responseMap);
            send_response(exchange, response);
            
        } else {
            // Respond with a 405 Method Not Allowed for non-GET requests
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
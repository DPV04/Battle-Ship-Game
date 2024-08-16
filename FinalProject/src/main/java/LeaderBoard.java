import java.util.*;

public class LeaderBoard {
    private HashMap<String, Integer> players;

    public LeaderBoard() {
        players = new HashMap<>();
    }

    public void addOrUpdatePlayer(String name, int score) {

        players.put(name, score);

    }

    public void displayLeaderboard() {
        // Convert the HashMap entries to a List and sort it by score
        List<Map.Entry<String, Integer>> sortedPlayers = new ArrayList<>(players.entrySet());
        sortedPlayers.sort((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()));

        System.out.println("Leaderboard:");
        for (Map.Entry<String, Integer> player : sortedPlayers) {
            System.out.println(player.getKey() + ": " + player.getValue());
        }
    }
}
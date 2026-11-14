
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

// Team class
class Team {

    final String name;
    final int points;
    final int goalDiff;

    Team(String name, int points, int goalDiff) {
        this.name = name;
        this.points = points;
        this.goalDiff = goalDiff;
    }

    @Override
    public String toString() {
        return name + "(" + points + "," + goalDiff + ")";
    }
}

// Generic Leaderboard class
class GenericLeaderboard<T> {

    private final List<T> items = new ArrayList<>();
    private final Comparator<? super T> order;

    GenericLeaderboard(Comparator<? super T> order) {
        this.order = order;
    }

    void add(T item) {
        items.add(item);
    }

    void addAll(List<? extends T> newItems) {
        items.addAll(newItems);
    }

    // TODO 1 completed
    List<T> ranking() {
        List<T> result = new ArrayList<>(items);
        result.sort(order);
        return result;
    }

    int size() {
        return items.size();
    }
}

// College class for generic reuse
class College {

    final String name;
    final int score;

    College(String name, int score) {
        this.name = name;
        this.score = score;
    }

    @Override
    public String toString() {
        return name + "(" + score + ")";
    }
}

// Main class
public class Leaderboard {

    public static void main(String[] args) {

        System.out.println("GENERIC LEADERBOARD SYSTEM");
        System.out.println("==========================");

        // Points descending, goal difference descending,
        // team name ascending

        Comparator<Team> teamOrder =
            Comparator.comparingInt((Team t) -> t.points)
                .reversed()
                .thenComparing(
                    Comparator.comparingInt(
                        (Team t) -> t.goalDiff
                    ).reversed()
                )
                .thenComparing(t -> t.name);

        GenericLeaderboard<Team> board =
            new GenericLeaderboard<>(teamOrder);

        // TEST 1: Dataset with ties

        List<Team> teams = new ArrayList<>();

        teams.add(new Team("Falcons", 30, 15));
        teams.add(new Team("Tigers", 30, 20));
        teams.add(new Team("Eagles", 30, 20));
        teams.add(new Team("Lions", 25, 12));
        teams.add(new Team("Wolves", 25, 12));
        teams.add(new Team("Panthers", 20, 8));

        board.addAll(teams);

        System.out.println("\nTEST 1: MULTI-FIELD RANKING");
        System.out.println("Original teams: " + teams);

        List<Team> ranking = board.ranking();

        System.out.println("\nRanked teams:");

        int position = 1;

        for (Team team : ranking) {
            System.out.println(position++ + ". " + team);
        }

        System.out.println(
            "\nOriginal list unchanged: " + teams
        );

        // TEST 2: Faulty comparator

        Comparator<Team> faultyOrder =
            (a, b) -> Integer.compare(b.points, a.points);

        GenericLeaderboard<Team> faultyBoard =
            new GenericLeaderboard<>(faultyOrder);

        faultyBoard.addAll(teams);

        System.out.println("\nTEST 2: FAULTY COMPARATOR");

        for (Team team : faultyBoard.ranking()) {
            System.out.println(team);
        }

        System.out.println(
            "Problem: Goal difference and name tie-breakers are ignored."
        );

        // TEST 3: Safe integer comparison

        Comparator<Team> safeOrder =
            (a, b) -> Integer.compare(b.points, a.points);

        List<Team> extremeTeams = new ArrayList<>();

        extremeTeams.add(
            new Team("High", Integer.MAX_VALUE, 10)
        );

        extremeTeams.add(
            new Team("Low", Integer.MIN_VALUE, 5)
        );

        extremeTeams.add(
            new Team("Middle", 100, 8)
        );

        extremeTeams.sort(safeOrder);

        System.out.println("\nTEST 3: SAFE INTEGER COMPARISON");

        for (Team team : extremeTeams) {
            System.out.println(team);
        }

        // Collections.sort demonstration

        System.out.println("\nCOLLECTIONS.SORT DEMONSTRATION");

        List<Team> sortedTeams = new ArrayList<>(teams);

        Collections.sort(sortedTeams, teamOrder);

        for (Team team : sortedTeams) {
            System.out.println(team);
        }

        // Generic reuse with College

        System.out.println("\nGENERIC REUSE: COLLEGE RANKING");

        Comparator<College> collegeOrder =
            Comparator.comparingInt(
                (College c) -> c.score
            ).reversed();

        GenericLeaderboard<College> collegeBoard =
            new GenericLeaderboard<>(collegeOrder);

        collegeBoard.add(new College("College A", 95));
        collegeBoard.add(new College("College B", 88));
        collegeBoard.add(new College("College C", 92));

        for (College college : collegeBoard.ranking()) {
            System.out.println(college);
        }

        System.out.println("\nLeaderboard completed.");
    }
}


package battleship;

import battleship.players.BattleshipPlayer;

public class Game {

    boolean VERBOSE = false;

    private Scoreboard scoreboard;
    private BattleshipPlayer p1, p2;

    Game(Scoreboard s, BattleshipPlayer p1, BattleshipPlayer p2) {
        scoreboard = s;
        this.p1 = p1;
        this.p2 = p2;
    }

    private int numMoves = 0;

    public void play() {
if (VERBOSE)
System.out.println("Player 1 reset()");
        p1.reset();
if (VERBOSE)
System.out.println("Player 2 reset()");
        p2.reset();

if (VERBOSE)
System.out.println("Player 1 hideShips()");
        Board b1 = p1.hideShips();
if (VERBOSE)
System.out.println("Player 2 hideShips()");
        Board b2 = p2.hideShips();
//b2.verboseMode = true;

        while (!b1.isComplete() && !b2.isComplete()) {
if (VERBOSE)
System.out.println("Player 1 go()");
            p1.go(b2);
if (VERBOSE)
System.out.println("Player 2 go()");
            p2.go(b1);
            b1.firedAtThisRound = false;
            b2.firedAtThisRound = false;

            numMoves++;
            if (numMoves > 1000) {
                break;
            }
        }

        if (b1.isComplete()) {
            scoreboard.addToScore(1,1);
        }
        if (b2.isComplete()) {
            scoreboard.addToScore(0,1);
        }
    }

}

# Battleship Strategy
This graphical Battleship simulator, created by Stephen Davies as part of a college Battleship programming tournament in fall 2016, runs a
series of Battleship guessing games against two `BattleshipPlayer` objects. The simulator executes individual player turns by calling 
strategies outlined by each `BattleshipPlayer`. Using Stephen’s existing framework, I implemented the `BattleshipPlayer` 
interface as `ashtabna_BattleshipPlayer`, which contains tactics for hiding and sinking ships. During the actual tournament, my 
Battleship strategy advanced to the final round, ultimately earning second place. For the purpose of demonstrating this application, 
my strategy plays against itself.

## Game Rules

### Game Play
Each player retains a two-dimensional grid of coordinates, upon which they “hide” their ships from their opponent. During a turn, a 
player fires at a chosen location on their opponent’s board, in an attempt to damage a portion of their hidden ships. 

### Board
In this simulation, a board is represented as a 9 x 9 matrix of characters, where each square of a ship is depicted with a letter. 
Empty sea is represented by a space.

### Ships
Players position five ships in total, each of varying sizes.

|Ship|Letter|Length|
|---|---|---|
|Carrier|A|5|
|Battleship|B|4| 
|Submarine|S|3|
|Destroyer|D|3|
|Patrol Boat|P|2|

## Sample Board
```
AAAAA    
       B  
       B  
     P B  
     P B  
          
 DDD    S 
        S 
        S
```

## Win State
Both players continue to choose coordinates until someone successfully locates and sinks all of their opponent’s ships.

## Strategy
<p>To keep track of turns, I store all hits (‘A’, ‘B’, ‘S’, ‘D’ or ‘P’), misses (‘ ‘) and untried targets (‘*’) in a matrix of characters
. During a turn, I either pursue a known target or sweep the board in search of new targets. For the latter case, I begin by continually 
selecting a random location in the matrix that has not already been fired upon and determining whether the smallest hidden ship can
actually exist there. 

<p>If the turn resulted in a hit, then I store the coordinates and type of ship hit for future reference. If previous turns resulted in 
a hit, then I examine my list of known wreckage and choose a neighboring square to target, based on ship size, the coordinates of the 
original hit, and previously attempted search directions.

<p>When I prepare my board for a new game, I make sure to hide ships at least one square apart from each other. To accomplish this, I 
choose coordinates for a ship randomly, until I find a suitable location.

## Interface
To run the simulator, you must include a list of all players in a text file called `roster.txt.` The file must contain each players’ 
name, followed by the name that precedes their `BattleshipPlayer` class (i.e., `ashtabna` for `ashtabna_BattleshipPlayer`), separated 
by commas. The simulator also requires the number of Battleship games to play, as a command line argument. Optionally, you can also 
pass “verbose” and “autostart,” in order to output individual turns and automatically start the simulation, respectfully. To run the 
graphical simulator, simply press on the picture of the battleship board on the upper right corner of the application. At the end of 
the simulation, the program displays the total number of points accumulated by each player, and also outputs the ship positions for 
each player’s board.

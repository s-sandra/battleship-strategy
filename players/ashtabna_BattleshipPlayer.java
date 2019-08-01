
package battleship.players;

import battleship.Board;

import java.util.Random;
import java.util.ArrayList;

public class ashtabna_BattleshipPlayer implements BattleshipPlayer {
	
	/**
	 * Square - this class is used to store the coordinates and 
	 * ship type. 
	 */
	private class Square{
		int row;
		int col;
		char shipType;
		
		Square(int r, int c, char s){
			row = r;
			col = c;
			shipType = s;
		}
	}
	
	private int maxCols = Board.WIDTH;
	private int maxRows = Board.HEIGHT;
	
	private Board myBoard = null; 
	private char[][] hiddenShips; //my ship arrangements. 
	private char[][] currentBoard = new char[maxRows][maxCols]; //board marking my moves.
	private ArrayList<Square> shipTargets; //accidentally found ships.
	private ArrayList<Integer> missingShipsLengths; //the lengths of ships not yet found.
	
	//keeps track of how many times each ship was hit.
	private int aHits;
	private int pHits;
	private int dHits;
	private int sHits;
	private int bHits;
	
	private boolean sunk; //flag for a sunk ship. 
	private int hitRow; //current row of a discovered ship. 
	private int hitCol; //current col of a discovered ship.
	private int origHitRow; //row where a ship was first discovered. 
	private int origHitCol; //col where a ship was first discovered.
	private char origShip; //the ship type that we are looking for. 
	private int searchDirection; //the direction (up - 0, down - 2, left - 3, or right - 1) to search for ships
	
	/**
     * hideShips - This method is called once at the beginning of each game
     * when you need to hide your ships.
     *
     * You must return a valid Board object. See that class for details.
     * Note carefully: under *no* circumstances should you return the same
     * board twice in a row; i.e., two successive calls to your hideShips()
     * method must always return *different* answers!
     */
    
	public Board hideShips() {
    	reset(); 
    	
    	placeShip('P', 2);
    	placeShip('D', 3);
    	placeShip('S', 3);
    	placeShip('B', 4);
    	placeShip('A', 5);
 
    	try{
    		myBoard = new Board(hiddenShips);
    	}
    	catch(Exception e){
    		hideShips();
    	}
        return myBoard;
    }
	
	
	/**
     * placeShip - This method is called at the beginning of a new game, to find
     * a suitable location for a ship and hide it on my board. 
     */
    private void placeShip(char ship, int length){
    	int colIndex = 0;
    	int rowIndex = 0;
    	boolean cannotPlace = true;
    	int direction = -1;
    	Random rng = new Random();
    	
    	//loops until a valid ship placement is discovered.
    	while(cannotPlace){    
    		colIndex = rng.nextInt(maxCols);
			rowIndex = rng.nextInt(maxRows);
			
			//finds a random empty board location. 
			while(hiddenShips[rowIndex][colIndex] != ' '){
				colIndex = rng.nextInt(maxCols);
				rowIndex = rng.nextInt(maxRows);
			}
			
			direction = rng.nextInt(4);
			
			//if the direction is up.
			if(direction == 0){
		
				//determines if there is room for the ship above.
				if(canPlace(-1, 0, rowIndex, colIndex, length, ' ', hiddenShips) &&
						notTouching(-1, 0, rowIndex, colIndex, length - 1)){
					cannotPlace = false;
				}
			}
			
			//if the direction is down.
			if(direction == 1){
				
				//determines if there is room for the ship below.
				if(canPlace(1, 0, rowIndex, colIndex, length, ' ', hiddenShips) &&
						notTouching(1, 0, rowIndex, colIndex, length - 1)){
					cannotPlace = false;
				}
			}
			
			//if the direction is left.
			if(direction == 2){
				
				//determines if there is room for the ship to the left.
				if(canPlace(0, -1, rowIndex, colIndex, length, ' ', hiddenShips) &&
						notTouching(0, -1, rowIndex, colIndex, length - 1)){
					cannotPlace = false;
				}
			}
			
			//if the direction is right.
			if(direction == 3){
				
				//determines if there is room for the ship to the right.
				if(canPlace(0, 1, rowIndex, colIndex, length, ' ', hiddenShips) &&
						notTouching(0, 1, rowIndex, colIndex, length - 1)){
					cannotPlace = false;
				}
			}
    	}
    	

    	//if the chosen direction is up.
    	if(direction == 0){
    		placeShip(-1, 0, rowIndex, colIndex, length, ship);
    	}
    	
    	//if the chosen direction is down.
    	if(direction == 1){
    		placeShip(1, 0, rowIndex, colIndex, length, ship);
    	}
		
    	//if the chosen direction is left.
    	if(direction == 2){
    		placeShip(0, -1, rowIndex, colIndex, length, ship);
    	}
    	
    	//if the chosen direction is right.
    	if(direction == 3){
    		placeShip(0, 1, rowIndex, colIndex, length, ship);
    	}
    }
    
    /**
     * placeShip - This method is called once a suitable location for a ship is found. 
     * It places the ship onto the board according to the given direction (expressed as change in row
     * and change in cols) relative to a chosen board index.  
     */
    private void placeShip(int rowDelta, int colDelta, int r, int c, int length, char ship){
    	for(int i = 0; i < length; i++){
    		hiddenShips[r][c] = ship;
    		r += rowDelta;
    		c += colDelta;
    	}
    }
    
    /**
     * clear - this method is called to clear the player's boards
     * of firing marks or hidden ships. 
     */
    private void clear(char[][] board, char space){
    	for(int i = 0; i < board.length; i++){
    		for(int j = 0; j < board[0].length; j++){
    			board[i][j] = space;
    		}
    	}
    }
  
    /**
     * notTouching - this recursive method is called when a prospective ship 
     * placement is found. It determines if the given placement will 
     * not border other ships. 
     */
    private boolean notTouching(int rowDelta, int colDelta, int r, int c, int length){
    	int leftCol = c + (colDelta * length) - 1; // the square to the left of the ship
    	int rightCol = c + (colDelta * length) + 1; // the square to the right of the ship
    	int downRow = r + (rowDelta * length) + 1; // the square below the ship
    	int upRow = r + (rowDelta * length) - 1; // the square above the ship
    	
		// base case, no more ship surroundings to check
    	if(length == -1){
    		return true;
    	}
    	
    	//changes the coordinates of ship surroundings if they are not on the board. 
    	if(leftCol < 0){
    		leftCol = c;
    	}
    	if(rightCol >= maxCols){
    		rightCol = c;
    	}
    	if(upRow < 0){
    		upRow = r;
    	}
    	if(downRow >= maxRows){
    		downRow = r;
    	}
    	 //determines if every surrounding index is empty. 
    	if(hiddenShips[r + rowDelta * length][leftCol] != ' '){
    		return false;
    	}
    	if(hiddenShips[r + rowDelta * length][rightCol] != ' '){
    		return false;
    	}
    	if(hiddenShips[downRow][c + colDelta * length] != ' '){
    		return false;
    	}
    	if(hiddenShips[upRow][c + colDelta * length] != ' '){
    		return false;
    	}
    	
    	return notTouching(rowDelta, colDelta, r, c, length - 1);

    }
    
    /**
     * canPlace - This method is called to determine if there are no ships in the given direction 
     *(expressed as change in row and change in cols) relative to a chosen board index.  
     */
    private boolean canPlace(int rowDelta, int colDelta, int r, int c, int length, char openSea, char[][] board){
    	
		// if no more board left for ship
		if(colDelta == 1 && c + length >= maxCols){
    		return false;
    	}
    	else if(colDelta == -1 && c - length < 0){
    		return false;
    	}
    	else if(rowDelta == 1 && r + length >= maxRows){
    		return false;
    	}
    	else if(rowDelta == -1 && r - length < 0){
    		return false;
    	}
    	
    	//determines if there are no ships from the starting to ending index of the placed ship. 
    	for(int i = 0; i < length - 1; i++){
			if(board[r + (rowDelta * i)][c + (colDelta * i)] != openSea){
				return false;
			}
		}
		return true;
	}

    /**
     * canFireAt - this method is called once a ship has been 
     * found on the opponent's board. It takes in the direction 
     * (expressed as change in row and change in col) and determines if
     * it is possible to fire in that direction relative to the row and 
     * col of the ship found.
     **/
    private boolean canFireAt(int row, int col) {
    	//if the index at the given direction is off the 
    	//edge of the board. 
    	if(row < 0 || col < 0){ 
    		return false;
    	}
    	if(row >= maxRows || col >= maxCols){
    		return false;
    	}
    	
    	//if the index in the given direction has already been fired upon. 
    	if(currentBoard[row][col] != '*'){
			return false;
		}
    	return true;
    }
    
    /**
     * chooseNewSearchDirection - this method is called to determine the next 
     * firing direction after a hit, if the previously tried direction was 
	 * unsuccessful.  
     */
    private int chooseNewSearchDirection(int currentDirection){
    	
    	//returns to the index where the ship was first found. 
    	hitRow = origHitRow;
		hitCol = origHitCol;
		
    	switch(currentDirection){
			case 0: //up
				return 2; //down
			case 2: //down
				return 3; //left
			case 3: // left
				return 1; //right
			case 1: //right
				return 0; //up
    	}
    	return 0;
    }
    
    /**
     * contains - this method is called when we hit a ship we are
     * not currently searching for. It determines if its ship type 
     * is already in our ArrayList shipTargets, which stores the coordinates
     * of targets to use for later.
     */
    private boolean contains(char shipHit){
    	for(Square square: shipTargets){
    		if (shipHit == square.shipType){
    			return true;
    		}
    	}
    	return false;
    }
   
    /**
     * markMove - this method is called after firing at a square. It marks the 
     * contents of the square on the Array currentBoard and sets the values of 
     * instance variables.
     */
    private void markMove(int row, int col, char shipHit){
    	hitRow = row;
    	hitCol = col;
    	
    	//if the move resulted in a hit. 
    	if(shipHit != ' '){
    		currentBoard[row][col] = shipHit;
    		
    		//if we were looking for new ships.
    		if(sunk){
    			sunk = false;
    			origHitRow = row;
    			origHitCol = col;
    			origShip = shipHit;
    		}
    		
    		//if we are currently searching for a ship and the ship found is not
    		//the ship we're looking for.
    		if(shipHit != origShip){
    			
    			// if the ship is not already in our target list. 
    			if(!contains(shipHit)){
    				shipTargets.add(new Square(row,col,shipHit)); //adds the coordinates to use for later.
    			}
    			hitRow = origHitRow; //returns to the original index of the ship we want. 
    			hitCol = origHitCol;
    			chooseNewSearchDirection(searchDirection); //changes the direction. 
    		}
    	}
    	
    	//if the move did not result in a hit
    	else{
    		currentBoard[row][col] = 'O';
    		
    		//if we are currently looking for a ship
    		if(!sunk){
        		chooseNewSearchDirection(searchDirection); //changes the direction.
        	}
    	}
    	
    	addHit(shipHit); 
    }
    
    
    /**
     * getShortestLength - this method is called when searching for new places to fire.
     * It returns the length of the smallest ship that has not been sunk. 
     **/
    private int getShortestLength(){
    	int min = 6;
    	
    	for(Integer length: missingShipsLengths){
    		if(length <= min){
    			min = length;
    		}
    	}
    	
    	return min;
    }
    
    /**
     * shipCanExist - this method is called when searching for new places to fire. It determines
     * if the shortest missing ship can exist in the given index.
     */
    private boolean shipCanExist(int shipLength, int r, int c){
    	boolean canPlace = false;
    	
    	
    	//if there is open sea below given index. 
    	if(canPlace(-1, 0 , r, c, shipLength - 1, '*', currentBoard)){
    		canPlace = true;
    	}
    	
    	//if there is open sea above given index.
    	if(canPlace(1, 0, r, c, shipLength - 1, '*', currentBoard)){
    		canPlace = true;
    	}
    	
    	//if there is open sea right given index.
    	if(canPlace(0, 1, r, c, shipLength - 1, '*', currentBoard)){
    		canPlace = true;
    	}
    	
    	//if there is open sea left given index.
    	if(canPlace(0, -1, r, c, shipLength - 1, '*', currentBoard)){
    		canPlace = true;
    	}
    	
    	return canPlace;
    }
    
    /**
     * Searches for square not occupied by the wreckage of ship 
     * of given type in given direction. 
     * Returns square that represents found location. 
     */
    private Square skipWreckage(int direction, char shipType) {

    	int rowDelta = 0;
    	int colDelta = 0;
    	
		//changes delta values to obtain next coordinate in the search direction.
    	switch (direction) {
			case 0: //up
				rowDelta = -1; 
				colDelta = 0; 
				break;
			case 1: //right
				rowDelta =  0; 
				colDelta = 1; 
				break;
			case 2: //down
				rowDelta =  1; 
				colDelta = 0; 
				break;
			case 3: //left
				rowDelta =  0; 
				colDelta = -1; 
				break;
    	}

    	//stores the coordinates of the current location. 
    	Square result = new Square(hitRow, hitCol, shipType);
    	
    	do{
    		result.row += rowDelta; //modifies the coordinates to reflect the change in direction. 
    		result.col += colDelta;
    		
    		//checks to see if the new coordinates are on the board.
    		if (result.row < 0 || result.row >= maxRows 
        			|| result.col < 0 || result.col >= maxCols) {
    			return null;
    		}
    	} while(currentBoard[result.row][result.col] == shipType); //changes the coordinates to skip ship wreckage. 
    	
    	return result;

    }

    /**
     * removeSunkShips - this method is called when we detect that our 
     * ArrayList shipTargets has potential targets to try next. It makes sure
     * that the ArrayList does not contain invalid coordinates by removing
     * any ships that have already been sunk by random chance. 
     */
    private void removeSunkShips(){
    	ArrayList<Square> removedTargets = new ArrayList<Square>(); 
    	
    	for(Square target : shipTargets){

    		switch (target.shipType) {
        	case 'A': 
        		if(aHits == 5){
        			removedTargets.add(target);
        		}
        		break;
        		
        	case 'B': 
        		if(bHits == 4){
        			removedTargets.add(target);
        		}
        		break;
        		
        	case 'S': 
        		if(sHits == 3){
        			removedTargets.add(target);
        		}
        		break;
        		
        	case 'D':  
        		if(dHits == 3){
        			removedTargets.add(target);
        		}
        		break;
        		
    		case 'P': 
    			if(pHits == 2){
    				removedTargets.add(target);
        		}
	    		break;
    		}
    	}
    	
    	//if shipTargets contains ships that have already been sunk
    	if(removedTargets.size() > 0){
    		for(Square target : removedTargets){
    			shipTargets.remove(target);
    		}
    	}
    }
    
    /**
     * go - This method is called repeatedly throughout the game, every
     * time it's your turn.
     *
     * When it's your turn, and go() is called, you must call fireAt() on
     * the Board object which is passed as a parameter. You must do this
     * exactly *once*: trying to fire more than once during your turn will
     * be detected as cheating.
     */
    public void go(Board opponentsBoard) {
 
        int chosenRow;
        int chosenCol;
        Random rng = new Random();
        char shipHit = ' ';
        
        //if we have sunk a ship and other known targets are available. 
        if(shipTargets.size() > 0){
        	
        	removeSunkShips(); //removes obsolete coordinates from shipTargets
        	
        	//if shipTargets still contains valid coordinates
        	if(sunk && shipTargets.size() > 0){
				
				// sets coordinates and type of last known damaged ship for targeting
	    		hitRow = shipTargets.get(0).row;
	    		hitCol = shipTargets.get(0).col;
	    		origHitRow = hitRow;
	    		origHitCol = hitCol;
	    		origShip = shipTargets.get(0).shipType;
	    		shipTargets.remove(0);
	    		sunk = false;
        	}
    	}
        
        //if a ship has been sunk and there are no known targets.
        else if(sunk){
        	
        	//chooses a random row and col. 
        	chosenRow = rng.nextInt(maxRows);
        	chosenCol = rng.nextInt(maxCols);
        	int shortestShipLength = getShortestLength(); //finds the shortest missing ship. 
        	
        	//while the random location has already been fired upon (empty sea denoted by '*')
        	//or the shortest ship cannot exist at that location
        	while(currentBoard[chosenRow][chosenCol] != '*' ||
        			!shipCanExist(shortestShipLength, chosenRow, chosenCol)){
        		chosenRow = rng.nextInt(maxRows);
            	chosenCol = rng.nextInt(maxCols);
        	}
        	
        	shipHit = opponentsBoard.fireAt(chosenRow, chosenCol);
        	markMove(chosenRow, chosenCol, shipHit);
        	return; // exits method as soon as the opponent's board was fired at.
        }
        
        //Searching for a ship that we have not yet sunk. 
        while(!sunk){

        	//skips any wreckage to store the next target in the given direction. 
        	Square skippedCoord = skipWreckage(searchDirection, origShip);

        	//if the firing coordinates are on the board and have not already been fired at
        	if(skippedCoord != null && canFireAt(skippedCoord.row, skippedCoord.col)){
	        	shipHit = opponentsBoard.fireAt(skippedCoord.row, skippedCoord.col);
	        	markMove(skippedCoord.row, skippedCoord.col, shipHit);
	        	return;
        	} else {
        		//tries a different search direction.
	        	searchDirection = chooseNewSearchDirection(searchDirection); 
        	}
        }
    }
    
    /**
     * addHit - this method is called after marking a move on the Array currentBoard.
     * It determines if a given ship has been sunk.
     */
    private void addHit(char ship){
    	
    	switch(ship){
    	case 'A':
    		aHits++;
    		
    		if(aHits == 5){
    			sunk = true;
    			searchDirection = 0; //resets the search direction.
    			missingShipsLengths.remove(new Integer(5)); //removes the ship from our list of ships to sink. 
    		}
    		break;
    		
    	case 'B':
    		bHits++;
    		
    		if(bHits == 4){
    			sunk = true;
    			searchDirection = 0;
    			missingShipsLengths.remove(new Integer(4));
    		}
    		break;
    		
    	case 'S':
    		sHits++;
    		
    		if(sHits == 3){
    			sunk = true;
    			searchDirection = 0;
    			missingShipsLengths.remove(new Integer(3));
    		} 
    		break;
    		
    	case 'D':
    		dHits++;
    		
    		if(dHits == 3){
    			sunk = true;
    			searchDirection = 0;
    			missingShipsLengths.remove(new Integer(3));
    		}
    		break;
    		
    	case 'P':
    		pHits++;
    		
    		if(pHits == 2){
    			sunk = true;
    			searchDirection = 0;
    			missingShipsLengths.remove(new Integer(2));
    		}
    		break;
    	}
    }

    /**
     * reset - This method is called when a game has ended and a new game
     * is beginning. It gives you a chance to reset any instance variables
     * you may have created, so that your BattleshipPlayer starts fresh.
     */
    public void reset() {
    	aHits = 0;
    	pHits = 0;
    	dHits = 0;
    	sHits = 0;
    	bHits = 0;
    	searchDirection = 0;
    	
    	hiddenShips = new char[maxRows][maxCols];
    	shipTargets = new ArrayList<Square>();
    	missingShipsLengths = new ArrayList<Integer>();
    	
    	missingShipsLengths.add(5);
    	missingShipsLengths.add(4);
    	missingShipsLengths.add(3);
    	missingShipsLengths.add(3);
    	missingShipsLengths.add(2);
    	
    	clear(hiddenShips, ' ');
    	
    	clear(currentBoard, '*');
    	sunk = true;
    }
}

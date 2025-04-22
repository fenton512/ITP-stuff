import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        //start the game
        game.startGame();
        int numOfTurns = GameManager.s.nextInt();
        //produce all turns
        for (int i = 0; i < numOfTurns; i++) {
            game.nextTurn();
        }
        //end the game
        game.defineResults();
    }
}
//interface movement strategy with 4 possible movements
interface MovementStrategy {
    GameObject moveUp(Figure figure);
    GameObject moveDown(Figure figure);
    GameObject moveLeft(Figure figure);
    GameObject moveRight(Figure figure);
}
//class that define concrete strategy for normal style
class NormalMovement implements MovementStrategy {
    //expected X coordinate after moving
    int predictX;
    //expected Y coordinate after moving
    int predictY;
    //object that was lied in [predictX, predictY] coordinates
    GameObject object;

    //For normal style move up decrease X by 1
    @Override
    public GameObject moveUp(Figure figure) {
        predictX = figure.getX() - 1;
        predictY = figure.getY();
        try {
            //change location of GameObjects on the board
            return permutationsOnTheBoard(figure);
        }catch (InvalidMessage e) {
            //if movement is invalid throw exception
            throw new InvalidMessage();
        }

    }
    //For normal style move up increase X by 1
    @Override
    public GameObject moveDown(Figure figure) {
        predictX = figure.getX() + 1;
        predictY = figure.getY();
        try {
            return permutationsOnTheBoard(figure);
        }catch (InvalidMessage e) {
            throw new InvalidMessage();
        }
    }
    //For normal style move up decrease Y by 1
    @Override
    public GameObject moveLeft(Figure figure) {
        predictX = figure.getX();
        predictY = figure.getY() - 1;
        try {
            return permutationsOnTheBoard(figure);
        }catch (InvalidMessage e) {
            throw new InvalidMessage();
        }
    }
    //For normal style move up increase Y by 1
    @Override
    public GameObject moveRight(Figure figure) {
        predictX = figure.getX();
        predictY = figure.getY() + 1;
        try {
            return permutationsOnTheBoard(figure);
        }catch (InvalidMessage e) {
            throw new InvalidMessage();
        }
    }
    //provide permutations on the board
    private GameObject permutationsOnTheBoard(Figure figure) {
        //if movement is correct
        if (Checker.getInstance().canMove(predictX, predictY)) {
            //delete object on the old position
            Board.getBoard().setObject(figure.getX(), figure.getY(), null);
            //update coordinates for figure
            figure.setY(predictY);
            figure.setX(predictX);
            //take object that has lied on [predictX, predictY] coordinates
            object = Board.getBoard().getObject(predictX, predictY);
            //move figure to the new coordinates
            Board.getBoard().setObject(predictX, predictY, figure);
            return object;
        //if cant move throws exceptions
        } else throw new InvalidMessage();
    }
}
//class that define concrete strategy for attack style
class AttackingMovement implements MovementStrategy {
    //expected X coordinate after moving
    int predictX;
    //expected Y coordinate after moving
    int predictY;
    //object that was lied in [predictX, predictY] coordinates
    GameObject object;
    //For attack style move up decrease X by 2
    @Override
    public GameObject moveUp(Figure figure) {
         predictX = figure.getX() - 2;
         predictY = figure.getY();
        try {
            return permutationsOnTheBoard(figure);
        }catch (InvalidMessage e) {
            throw new InvalidMessage();
        }
    }
    //For attack style move up increase X by 2
    @Override
    public GameObject moveDown(Figure figure) {
        //define new coordinates
        predictX = figure.getX() + 2;
        predictY = figure.getY();
        try {
            //change location of objects on the board
            return permutationsOnTheBoard(figure);
        }catch (InvalidMessage e) {
            throw new InvalidMessage();
        }

    }
    //For attack style move up decrease Y by 2
    @Override
    //define new coordinates
    public GameObject moveLeft(Figure figure) {
        predictX = figure.getX();
        predictY = figure.getY() - 2;
        try {
            //change location of objects on the board
            return permutationsOnTheBoard(figure);
        }catch (InvalidMessage e) {
            throw new InvalidMessage();
        }
    }
    //For attack style move up increase Y by 2
    @Override
    public GameObject moveRight(Figure figure) {
        //define new coordinates
        predictX = figure.getX();
        predictY = figure.getY() + 2;
        try {
            //change location of objects on the board
            return permutationsOnTheBoard(figure);
        }catch (InvalidMessage e) {
            throw new InvalidMessage();
        }
    }
    private GameObject permutationsOnTheBoard(Figure figure) {
        //if movement is correct
        if (Checker.getInstance().canMove(predictX, predictY)) {
            //delete figure from old coordinates
            Board.getBoard().setObject(figure.getX(), figure.getY(), null);
            //set new coordinates
            figure.setY(predictY);
            figure.setX(predictX);
            //define game object that has lied on new coordinates
            object = Board.getBoard().getObject(predictX, predictY);
            //set figure on the new coordinates
            Board.getBoard().setObject(predictX, predictY, figure);
            return object;
        //if cant move throws exception
        } else throw new InvalidMessage();
    }
}
//singleton class checker for checks correctness of actions
class Checker {
    //field that contains instance of the class
    private static Checker instance;
    //field for board
    private Board board;
    //field for active figure
    private Figure player;
    //private constructor of the class
    private Checker() {this.board = Board.getBoard();}

    //if checker was not created then create it and return, otherwise just return instance
    public static Checker getInstance() {
        if (instance == null) instance = new Checker();
        return instance;
    }
    //check is figure still within the board after moving
    private boolean isWithinBoard(int predictX, int predictY) {
        int dimention = board.getDIMENTION();
        return dimention >= predictX && dimention >= predictY && predictX >= 1 && predictY >= 1;
    }
    //check is cell occupied by friendly figure
    private boolean isOccupiedByFriend(int predictX, int predictY) {
        GameObject object = board.getObject(predictX, predictY);
        //if object on new coordinates if figure with same color return true, otherwise return false
        if (object != null && object.getClass().getSimpleName().equals("Figure")) {
            return (((Figure)object).getColor().equals(player.getColor()));
        }
        return false;
    }
    //check is cell occupied by any game object
    private boolean isOccupied(int predictX, int predictY) {
        return board.getObject(predictX, predictY) != null;
    }
    //can move only if is still within board and new cell is free
    public boolean canMove(int predictX, int predictY) {
        return isWithinBoard(predictX, predictY) && !isOccupiedByFriend(predictX, predictY);
    }
    //can clone if new cell if free
    public boolean canClone(int predictX, int predictY) {
        return player!= null && !isOccupied(predictX, predictY) && !player.getIsClone() && player.getNumOfClones() < 1;
    }
    //setter for player
    public void setPlayer(Figure player) {
        this.player = player;
    }
}
//abstract class for Coin and Figure
abstract class GameObject {
    //each game object has coordinates
    protected int x;
    protected int y;
    //getters and setters for fields
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    //constructor
    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
//class figure
class Figure extends GameObject{
    private String color;
    //field represents moving style
    private MovementStrategy movementStrategy;
    private boolean isClone;
    private Figure parent;
    private int numOfClones;

    //method that change movement strategy
    private void setMovementStrategy(MovementStrategy s) {
        this.movementStrategy = s;
    }
    //constructor for figure
    public Figure(String color, int x, int y, boolean isClone, Figure parent) {
        super(x, y);
        this.color = color;
        this.movementStrategy = new NormalMovement();
        this.isClone = isClone;
        this.parent = parent;
    }
    //method for action STYLE that returns Message class for flexible printing output
    public Message changeStyle() {
        //changing movement strategy
        if (movementStrategy.getClass().getSimpleName().equals("AttackingMovement")) {
            setMovementStrategy(new NormalMovement());
            return new SuccessfulStyle(this.x, this.y, this.getName(), "NORMAL ");
        } else {
            setMovementStrategy(new AttackingMovement());
            return new SuccessfulStyle(this.x, this.y, this.getName(), "ATTACKING");
        }
    }
    //method to move up, that know nothing about movement strategy besides of existence moveUp method
    public Message goUp() {
        try {
            //if move without exception then return correct message
            GameObject object = movementStrategy.moveUp(this);
            //decide type of message
            return whatShouldPrint(object);
        //else throws exception
        } catch (InvalidMessage e) {
            throw new InvalidMessage();
        }

    }
    //method to move down, that know nothing about movement strategy besides of existence moveDown method
    public Message goDown() {
        try {
            //if move without exception then return correct message
            GameObject object = movementStrategy.moveDown(this);
            //decide type of message
            return whatShouldPrint(object);
        //else throws exception
        }catch (InvalidMessage e) {
            throw new InvalidMessage();
        }
    }
    //method to move left, that know nothing about movement strategy besides of existence moveLeft method
    public Message goLeft() {
        try {
            //if move without exception then return correct message
            GameObject object = movementStrategy.moveLeft(this);
            //decide type of message
            return whatShouldPrint(object);
        //else throws exception
        } catch (InvalidMessage e) {
            throw new InvalidMessage();
        }
    }
    //method to move right, that know nothing about movement strategy besides of existence moveRight method
    public Message goRight() {
        //if move without exception then return correct message
        try {
            GameObject object = movementStrategy.moveRight(this);
            //decide type of message
            return whatShouldPrint(object);
        //else throws exception
        } catch (InvalidMessage e) {
            throw new InvalidMessage();
        }
    }
    //getters of private fields
    public boolean getIsClone() { return this.isClone;}
    public String getColor() {
        return color;
    }
    public String getName() {
        if (isClone) return color + "CLONE";
        return color;
    }

    public int getNumOfClones() {
        return numOfClones;
    }

    //method for CLONE action
    public Message makeClone() {
        //if it can clone then create new figure that is clone
        if (Checker.getInstance().canClone(this.y, this.x)) {
            numOfClones++;
            Figure clone = new Figure(this.color, this.y, this.x, true, this);
            //add clone on the board
            Board.getBoard().setObject(clone.x, clone.y, clone);
            //return successful message
            return new SuccessfulClon(clone.x, clone.y, this.getName());
        //else throw exception
        } else throw new InvalidMessage();
    }

    //method for understand type of returning message
    private Message whatShouldPrint(GameObject object) {
        //if cell was free
        if (object == null) return new MoveToEmptyCell(this.x, this.y, this.getName());
        String className = object.getClass().getSimpleName();
        //if cell was occupied by enemy figure
        if (className.equals("Figure")) {
            //if it was clone increase numOfClones
            if (((Figure) object).isClone && ((Figure) object).parent.numOfClones == 1) ((Figure) object).parent.numOfClones--;
            //return message about killing
            return new MoveAndKill(this.x, this.y, this.getName(), ((Figure) object).getName());
        }
        //if coin then get value, increase team score and return massage about collection
        int value = ((Coin)object).getValue();
        if (color.equals("GREEN")) GameManager.increaseGreenScore(value);
        else GameManager.increaseRedScore(value);
        return new MoveAndCollect(this.x, this.y, this.getName(), value);
    }
}
//enumerate for simplify parsing actions from the console
enum Action {
    UP("UP"),
    DOWN("DOWN"),
    RIGHT("RIGHT"),
    LEFT("LEFT"),
    COPY("COPY"),
    STYLE("STYLE");

    private String title;
    Action(String title) {this.title = title;}
}

//singleton class board
class Board {
    //private field for instance
    private static Board board;
    //private constructor
    private Board(int dimenstion) {
        this.DIMENTION = dimenstion;
        //filling the board that is represented by hashmap
        objects = new HashMap<>();
        for (int i = 1; i <= dimenstion; i++) {
            objects.put(i, new HashMap<>());
        }
    }
    private final int DIMENTION;
    private Map<Integer, Map<Integer, GameObject>> objects;
    //direct references on all possible figures in the game
    private Figure green;
    private Figure red;
    private Figure greenClone;
    private Figure redClone;
    //get instance method for board class
    public static Board getBoard(int dimension) {
        if(board == null) {
            board = new Board(dimension);
        }
        return board;
    }
    //overloading get instance method
    public static Board getBoard() {
        if (board == null) {
            throw new RuntimeException();
        }
        return board;
    }
    //method for getting GameObject from the board
    public GameObject getObject(int x, int y) {
        return objects.get(x).get(y);
    }
    //method for setting GameObject from the board
    public void setObject(int x, int y, GameObject object) {
        //target is old object on [x, y] field
        var target = objects.get(x).put(y, object);
        //if one of the figures change its location, then update reference
        if (object != null && object.getClass().getSimpleName().equals("Figure")) {
            updatePlayers((Figure) object, (Figure) object);
        }
        //if one of the figures was killed, then update reference
        if (target != null && target.getClass().getSimpleName().equals("Figure")) {
            updatePlayers((Figure) target, null);
        }
    }
    //updating referenced of the figures depend on its color and "clonable"
    private void updatePlayers(Figure player, Figure newPlayer) {
        if ((player).getColor().equals("GREEN")) {
            if ((player).getIsClone()) {
                Board.getBoard().setGreenClone(newPlayer);
            }
            else Board.getBoard().setGreen(newPlayer);
        }else {
            if ((player).getIsClone()) Board.getBoard().setRedClone(newPlayer);
            else Board.getBoard().setRed(newPlayer);
        }
    }

    //getters and setters for private fields
    public int getDIMENTION() {
        return DIMENTION;
    }
    public Figure getGreenClone() {
        return greenClone;
    }
    public void setGreenClone(Figure greenClone) {
        this.greenClone = greenClone;
    }
    public Figure getRedClone() {
        return redClone;
    }
    public void setRedClone(Figure redClone) {
        this.redClone = redClone;
    }
    public Figure getGreen() {
        return green;
    }
    public Figure getRed() {
        return red;
    }
    public void setGreen(Figure green) {
        this.green = green;
    }
    public void setRed(Figure red) {
        this.red = red;
    }
}
//class of the coin in the game with its coordinates and value
class Coin extends GameObject{
    private int value;

    public Coin(int x, int y, int value) {
        super(x, y);
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
//interface for provide messages in the console
interface Message {
    void getMessage();
}
//abstract class for success message with coordinates and color
abstract class SucessMessage implements Message {
    protected int x;
    protected int y;
    protected String figure;

    public SucessMessage(int x, int y, String figure) {
        this.x = x;
        this.y = y;
        this.figure = figure;
    }
}
//abstract class for game end scenarios with final scores of the teams
abstract class Scenarios implements Message {
    protected int greenScore;
    protected int redScore;

    public Scenarios(int greenScore, int redScore) {
        this.greenScore = greenScore;
        this.redScore = redScore;
    }
}

//class for exception
class InvalidMessage extends RuntimeException{
    @Override
    public String getMessage() {
        return "INVALID ACTION";
    }
}

//class for successful cloning message
class SuccessfulClon extends SucessMessage implements Message{
    public SuccessfulClon(int x, int y, String figure) {
        super(x, y, figure);
    }
    @Override
    public void getMessage() {
        System.out.println(figure + " CLONED TO "+ x + " " + y);
    }
}
//class for successful changing style message
class SuccessfulStyle extends SucessMessage implements Message {
    private String newStyle;

    public SuccessfulStyle(int x, int y, String figure, String newStyle) {
        super(x, y, figure);
        this.newStyle = newStyle;
    }

    @Override
    public void getMessage() {
        System.out.println(figure + " CHANGED STYLE TO " + newStyle);
    }
}
//class for successful moving to empty cell message
class MoveToEmptyCell extends SucessMessage implements Message {
    public MoveToEmptyCell(int x, int y, String figure) {
        super(x, y, figure);
    }

    @Override
    public void getMessage() {
        System.out.println(figure + " MOVED TO " + x + " " + y);
    }
}
////class for successful killing message
class MoveAndKill extends SucessMessage implements Message {
    private String enemy;

    public MoveAndKill(int x, int y, String figure, String enemy) {
        super(x, y, figure);
        this.enemy = enemy;
    }

    @Override
    public void getMessage() {
        System.out.println(figure + " MOVED TO " + x + " " + y + " AND KILLED " + enemy);
    }
}
//class for successful collection message
class MoveAndCollect extends SucessMessage implements Message {
    private int value;

    public MoveAndCollect(int x, int y, String figure, int value) {
        super(x, y, figure);
        this.value = value;
    }

    @Override
    public void getMessage() {
        System.out.println(figure + " MOVED TO " + x + " " + y + " AND COLLECTED " + value);
    }
}
//class for tie scenario message
class TieMessage extends Scenarios {
    public TieMessage(int greenScore, int redScore) {
        super(greenScore, redScore);
    }

    @Override
    public void getMessage() {
        System.out.println("TIE. SCORE " + greenScore + " "  + redScore);
    }
}
//class for green wining scenario
class GreenWinMessage extends Scenarios {
    public GreenWinMessage(int greenScore, int redScore) {
        super(greenScore, redScore);
    }

    @Override
    public void getMessage() {
        System.out.println("GREEN TEAM WINS. SCORE " + greenScore + " " + redScore);
    }
}
//class for red wining scenario
class RedWinMessage extends Scenarios {
    public RedWinMessage(int greenScore, int redScore) {
        super(greenScore, redScore);
    }

    @Override
    public void getMessage() {
        System.out.println("RED TEAM WINS. SCORE "  + greenScore + " " + redScore);
    }
}

//additional facade for game
class GameManager {
    private static int greenTeamScore;
    private static int redTeamScore;
    public static Scanner s;
    private static int numOfPlayer;

    public GameManager() {
        this.s = new Scanner(System.in);
    }
    //choose the active figure
    public Figure definePlayer() {
        String player = s.next();
        if (player.equals("GREEN")) return Board.getBoard().getGreen();
        else if (player.equals("RED")) return Board.getBoard().getRed();
        else if (player.equals("GREENCLONE")) return Board.getBoard().getGreenClone();
        else return Board.getBoard().getRedClone();
    }
    public Action defineAction() {
        String action = s.next();
        return Action.valueOf(action);
    }

    public Board initializeBoard() {
        int dimension = s.nextInt();
        Board board = Board.getBoard(dimension);
        return board;
    }

    public Figure initializePlayer() {
        int x = s.nextInt();
        int y = s.nextInt();
        if (numOfPlayer == 0){
            numOfPlayer++;
            return new Figure("GREEN", x, y, false, null);
        }else {
            numOfPlayer++;
            return new Figure("RED", x, y, false, null);
        }
    }
    public void initializeCoins() {
        int numOfCoins = s.nextInt();
        int x;
        int y;
        int value;
        for (int i = 0; i < numOfCoins; i++) {
            x = s.nextInt();
            y = s.nextInt();
            value = s.nextInt();
            Coin coin = new Coin(x, y, value);
            Board.getBoard().setObject(x, y, coin);
        }
    }
    public static void increaseGreenScore(int value) {
        greenTeamScore += value;
    }
    public static void increaseRedScore(int value) {
        redTeamScore += value;
    }
    //deciding winner of the game
    public Message whoWin() {
        if (greenTeamScore == redTeamScore) return new TieMessage(greenTeamScore, redTeamScore);
        if (greenTeamScore > redTeamScore) return new GreenWinMessage(greenTeamScore, redTeamScore);
        return new RedWinMessage(greenTeamScore, redTeamScore);
    }

}
//Facade of the game
class Game {
    private Figure activeFigure;
    private GameManager gameManager;
    private Message message;
    private Board board;
    private Checker checker;


    //starting game with initialization all needed elements
    public void startGame() {
        gameManager = new GameManager();
        gameManager.initializeBoard();
        board = Board.getBoard();
        checker = Checker.getInstance();
        for (int i = 0; i < 2; i++) {
            var player = gameManager.initializePlayer();
            board.setObject(player.x, player.y, player);
        }
        gameManager.initializeCoins();
    }
    //decide the winner of the game
    public void defineResults() {
        message = gameManager.whoWin();
        message.getMessage();
    }
    //provide next turn of the game
    public void nextTurn() {
        try {
            activeFigure = gameManager.definePlayer();
            var action = gameManager.defineAction();
            //if active figure is not alive, throw exception
            if (activeFigure == null) throw new InvalidMessage();
            //set active player for the player
            checker.setPlayer(activeFigure);
            //depending on the type of movement, provide it
            switch (action) {
                case UP -> message = activeFigure.goUp();
                case DOWN -> message = activeFigure.goDown();
                case RIGHT -> message = activeFigure.goRight();
                case LEFT -> message = activeFigure.goLeft();
                case COPY -> message = activeFigure.makeClone();
                case STYLE -> message = activeFigure.changeStyle();
            }
            //print output to the console
            message.getMessage();
        //print invalid action message
        }catch (InvalidMessage e) {
            System.out.println(e.getMessage());
        }
    }

}



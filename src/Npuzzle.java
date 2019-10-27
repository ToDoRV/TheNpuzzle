import java.util.*;

public class Npuzzle {
    private static final Integer STARTING_PRICE = 0;
    private static final Integer COST_TO_TRAVEL = 1;
    private static final Integer FOUND_GOAL = -1;

    private Integer countOfTilesWithNumbers;
    private Integer dimension;
    private Integer indexOfZero;
    private Cell[] pairsOfTarget;

    private Puzzle startupPuzzle;

    public Npuzzle() {
        Scanner input = new Scanner(System.in);

        enterCountOfTiles(input);
        findDimension();
        enterIndexOfZero(input);
        fillPairsOfTarget();

        startupPuzzle = new Puzzle(input, dimension, pairsOfTarget);
    }

    private void enterCountOfTiles(Scanner input) {
        while (!checkAllowableValuesForCountOfTiles(countOfTilesWithNumbers = input.nextInt())) {
            System.out.println("Please try again!");
        }
    }

    private boolean checkAllowableValuesForCountOfTiles(Integer number) {
        Double sqrt = Math.sqrt(number + 1);
        return ((sqrt - Math.floor(sqrt)) == 0);
    }

    private void findDimension() {
        dimension = (int) Math.sqrt(countOfTilesWithNumbers + 1);
    }

    private void enterIndexOfZero(Scanner input) {
        indexOfZero = input.nextInt();
        if (indexOfZero < 0 || indexOfZero > countOfTilesWithNumbers) {
            indexOfZero = countOfTilesWithNumbers;
        }
    }

    private void fillPairsOfTarget() {
        pairsOfTarget = new Cell[countOfTilesWithNumbers + 1];

        Integer rowValue = 0;
        Integer columnValue = 0;

        for (int i = 0; i < indexOfZero; ++i) {
            pairsOfTarget[i + 1] = new Cell(rowValue, columnValue);
            rowValue = increaseRowValue(rowValue, columnValue, dimension);
            columnValue = increaseColumnValue(rowValue, columnValue, dimension);
        }

        pairsOfTarget[0] = new Cell(rowValue, columnValue);
        rowValue = increaseRowValue(rowValue, columnValue, dimension);
        columnValue = increaseColumnValue(rowValue, columnValue, dimension);

        for (int i = indexOfZero; i < countOfTilesWithNumbers; ++i) {
            pairsOfTarget[i + 1] = new Cell(rowValue, columnValue);
            rowValue = increaseRowValue(rowValue, columnValue, dimension);
            columnValue = increaseColumnValue(rowValue, columnValue, dimension);
        }

        /*
        for (int i = 0; i <= countOfTiles; ++i) {
            System.out.println(i + ": (" + pairsOfTarget[i].getRow() + ":" + pairsOfTarget[i].getColumn() + ")");
        }
         */
    }

    private Integer increaseRowValue(Integer row, Integer column, Integer dimension) {
        if (column.equals(dimension - 1)) {
            return ++row;
        }
        return row;
    }

    private Integer increaseColumnValue(Integer row, Integer column, Integer dimension) {
        ++column;
        if (column.equals(dimension)) {
            column = 0;
        }
        return column;
    }

    public void findPath() { //IDAstar
        if (countOfInversions() % 2 != 0) {
            System.out.println("It is not possible to solve that puzzle!");
            return;
        }

        LinkedList<Puzzle> puzzleList = new LinkedList<>();
        puzzleList.addFirst(startupPuzzle);
        Integer threshold = startupPuzzle.getHeuristic();
        Integer resultOfSearch;

        do {
            resultOfSearch = depthSearchGoal(startupPuzzle, STARTING_PRICE, threshold, puzzleList);
            threshold = resultOfSearch;
        } while (!resultOfSearch.equals(FOUND_GOAL));

        printResult(puzzleList);
    }

    private Integer countOfInversions() {
        Integer sizeOfPairsOfTarget = pairsOfTarget.length;
        Integer[] goalPuzzlePositionsOfNumbers = new Integer[sizeOfPairsOfTarget];
        for (int i = 0; i < sizeOfPairsOfTarget; ++i) {
            goalPuzzlePositionsOfNumbers[i] = pairsOfTarget[i].getRow() * dimension + pairsOfTarget[i].getColumn();
        }

        Integer[] startPuzzlePositionsOfNumbers = startupPuzzle.getPositionsOfNumbers();

        Integer count = 0;
        for (int i = 1; i < sizeOfPairsOfTarget; ++i) {
            for (int j = i + 1; j < sizeOfPairsOfTarget; ++j) {
                if (startPuzzlePositionsOfNumbers[i] < startPuzzlePositionsOfNumbers[j]
                        ^ goalPuzzlePositionsOfNumbers[i] < goalPuzzlePositionsOfNumbers[j]) {
                    ++count;
                }
            }
        }
        return count;
    }

    private Integer depthSearchGoal(Puzzle puzzle, Integer cost, Integer threshold, LinkedList<Puzzle> puzzleList) {
        if (puzzle.getHeuristic().equals(0)) {
            return FOUND_GOAL;
        }

        Integer valueOfCurrentNode = puzzle.getHeuristic() + cost;
        if (valueOfCurrentNode > threshold) {
            return valueOfCurrentNode;
        }

        Integer minValue = Integer.MAX_VALUE;
        ArrayList<Puzzle> successors = findSuccessors(puzzle);
        for (Puzzle currentPuzzle : successors) {
            puzzleList.addLast(currentPuzzle);

            Integer valueOfReachedNode =
                    depthSearchGoal(currentPuzzle, cost + COST_TO_TRAVEL, threshold, puzzleList);

            if (valueOfReachedNode.equals(FOUND_GOAL)) {
                return FOUND_GOAL;
            }

            puzzleList.removeLast();

            if (valueOfReachedNode < minValue) {
                minValue = valueOfReachedNode;
            }
        }
        return minValue;
    }

    private ArrayList<Puzzle> findSuccessors(Puzzle puzzle) {
        ArrayList<Direction> possibleDirections = puzzle.getPossibleDirections();
        ArrayList<Puzzle> successors = new ArrayList<>();

        for (Direction direction : possibleDirections) {
            Puzzle successor = new Puzzle(puzzle, direction, pairsOfTarget);
            successors.add(successor);
        }

        return successors;
    }

    private void printResult(LinkedList<Puzzle> puzzleList) {
        System.out.println(puzzleList.size() - 1);
        System.out.println();
        for (Iterator<Puzzle> i = puzzleList.iterator(); i.hasNext(); ) {
            Puzzle currentPuzzle = i.next();
            Direction direction = currentPuzzle.getDirectionFromParentNodeToCurrentNode();
            if (direction != null) {
                System.out.println(direction.getOppositeDirection());
                System.out.println();
            }
            /*
            currentPuzzle.printTable();
            System.out.println();
             */
        }
    }

    public static void main(String[] args) {
        Npuzzle npuzzle = new Npuzzle();
        npuzzle.findPath();
    }
}
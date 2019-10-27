import java.util.ArrayList;
import java.util.Scanner;

public class Puzzle {

    private Integer dimensionOfTable;
    private Integer[][] table;
    private Integer heuristic;
    private Direction directionFromParentNodeToCurrentNode;
    private Cell cellOfZero;

    public Puzzle(Scanner input, Integer dimensionOfTable, Cell[] pairsOfTarget) {
        this.dimensionOfTable = dimensionOfTable;
        this.table = new Integer[dimensionOfTable][dimensionOfTable];

        for (int i = 0; i < dimensionOfTable; ++i) {
            for (int j = 0; j < dimensionOfTable; ++j) {
                table[i][j] = input.nextInt();
                if (table[i][j].equals(0)) {
                    cellOfZero = new Cell(i, j);
                }
            }
        }

        heuristic = findHeuristic(pairsOfTarget);
        directionFromParentNodeToCurrentNode = null;
    }

    public Puzzle(Puzzle parentPuzzle, Direction direction, Cell[] pairsOfTarget) {
        this.dimensionOfTable = parentPuzzle.getDimensionOfTable();
        this.table = new Integer[dimensionOfTable][dimensionOfTable];

        copyParentPuzzle(parentPuzzle);

        Cell parentCellOfZero = parentPuzzle.getCellOfZero();
        Integer rowOfSwapValue = parentCellOfZero.getRow() + direction.getRow();
        Integer columnOfSwapValue = parentCellOfZero.getColumn() + direction.getColumn();
        table[parentCellOfZero.getRow()][parentCellOfZero.getColumn()] = table[rowOfSwapValue][columnOfSwapValue];
        table[rowOfSwapValue][columnOfSwapValue] = 0;

        cellOfZero = new Cell(rowOfSwapValue, columnOfSwapValue);

        directionFromParentNodeToCurrentNode = direction;
        heuristic = findHeuristic(pairsOfTarget);
    }

    private void copyParentPuzzle(Puzzle parentPuzzle) {
        Integer[][] parentTable = parentPuzzle.getTable();
        for (int i = 0; i < dimensionOfTable; ++i) {
            for (int j = 0; j < dimensionOfTable; ++j) {
                this.table[i][j] = parentTable[i][j];
            }
        }
    }

    private Integer getDimensionOfTable() {
        return dimensionOfTable;
    }

    private Integer[][] getTable() {
        return table;
    }

    public Integer getHeuristic() {
        return heuristic;
    }

    public Direction getDirectionFromParentNodeToCurrentNode() {
        return this.directionFromParentNodeToCurrentNode;
    }

    private Cell getCellOfZero() {
        return cellOfZero;
    }

    private Integer findHeuristic(Cell[] pairsOfTarget) {
        Integer sum = 0;
        for (int i = 0; i < dimensionOfTable; ++i) {
            for (int j = 0; j < dimensionOfTable; ++j) {
                sum += (Math.abs(pairsOfTarget[table[i][j]].getRow() - i) +
                        Math.abs(pairsOfTarget[table[i][j]].getColumn() - j));
            }
        }
        return sum;
    }

    public ArrayList<Direction> getPossibleDirections() {
        ArrayList<Direction> possibleDirections = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            Integer row = direction.getRow() + cellOfZero.getRow();
            Integer column = direction.getColumn() + cellOfZero.getColumn();
            if (isRealCell(row, column)) {
                possibleDirections.add(direction);
            }
        }

        if (directionFromParentNodeToCurrentNode != null) {
            possibleDirections.remove(directionFromParentNodeToCurrentNode.getOppositeDirection());
        }

        return possibleDirections;
    }

    private boolean isRealCell(Integer row, Integer column) {
        return row >= 0 && row < dimensionOfTable
                && column >= 0 && column < dimensionOfTable;
    }

    public Integer[] getPositionsOfNumbers() {
        Integer[] positionsOfNumbers = new Integer[dimensionOfTable * dimensionOfTable];
        for (int i = 0; i < dimensionOfTable; ++i) {
            for (int j = 0; j < dimensionOfTable; ++j) {
                positionsOfNumbers[table[i][j]] = i * dimensionOfTable + j;
            }
        }
        return positionsOfNumbers;
    }

    public void printTable() {
        for (int i = 0; i < dimensionOfTable; ++i) {
            for (int j = 0; j < dimensionOfTable; ++j) {
                System.out.print(table[i][j] + " ");
            }
            System.out.println();
        }
    }
}

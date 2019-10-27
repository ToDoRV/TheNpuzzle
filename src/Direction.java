public enum Direction {
    up(-1, 0),
    right(0, 1),
    down(1, 0),
    left(0, -1);

    private final int row;
    private final int column;

    Direction(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Direction getOppositeDirection() {
        switch (this) {
            case up:
                return down;
            case right:
                return left;
            case down:
                return up;
            case left:
                return right;
            default:
                return null;
        }
    }
}

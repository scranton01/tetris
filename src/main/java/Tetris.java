import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

public class Tetris extends JPanel {
    List<List<Block>> board;
    boolean isActive;
    static final int BOARD_WIDTH = 10;
    static final int BOARD_HEIGHT = 24;
    static final int BOARD_VALID_HEIGHT = 20;
    Random random;
    public Tetris() {
        this.board = new ArrayList<>();
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            List<Block> row = new ArrayList<>();
            for (int j = 0; j < BOARD_WIDTH; j++) {
                row.add(new Block(BlockType.BLANK));
            }
            this.board.add(row);
        }
        this.setPreferredSize(new Dimension(300, 600));
        this.random = new Random();
    }


    //tetris is 10x20
    //return row number of returned
    List<Integer> eliminateFilledRow() {
        List<Integer> eliminatedRows = new ArrayList<>();
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            boolean isFilled = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board.get(i).get(j).type == BlockType.BLANK) {
                    isFilled = false;
                    break;
                }
            }
            if (isFilled) {
                for (Block block : board.get(i)) {
                    block.type = BlockType.BLANK;
                }
                eliminatedRows.add(i);
            }
        }
        repaint();
        return eliminatedRows;
    }

    //Parameter: row num of eliminated
    void dropBlocksAbove(int row) {
        while (row + 1 < BOARD_HEIGHT) {
            List<Block> curRow = board.get(row);
            board.set(row, board.get(row + 1));
            row++;
        }
        repaint();
    }

    // move left or right
    int move(MoveAction moveAction) {
        Set<Integer> rows = new HashSet<>();
        Set<Integer> columns = new HashSet<>();
        List<List<Integer>> originalCoordinate = getActiveCoordinate(rows, columns);
        List<List<Integer>> nextCoordinate = new ArrayList<>();
        for (List<Integer> coordinate : originalCoordinate) {
            int row = coordinate.get(0);
            int col = coordinate.get(1);
            List<Integer> newCoord = new ArrayList<>();
            newCoord.add(row);
            if (moveAction.equals(MoveAction.LEFT)) {
                newCoord.add(col - 1);
            } else if (moveAction.equals(MoveAction.RIGHT)) {
                newCoord.add(col + 1);
            }
            nextCoordinate.add(newCoord);
        }
        if (!isNextMoveValid(nextCoordinate)) {
            return -1;
        }
        int returnCode = updateBoard(originalCoordinate, nextCoordinate);
        repaint();
        return returnCode;
    }


    //drop active block by one row
    int drop() {
        Set<Integer> rows = new HashSet<>();
        Set<Integer> columns = new HashSet<>();
        List<List<Integer>> originalCoordinate = getActiveCoordinate(rows, columns);
        if (originalCoordinate.isEmpty()) {
            return 1;
        }
        List<List<Integer>> nextCoordinate = new ArrayList<>();
        for (List<Integer> coord : originalCoordinate) {
            int row = coord.get(0);
            int col = coord.get(1);
            List<Integer> newCoord = new ArrayList<>();
            newCoord.add(row - 1);
            newCoord.add(col);
            nextCoordinate.add(newCoord);
        }
        if (!isNextMoveValid(nextCoordinate)) {
            for (List<Integer> coord : originalCoordinate) {
                int row = coord.get(0);
                int col = coord.get(1);
                this.board.get(row).get(col).isActive = false;
            }
            this.isActive = false;
            return 1;
        }
        int returnCode = updateBoard(originalCoordinate, nextCoordinate);
        List<Integer> deletedRow = this.eliminateFilledRow();
        if (!deletedRow.isEmpty()) {
            for (int row : deletedRow) {
                this.dropBlocksAbove(row);
            }
        }
        repaint();
        return returnCode;
    }


    //return -1 if unable to rotate
    //todo consider about nextCoordinate is out of bound case
    int rotate(RotateAction action) {
        // find all active block
        Set<Integer> rows = new HashSet<>();
        Set<Integer> columns = new HashSet<>();
        List<List<Integer>> originalCoordinate = getActiveCoordinate(rows, columns);
        int rowSum = 0;
        for (Integer i : rows) {
            rowSum += i;
        }
        int rowCenter = rowSum / rows.size();
        int colSum = 0;
        for (Integer i : columns) {
            colSum += i;
        }
        int colCenter = colSum / columns.size();
        List<List<Integer>> nextCoordinate = new ArrayList<>();

        //if active block I
        if (board.get(originalCoordinate.get(0).get(0)).get(originalCoordinate.get(0).get(1)).type == BlockType.I) {
            int pivotRow = originalCoordinate.get(1).get(0);
            int pivotCol = originalCoordinate.get(1).get(1);
            //original is vertical
            if (board.get(pivotRow + 1).get(pivotCol).isActive) {
                //if rotated block out of bound
                if (pivotCol - 1 < 0) {
                    List<Integer> list = new ArrayList<>();
                    list.add(pivotRow);
                    list.add(pivotCol);
                    nextCoordinate.add(list);

                    List<Integer> list1 = new ArrayList<>();
                    list1.add(pivotRow);
                    list1.add(pivotCol + 1);
                    nextCoordinate.add(list1);

                    List<Integer> list2 = new ArrayList<>();
                    list2.add(pivotRow);
                    list2.add(pivotCol + 2);
                    nextCoordinate.add(list2);

                    List<Integer> list4 = new ArrayList<>();
                    list4.add(pivotRow);
                    list4.add(pivotCol + 3);
                    nextCoordinate.add(list4);
                } else if (pivotCol + 2 >= BOARD_WIDTH) {
                    List<Integer> list = new ArrayList<>();
                    list.add(pivotRow);
                    list.add(pivotCol - 3);
                    nextCoordinate.add(list);

                    List<Integer> list1 = new ArrayList<>();
                    list1.add(pivotRow);
                    list1.add(pivotCol - 2);
                    nextCoordinate.add(list1);

                    List<Integer> list2 = new ArrayList<>();
                    list2.add(pivotRow);
                    list2.add(pivotCol - 1);
                    nextCoordinate.add(list2);

                    List<Integer> list4 = new ArrayList<>();
                    list4.add(pivotRow);
                    list4.add(pivotCol);
                    nextCoordinate.add(list4);
                } else {
                    List<Integer> list = new ArrayList<>();
                    list.add(pivotRow);
                    list.add(pivotCol - 1);
                    nextCoordinate.add(list);

                    List<Integer> list1 = new ArrayList<>();
                    list1.add(pivotRow);
                    list1.add(pivotCol);
                    nextCoordinate.add(list1);

                    List<Integer> list2 = new ArrayList<>();
                    list2.add(pivotRow);
                    list2.add(pivotCol + 1);
                    nextCoordinate.add(list2);

                    List<Integer> list4 = new ArrayList<>();
                    list4.add(pivotRow);
                    list4.add(pivotCol + 2);
                    nextCoordinate.add(list4);
                }
                //if original is horizontal
            } else {
                List<Integer> list = new ArrayList<>();
                list.add(pivotRow + 2);
                list.add(pivotCol);
                nextCoordinate.add(list);

                List<Integer> list1 = new ArrayList<>();
                list1.add(pivotRow);
                list1.add(pivotCol);
                nextCoordinate.add(list1);

                List<Integer> list2 = new ArrayList<>();
                list2.add(pivotRow + 1);
                list2.add(pivotCol);
                nextCoordinate.add(list2);

                List<Integer> list4 = new ArrayList<>();
                list4.add(pivotRow - 1);
                list4.add(pivotCol);
                nextCoordinate.add(list4);
            }
            //rotate other blocks
        } else {
            int offset = 0;
            if (colCenter == 0) {
                offset = 1;
            }
            if (colCenter == BOARD_WIDTH - 1) {
                offset = -1;
            }
            if (board.get(rowCenter).get(colCenter).isActive) {
                List<Integer> coordinate = new ArrayList<>();
                coordinate.add(rowCenter);
                coordinate.add(colCenter);
                nextCoordinate.add(coordinate);
            }
            if (colCenter - 1 >= 0 && board.get(rowCenter + 1).get(colCenter - 1).isActive) {
                if (action == RotateAction.RIGHT) {
                    List<Integer> coordinate = new ArrayList<>();
                    coordinate.add(rowCenter + 1);
                    coordinate.add(colCenter + 1);
                    nextCoordinate.add(coordinate);
                } else {
                    List<Integer> coordinate = new ArrayList<>();
                    coordinate.add(rowCenter - 1);
                    coordinate.add(colCenter - 1);
                    nextCoordinate.add(coordinate);
                }
            }
            if (board.get(rowCenter + 1).get(colCenter).isActive) {
                if (action == RotateAction.RIGHT) {
                    List<Integer> coordinate = new ArrayList<>();
                    coordinate.add(rowCenter);
                    coordinate.add(colCenter + 1);
                    nextCoordinate.add(coordinate);
                } else {
                    List<Integer> coordinate = new ArrayList<>();
                    coordinate.add(rowCenter);
                    coordinate.add(colCenter - 1);
                    nextCoordinate.add(coordinate);
                }
            }
            if (colCenter + 1 < BOARD_WIDTH && board.get(rowCenter + 1).get(colCenter + 1).isActive) {
                if (action == RotateAction.RIGHT) {
                    List<Integer> coordinate = new ArrayList<>();
                    coordinate.add(rowCenter - 1);
                    coordinate.add(colCenter + 1);
                    nextCoordinate.add(coordinate);
                } else {
                    List<Integer> coordinate = new ArrayList<>();
                    coordinate.add(rowCenter + 1);
                    coordinate.add(colCenter - 1);
                    nextCoordinate.add(coordinate);
                }
            }
            if (colCenter + 1 < BOARD_WIDTH && board.get(rowCenter).get(colCenter + 1).isActive) {
                if (action == RotateAction.RIGHT) {
                    List<Integer> coordinate = new ArrayList<>();
                    coordinate.add(rowCenter - 1);
                    coordinate.add(colCenter);
                    nextCoordinate.add(coordinate);
                } else {
                    List<Integer> coordinate = new ArrayList<>();
                    coordinate.add(rowCenter + 1);
                    coordinate.add(colCenter);
                    nextCoordinate.add(coordinate);
                }
            }
            if (colCenter + 1 < BOARD_WIDTH && board.get(rowCenter - 1).get(colCenter + 1).isActive) {
                if (action == RotateAction.RIGHT) {
                    List<Integer> coordinate = new ArrayList<>();
                    coordinate.add(rowCenter - 1);
                    coordinate.add(colCenter - 1);
                    nextCoordinate.add(coordinate);
                } else {
                    List<Integer> coordinate = new ArrayList<>();
                    coordinate.add(rowCenter + 1);
                    coordinate.add(colCenter + 1);
                    nextCoordinate.add(coordinate);
                }
            }
            if (board.get(rowCenter - 1).get(colCenter).isActive) {
                if (action == RotateAction.RIGHT) {
                    List<Integer> coordinate = new ArrayList<>();
                    coordinate.add(rowCenter);
                    coordinate.add(colCenter - 1);
                    nextCoordinate.add(coordinate);
                } else {
                    List<Integer> coordinate = new ArrayList<>();
                    coordinate.add(rowCenter);
                    coordinate.add(colCenter + 1);
                    nextCoordinate.add(coordinate);
                }
            }
            if (colCenter - 1 >= 0 && board.get(rowCenter - 1).get(colCenter - 1).isActive) {
                if (action == RotateAction.RIGHT) {
                    List<Integer> coordinate = new ArrayList<>();
                    coordinate.add(rowCenter + 1);
                    coordinate.add(colCenter - 1);
                    nextCoordinate.add(coordinate);
                } else {
                    List<Integer> coordinate = new ArrayList<>();
                    coordinate.add(rowCenter - 1);
                    coordinate.add(colCenter + 1);
                    nextCoordinate.add(coordinate);
                }
            }
            if (colCenter - 1 >= 0 && board.get(rowCenter).get(colCenter - 1).isActive) {
                if (action == RotateAction.RIGHT) {
                    List<Integer> coordinate = new ArrayList<>();
                    coordinate.add(rowCenter + 1);
                    coordinate.add(colCenter);
                    nextCoordinate.add(coordinate);
                } else {
                    List<Integer> coordinate = new ArrayList<>();
                    coordinate.add(rowCenter - 1);
                    coordinate.add(colCenter);
                    nextCoordinate.add(coordinate);
                }
            }
        }

        if (!isNextMoveValid(nextCoordinate)) {
            return -1;
        }

        int returnCode = updateBoard(originalCoordinate, nextCoordinate);
        repaint();
        return returnCode;
    }

    private int updateBoard(List<List<Integer>> originalCoordinate, List<List<Integer>> nextCoordinate) {
        //get active block type
        int originalRow = originalCoordinate.get(0).get(0);
        int originalCol = originalCoordinate.get(0).get(1);
        BlockType originalType = board.get(originalRow).get(originalCol).type;

        //remove original
        for (int i = 0; i < originalCoordinate.size(); i++) {
            int row = originalCoordinate.get(i).get(0);
            int col = originalCoordinate.get(i).get(1);
            board.get(row).get(col).isActive = false;
            board.get(row).get(col).type = BlockType.BLANK;
        }

        //update next coordinate
        for (int i = 0; i < nextCoordinate.size(); i++) {
            int row = nextCoordinate.get(i).get(0);
            int col = nextCoordinate.get(i).get(1);
            board.get(row).get(col).isActive = true;
            board.get(row).get(col).type = originalType;
        }
        return 0;
    }

    private List<List<Integer>> getActiveCoordinate(Set<Integer> rows, Set<Integer> columns) {
        List<List<Integer>> originalCoordinate = new ArrayList<>();
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board.get(i).get(j).isActive) {
                    rows.add(i);
                    columns.add(j);
                    List<Integer> coordinate = new ArrayList<>();
                    coordinate.add(i);
                    coordinate.add(j);
                    originalCoordinate.add(coordinate);
                }
            }
        }
        return originalCoordinate;
    }


    private boolean isNextMoveValid(List<List<Integer>> nextCoordinate) {
        for (int i = 0; i < nextCoordinate.size(); i++) {
            int row = nextCoordinate.get(i).get(0);
            int col = nextCoordinate.get(i).get(1);
            if (row < 0 || col < 0 || col >= BOARD_WIDTH) {
                return false;
            }
            if (!board.get(row).get(col).isActive && board.get(row).get(col).type != BlockType.BLANK) {
                return false;
            }
        }
        return true;
    }

    void generateNewBlock() {
        int num = random.nextInt(1, 8);
        BlockType type;
        switch (num) {
            case 1:
                type = BlockType.T;
                this.board.get(21).get(4).isActive = true;
                this.board.get(21).get(4).type = type;
                this.board.get(20).get(3).isActive = true;
                this.board.get(20).get(3).type = type;
                this.board.get(20).get(4).isActive = true;
                this.board.get(20).get(4).type = type;
                this.board.get(20).get(5).isActive = true;
                this.board.get(20).get(5).type = type;
                break;
            case 2:
                type = BlockType.L;
                this.board.get(22).get(4).isActive = true;
                this.board.get(22).get(4).type = type;
                this.board.get(21).get(4).isActive = true;
                this.board.get(21).get(4).type = type;
                this.board.get(20).get(4).isActive = true;
                this.board.get(20).get(4).type = type;
                this.board.get(20).get(5).isActive = true;
                this.board.get(20).get(5).type = type;
                break;
            case 3:
                type = BlockType.J;
                this.board.get(22).get(5).isActive = true;
                this.board.get(22).get(5).type = type;
                this.board.get(21).get(5).isActive = true;
                this.board.get(21).get(5).type = type;
                this.board.get(20).get(5).isActive = true;
                this.board.get(20).get(5).type = type;
                this.board.get(20).get(4).isActive = true;
                this.board.get(20).get(4).type = type;
                break;
            case 4:
                type = BlockType.Z;
                this.board.get(21).get(4).isActive = true;
                this.board.get(21).get(4).type = type;
                this.board.get(21).get(5).isActive = true;
                this.board.get(21).get(5).type = type;
                this.board.get(20).get(5).isActive = true;
                this.board.get(20).get(5).type = type;
                this.board.get(20).get(6).isActive = true;
                this.board.get(20).get(6).type = type;
                break;
            case 5:
                type = BlockType.S;
                this.board.get(21).get(6).isActive = true;
                this.board.get(21).get(6).type = type;
                this.board.get(21).get(5).isActive = true;
                this.board.get(21).get(5).type = type;
                this.board.get(20).get(5).isActive = true;
                this.board.get(20).get(5).type = type;
                this.board.get(20).get(4).isActive = true;
                this.board.get(20).get(4).type = type;
                break;
            case 6:
                type = BlockType.I;
                this.board.get(23).get(4).isActive = true;
                this.board.get(23).get(4).type = type;
                this.board.get(22).get(4).isActive = true;
                this.board.get(22).get(4).type = type;
                this.board.get(21).get(4).isActive = true;
                this.board.get(21).get(4).type = type;
                this.board.get(20).get(4).isActive = true;
                this.board.get(20).get(4).type = type;
                break;
            case 7:
                type = BlockType.O;
                this.board.get(21).get(5).isActive = true;
                this.board.get(21).get(5).type = type;
                this.board.get(21).get(4).isActive = true;
                this.board.get(21).get(4).type = type;
                this.board.get(20).get(5).isActive = true;
                this.board.get(20).get(5).type = type;
                this.board.get(20).get(4).isActive = true;
                this.board.get(20).get(4).type = type;
        }
        this.isActive = true;
        repaint();
    }

    // for testing only
    void print() {
        for (int i = 23; i >= 0; i--) {
            for (int j = 0; j < 10; j++) {
                if (board.get(i).get(j).type == BlockType.BLANK) {
                    System.out.print("[ ]");
                } else {
                    System.out.print("[" + board.get(i).get(j).type + "]");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        ClassLoader classLoader = this.getClass().getClassLoader();
        ImageIcon blankImage = new ImageIcon(classLoader.getResource("blank.png"));
        ImageIcon iImage = new ImageIcon(classLoader.getResource("I.png"));
        ImageIcon jImage = new ImageIcon(classLoader.getResource("J.png"));
        ImageIcon lImage = new ImageIcon(classLoader.getResource("L.png"));
        ImageIcon oImage = new ImageIcon(classLoader.getResource("O.png"));
        ImageIcon sImage = new ImageIcon(classLoader.getResource("S.png"));
        ImageIcon tImage = new ImageIcon(classLoader.getResource("T.png"));
        ImageIcon zImage = new ImageIcon(classLoader.getResource("Z.png"));

        int row=0;
        for (int i = BOARD_VALID_HEIGHT - 1; i >= 0; i--) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                BlockType type = this.board.get(i).get(j).type;
                switch (type) {
                    //case BlockType.BLANK -> this.add(new JLabel(blankImage));
                    case BlockType.BLANK -> blankImage.paintIcon(this, gr, j*30, row*30);
                    case BlockType.I -> iImage.paintIcon(this, gr, j*30, row*30);
                    case BlockType.J -> jImage.paintIcon(this, gr, j*30, row*30);
                    case BlockType.L -> lImage.paintIcon(this, gr, j*30, row*30);
                    case BlockType.O -> oImage.paintIcon(this, gr, j*30, row*30);
                    case BlockType.S -> sImage.paintIcon(this, gr, j*30, row*30);
                    case BlockType.T -> tImage.paintIcon(this, gr, j*30, row*30);
                    case BlockType.Z -> zImage.paintIcon(this, gr, j*30, row*30);
                }
            }
            row++;
        }
    }
}
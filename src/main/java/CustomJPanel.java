import javax.swing.*;
import java.awt.*;

public class CustomJPanel extends JPanel {

    Tetris tetris;
    public CustomJPanel(Tetris tetris) {
        setFocusable(true);
        requestFocusInWindow();
        this.tetris = tetris;

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        ClassLoader classLoader = this.getClass().getClassLoader();
        ImageIcon blankImage = new ImageIcon(classLoader.getResource("blank.png"));
        ImageIcon iImage = new ImageIcon(classLoader.getResource("I.png"));
        ImageIcon jImage = new ImageIcon(classLoader.getResource("J.png"));
        ImageIcon lImage = new ImageIcon(classLoader.getResource("L.png"));
        ImageIcon oImage = new ImageIcon(classLoader.getResource("O.png"));
        ImageIcon sImage = new ImageIcon(classLoader.getResource("S.png"));
        ImageIcon tImage = new ImageIcon(classLoader.getResource("T.png"));
        ImageIcon zImage = new ImageIcon(classLoader.getResource("Z.png"));

        //JPanel tetrisJPanel = new JPanel();
        CustomJPanel tetrisJPanel = new CustomJPanel(tetris);
        tetrisJPanel.setLayout(new GridLayout(Tetris.BOARD_VALID_HEIGHT, Tetris.BOARD_WIDTH));
        for (int i = Tetris.BOARD_VALID_HEIGHT-1; i >=0; i--) {
            for (int j = 0; j < Tetris.BOARD_WIDTH; j++) {
                BlockType type = tetris.board.get(i).get(j).type;
                switch (type) {
                    case BlockType.BLANK -> tetrisJPanel.add(new JLabel(blankImage));
                    case BlockType.I -> tetrisJPanel.add(new JLabel(iImage));
                    case BlockType.J -> tetrisJPanel.add(new JLabel(jImage));
                    case BlockType.L -> tetrisJPanel.add(new JLabel(lImage));
                    case BlockType.O -> tetrisJPanel.add(new JLabel(oImage));
                    case BlockType.S -> tetrisJPanel.add(new JLabel(sImage));
                    case BlockType.T -> tetrisJPanel.add(new JLabel(tImage));
                    case BlockType.Z -> tetrisJPanel.add(new JLabel(zImage));
                }
            }
        }

    }


}

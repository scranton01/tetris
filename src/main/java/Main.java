import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        //initialize Board
        Tetris tetris = new Tetris();
        //board.print();

        JFrame jFrame = new JFrame();
        jFrame.setName("Tetris");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
        tetris.print();

        jFrame.getContentPane().removeAll();
        jFrame.getContentPane().invalidate();
        //jFrame.getContentPane().add(tetris);
        jFrame.add(tetris);
        jFrame.revalidate();
        jFrame.pack();
//        jFrame.addKeyListener();





        //spawn new piece
        //board.generateNewBlock();

//        frame.pack();
        jFrame.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        System.out.println("up");
                        tetris.rotate(RotateAction.RIGHT);
                        break;
                    case KeyEvent.VK_DOWN:
                        System.out.println("down");
                        tetris.rotate(RotateAction.LEFT);
                        break;
                    case KeyEvent.VK_LEFT:
                        System.out.println("left");
                        tetris.move(MoveAction.LEFT);
                        break;
                    case KeyEvent.VK_RIGHT:
                        System.out.println("right");
                        tetris.move(MoveAction.RIGHT);
                        break;
                    case KeyEvent.VK_SPACE:
                        System.out.println("right");
                        tetris.drop();
                        break;
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        });

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Runnable dropRunnable = () -> {
            if (!tetris.isActive) {
                tetris.generateNewBlock();
            }
            tetris.drop();
            List<Integer> deletedRow = tetris.eliminateFilledRow();
            if (!deletedRow.isEmpty()) {
                for (int row : deletedRow) {
                    tetris.dropBlocksAbove(row);
                }
            }
            tetris.print();
        };
        executor.scheduleAtFixedRate(dropRunnable, 0, 1, TimeUnit.SECONDS);
    }
}

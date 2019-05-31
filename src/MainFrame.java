import java.awt.Dimension;
import javax.swing.JFrame;

public class MainFrame extends JFrame
{
    private static final long serialVersionUID = 1L;
    private static final String TITLE = "Dijkstra's Algorithm Simulation";
    private static final Dimension SIZE = new Dimension(1200, 700);
        
    public static void main(String[] args)
    {
        MainFrame frame = new MainFrame();
        frame.setTitle(TITLE);
        frame.setPreferredSize(SIZE);
        frame.setMinimumSize(SIZE);
        frame.setMaximumSize(SIZE);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
        
        MainPanel panel = new MainPanel();
        panel.setFocusable(true);
        frame.add(panel);
        
        frame.setVisible(true);
    }
}

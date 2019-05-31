import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class MainPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener, KeyListener
{
    private ArrayList<Node> nodes;
    private Timer searchTimer;
    private Timer weightTimer;
    
    private Node start;
    private Node end;
    private Node nodeClicked;
    private Node nodeReleased;
    
    private boolean addNode;
    private boolean mouseDragged;
    
    private int weight;
    private int connectX;
    private int connectY;
    
    public MainPanel()
    {
        nodes = new ArrayList<>();
        searchTimer = new Timer(1000, this);
        
        start = null;
        end = null;
        
        addNode = true;
        nodeClicked = null;
        weightTimer = new Timer(100, this);
        
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        setBackground(Color.GRAY);
        
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("TimesRoman", Font.BOLD, 17));
        
        //Draw Controls
        g2.drawString("Controls", 20, 20);
        g2.drawString("Left Click - Place Node/Select Points (Toggle)", 20, 40);
        g2.drawString("Right Click - Delete Node", 20, 60);
        g2.drawString("To connect nodes, left click", 20, 80);
        g2.drawString("  and drag, then click again when", 20, 100);
        g2.drawString("  the weight is appropriate.", 20, 120);
        g2.drawString("Enter - Find shortest path.", 20, 140);
        
        //Draw Nodes
        for(int i = 0; i < nodes.size(); i++)
        {
            Color c = Color.BLACK;
            if(nodes.get(i) == start)
            {
                c = Color.GREEN;
            }
            else if(nodes.get(i) == end)
            {
                c = Color.RED;
            }
            
            g2.setColor(c);
            g2.draw(nodes.get(i).getShape());
            
            g2.drawString(String.valueOf(nodes.get(i).getNum()), nodes.get(i).getX(), nodes.get(i).getY());
        }
        
        //Draw current weight adjustment
        if(weightTimer.isRunning())
        {
            g2.setColor(Color.BLACK);
            g2.drawString(String.valueOf(weight), connectX, connectY);
        }
        
        //Draw connections and respsective weights between nodes
        for(int i = 0; i < nodes.size(); i++)
        {
            nodes.get(i).drawConnections(g2);
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) 
    {
        if(SwingUtilities.isLeftMouseButton(me))
        {
            if(weightTimer.isRunning())
            {
                weightTimer.stop();
                nodeClicked.connect(nodeReleased, weight);
                nodeReleased.connect(nodeClicked, weight);

                repaint();
            }
            else
            {
                if(addNode)
                {
                    nodes.add(new Node(me.getX(), me.getY(), nodes.size() + 1));
                }
                else
                {
                    if(nodeClicked == start)
                    {
                        start = null;
                    }
                    else if(nodeClicked == end)
                    {
                        end = null;
                    }
                    else
                    {
                        if(start != null && end == null)
                        {
                            end = nodeClicked;
                        }
                        else if(start == null)
                        {
                            start = nodeClicked;
                        }
                    }
                }
            }
        }
        else if(SwingUtilities.isRightMouseButton(me) && !weightTimer.isRunning())
        {
            if(nodeClicked != null)
            {
                nodeClicked.disconnectAll();
                nodes.remove(nodeClicked);
            }
        }
        
        nodeClicked = null;
        addNode = true;

        repaint();
    }

    @Override
    public void mousePressed(MouseEvent me) 
    {
        if(!weightTimer.isRunning())
        {
            nodeClicked = null;
            nodeReleased = null;
            addNode = true;

            for(int i = 0; i < nodes.size(); i++)
            {
                int x = me.getX();
                int y = me.getY();
                if(nodes.get(i).getShape().contains(x, y))
                {
                    addNode = false;
                    nodeClicked = nodes.get(i);
                    break;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) 
    {
        if(mouseDragged)
        {
            for(int i = 0; i < nodes.size(); i++)
            {
                int x = me.getX();
                int y = me.getY();
                if(nodes.get(i).getShape().contains(x, y))
                {
                    nodeReleased = nodes.get(i);
                    break;
                }
            }
            
            if(nodeReleased != nodeClicked && nodeClicked != null && nodeReleased != null)
            {
                weightTimer.start();
            }
            
            mouseDragged = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) 
    {
        if(weightTimer == ae.getSource())
        {
            Point mouse = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(mouse, this);
            weight = (int)Math.sqrt(Math.pow(mouse.getX() - nodeReleased.getX(), 2) + Math.pow(mouse.getY() - nodeReleased.getY(), 2)) / 10;
            
            connectX = (int)(nodeReleased.getX() + ((nodeClicked.getX() - nodeReleased.getX()) / 2));
            connectY = (int)(nodeReleased.getY() + ((nodeClicked.getY() - nodeReleased.getY()) / 2));
        }
        else if(searchTimer == ae.getSource())
        {
            //Begin search
            ArrayList<Node> nodeList = new ArrayList<>();
            ArrayList<Integer> nodeWeights = new ArrayList<>();
            for(int i = 0; i < nodes.size(); i++)
            {
                if(!nodes.get(i).isVisited())
                {
                    nodeList.add(nodes.get(i));
                    nodeWeights.add(nodes.get(i).getWeight());
                }
            }
            
            if(!nodeList.isEmpty())
            {
                int index = nodeWeights.indexOf(Collections.min(nodeWeights));
                nodeList.get(index).updateAdjacent();
            }
            else
            {
                searchTimer.stop();
                
                for(int i = 0; i < nodes.size(); i++)
                {
                    nodes.get(i).stopVisual();
                }
                
                ArrayList<Node> path = end.getPath();
                
                for(int i = 1; i < path.size(); i++)
                {
                    path.get(i).highlightConnection(path.get(i-1));
                    path.get(i-1).highlightConnection(path.get(i));
                }
            }
        }
        
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent ke) 
    {
        if(start != null && end != null && ke.getKeyCode() == KeyEvent.VK_ENTER)
        {
            for(int i = 0; i < nodes.size(); i++)
            {
                nodes.get(i).reset();
            }

            start.setStartNode();
            searchTimer.start();
        }
    }
        
    @Override
    public void mouseDragged(MouseEvent me) 
    {
        mouseDragged = true;
    }
    
    @Override
    public void keyReleased(KeyEvent ke) 
    {}
    
    @Override
    public void mouseMoved(MouseEvent me) 
    {}
    
    @Override
    public void mouseEntered(MouseEvent me) 
    {}

    @Override
    public void mouseExited(MouseEvent me) 
    {}
    
    @Override
    public void keyTyped(KeyEvent ke) 
    {}
}

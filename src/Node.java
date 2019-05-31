import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collections;

public class Node 
{
    private boolean isVisited;
    private boolean nodeActive;
    private boolean isEndNode;
    
    private ArrayList<Node> connectedNodes;
    private ArrayList<Integer> nodeWeights;
    private ArrayList<Node> highlightedNodes;
    
    private Node lastNode;
    private Shape nodeShape;
    
    private int currentWeight;
    private int num;
    
    int x;
    int y;
    
    private final int RADIUS = 30;
    
    public Node(int x, int y, int num)
    {
        currentWeight = Integer.MAX_VALUE;
        nodeShape = new Ellipse2D.Double(x - RADIUS, y - RADIUS, 2*RADIUS, 2*RADIUS);
        
        highlightedNodes = new ArrayList<>();
        connectedNodes = new ArrayList<>();
        nodeWeights = new ArrayList<>();
        
        this.x = x;
        this.y = y;
        this.num = num;
    }
    
    public void connect(Node node, int weight)
    {
        if(!connectedNodes.contains(node))
        {
            connectedNodes.add(node);
            nodeWeights.add(weight);
        }
    }
    
    public boolean disconnect(Node node)
    {
        int index = connectedNodes.indexOf(node);
        if(index != -1)
        {
            connectedNodes.remove(index);
            nodeWeights.remove(index);
            node.disconnect(this);
            return true;
        }
        return false;
    }
    
    public void disconnectAll()
    {
        for(int i = connectedNodes.size() - 1; i >= 0; i--)
        {
            connectedNodes.get(i).disconnect(this);
        }
        connectedNodes.clear();
        nodeWeights.clear();
    }
    
    public void setStartNode()
    {
        currentWeight = 0;
    }
    
    public void updateAdjacent()
    {
        nodeActive = true;
        isVisited = true;
        
        for(int i = 0; i < connectedNodes.size(); i++)
        {
            connectedNodes.get(i).relaxWeight(this);
        }
    }
    
    public void relaxWeight(Node node)
    {
        int connection = nodeWeights.get(connectedNodes.indexOf(node));
        if(node.getWeight() + connection < currentWeight)
        {
            lastNode = node;
            currentWeight = node.getWeight() + connection;
        }
    }
    
    public void highlightConnection(Node node)
    {
        highlightedNodes.add(node);
    }
    
    public void drawConnections(Graphics2D g2)
    {
        for(int i = 0; i < connectedNodes.size(); i++)
        {
            if(y < connectedNodes.get(i).getY())
            {
                Node n = connectedNodes.get(i);
                
                g2.setColor((nodeActive || (highlightedNodes.contains(n))) ? Color.RED : Color.BLACK);
                
                double theta = Math.atan2(x - n.getX(), y - n.getY());
                theta %= Math.PI / 2;
                int yInc = (int)(Math.sin(theta) * RADIUS) * (theta < 0 ? -1 : 1);
                int xInc = (int)(Math.cos(theta) * RADIUS) * (theta < 0 ? -1 : 1);

                g2.drawLine(x - xInc, y + yInc, n.getX() + xInc, n.getY() - yInc);

                int midX = (int)(x + ((n.getX() - x) / 2));
                int midY = (int)(y + ((n.getY() - y) / 2));

                g2.setColor(Color.WHITE);
                g2.drawString(String.valueOf(nodeWeights.get(i)), midX, midY);
            }
        }
        
        highlightedNodes.clear();
    }
    
    public ArrayList<Node> getPath()
    {
        ArrayList<Node> finalPath = new ArrayList<>();
        
        Node currNode = this;
        while(currNode.getLast() != null)
        {
            finalPath.add(currNode);
            currNode = currNode.getLast();
        }
        finalPath.add(currNode);
        
        Collections.reverse(finalPath);
        
        return finalPath;
    }
    
    public boolean isVisited()
    {
        return this.isVisited;
    }
    
    public Node getLast()
    {
        return lastNode;
    }
    
    public int getNum()
    {
        return num;
    }
    
    public void stopVisual()
    {
        nodeActive = false;
    }
    
    public int getWeight()
    {
        return currentWeight;
    }
    
    public Shape getShape()
    {
        return nodeShape;
    }
    
    public boolean isEndNode()
    {
        return this.isEndNode;
    }
    
    public int getX()
    {
        return x;
    }
    
    public int getY()
    {
        return y;
    }
    
    public void reset()
    {
        currentWeight = Integer.MAX_VALUE;
        isVisited = false;
        lastNode = null;
    }
}

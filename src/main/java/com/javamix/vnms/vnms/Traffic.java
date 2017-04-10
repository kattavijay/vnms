package com.javamix.vnms.vnms; 
/*
 * This class is to display the traffic and connection graphically
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Traffic
	extends JPanel
	implements ActionListener, MouseMotionListener {
		
	private VirtualWatch vpn;

	// initialize when statisticsPanel is initialized
	public LinkedList VPNPairList;
	public LinkedList NodeList;
	private Hashtable NodeRealLocation;

	private Random random;
	private static int radius = 180;

	private JLabel deviceLabel;

	private static JPanel leftPanel;
	private static JPanel controls;
	private static JPanel legend;

	public static JPanel computerPanel;

	public static final int PLAINTEXT = 0;
	public static final int IPSEC = 1;
	public static final int PPTP = 2;
	public static final int HTTPS = 3;
	public static final int SSH = 4;

	public Traffic(VirtualWatch vpn, int Width, int Height) {
		super(false);
		VPNPairList = new LinkedList();
		NodeList = new LinkedList();
		NodeRealLocation = new Hashtable();
		
		this.vpn = vpn;

		random = new Random();

		setLayout(new BorderLayout());

		leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(0, 1));
		
		JLabel ipSecLabel = new JLabel("  IPSec ----");
		ipSecLabel.setForeground(Color.BLUE);
		JLabel pptpLabel = new JLabel("  PPTP ----");
		pptpLabel.setForeground(Color.GREEN);
		JLabel httpsLabel = new JLabel("  HTTPS ----");
		httpsLabel.setForeground(Color.ORANGE);
		JLabel sshLabel = new JLabel("  SSH ----");
		sshLabel.setForeground(Color.RED);
		legend = new JPanel();
		legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));
		legend.add(ipSecLabel);
		legend.add(pptpLabel);
		legend.add(httpsLabel);
		legend.add(sshLabel);
		legend.setBorder(BorderFactory.createTitledBorder("Legend"));
		leftPanel.add(legend);

		add(leftPanel, BorderLayout.WEST);

		computerPanel = new JPanel();
		//computerPanel.setLayout(new GridLayout(8,8));
		computerPanel.setLayout(null);
		computerPanel.setBorder(BorderFactory.createTitledBorder("Network"));

		add(computerPanel, BorderLayout.CENTER);

	}

	public void paint(Graphics g) {
		super.paint(g);
		drawLines(g);
		if (computerPanel.getWidth() > computerPanel.getHeight()) {
			radius = computerPanel.getHeight() / 2;
		} else {
			radius = computerPanel.getWidth() / 2;
		}
		radius = (int) (radius * 0.8);
	}

	private void drawLines(Graphics g) {

		Iterator listIterator = VPNPairList.listIterator();
		while (listIterator.hasNext()) {
			Pair aPair = (Pair) listIterator.next();
			String from = aPair.from;
			String to = aPair.to;

			if (aPair.type == IPSEC) {
				g.setColor(Color.BLUE);
			} else if (aPair.type == PPTP) {
				g.setColor(Color.GREEN);
			} else if (aPair.type == HTTPS) {
				g.setColor(Color.ORANGE);
			} else if (aPair.type == SSH) {
				g.setColor(Color.RED);
			} else {
				g.setColor(Color.BLACK);
			}

			Point fromPoint = (Point) NodeRealLocation.get(from.toString());
			Point toPoint = (Point) NodeRealLocation.get(to.toString());

			int indent = computerPanel.getLocation().x;
			g.drawLine(
				fromPoint.x + indent + 100,
				fromPoint.y + 30,
				toPoint.x + indent + 100,
				toPoint.y + 30);
		}
	}

	public boolean AddVPNPair(String from, String to, int type) {

		boolean gotNewNode = false;

		Node fromNode = new Node(from);
		Node toNode = new Node(to);
		Pair newPair = new Pair(from, to, type);

		int x, y;
		// check if the fromNode is already in NodeList
		if (NodeListContainsNode(NodeList, fromNode)) {
		} else {

			x = getx();
			y = gety(x);
			fromNode.x = x;
			fromNode.y = y;

			NodeList.add(fromNode);
			fromNode.resolveIPtoHost();
			addNodeToPanel(fromNode);
			gotNewNode = true;
		}

		if (NodeListContainsNode(NodeList, toNode)) {
		} else {
			x = getx();
			y = gety(x);
			toNode.x = x;
			toNode.y = y;
			NodeList.add(toNode);
			toNode.resolveIPtoHost();
			addNodeToPanel(toNode);
			gotNewNode = true;
		}

		Pair aPair;

		// if the pair contains at least one new node
		// then it must be a new pair
		if (gotNewNode) {
			VPNPairList.add(newPair);

			String connectionType;
			switch (newPair.type) {
				case (Traffic.IPSEC) :
					connectionType = "IPsec";
					break;
				case (Traffic.HTTPS) :
					connectionType = "HTTPS";
					break;
				case (Traffic.SSH)   :
					connectionType = "SSH";
					break;
				case (Traffic.PPTP) :
					connectionType = "PPTP";
					break;
				case (Traffic.PLAINTEXT) :
					connectionType = "unencrypted";
					break;
				default :
					connectionType = "undefined";
					break;
			}

			vpn.statisticsPanel.connectionModel.addRow(
				new Object[] { newPair.from, newPair.to, connectionType });
			return true;
		}

		// if both node already exists, check if the pair
		// already exists, if not, then add it
		aPair = PairListContainsPair(VPNPairList, newPair);
		if (aPair == null) {
			VPNPairList.add(newPair);

			String connectionType;
			switch (newPair.type) {
				case (Traffic.IPSEC) :
					connectionType = "IPsec";
					break;
				case (Traffic.HTTPS) :
					connectionType = "HTTPS";
					break;
				case (Traffic.SSH)   :
					connectionType = "SSH";
					break;
				case (Traffic.PPTP) :
					connectionType = "PPTP";
					break;
				case (Traffic.PLAINTEXT) :
					connectionType = "unencrypted";
					break;
				default :
					connectionType = "undefined";
					break;
			}
			vpn.statisticsPanel.connectionModel.addRow(
				new Object[] { newPair.from, newPair.to, connectionType });
			return true;
		} else
			// if both node already exists, and the pair also exists
			// then update its connection type
			{
			int numRows = vpn.statisticsPanel.connectionModel.getRowCount();
			for (int i = 0; i < numRows; i++) {
				boolean match = false;
				String src = (String) vpn.statisticsPanel.connectionModel.getValueAt(i, 0);
				String dst = (String) vpn.statisticsPanel.connectionModel.getValueAt(i, 1);
				if ((src.compareTo(aPair.from) == 0)
					&& (dst.compareTo(aPair.to) == 0)) {
					match = true;
				} else if (
					(src.compareTo(aPair.to) == 0)
						&& (dst.compareTo(aPair.from) == 0)) {
					match = true;
				}

				if (match) // if there is a match of connection
					{
					// connections only upgrade, won't downgrade
					if (((String) vpn.statisticsPanel.connectionModel.getValueAt(i, 2))
						.compareTo("unencrypted")
						== 0) {
						// old entry found, update!
						String connectionType;
						switch (newPair.type) {
							case (Traffic.IPSEC) :
								connectionType = "IPsec";
								break;
							case (Traffic.HTTPS) :
								connectionType = "HTTPS";
								break;
							case (Traffic.SSH)   :
								connectionType = "SSH" ;
								break;
							case (Traffic.PPTP) :
								connectionType = "PPTP";
								break;
							case (Traffic.PLAINTEXT) :
								connectionType = "unencrypted";
								break;
							default :
								connectionType = "undefined";
								break;
						}
						vpn.statisticsPanel.connectionModel.setValueAt(connectionType, i, 2);
					}
				}
			}
			aPair.updateType(newPair); // update the traffic panel

			return true;
		}
	}

	private void addNodeToPanel(Node aNode) {
		ImageIcon icon = createImageIcon("images/Server.gif", "");

		String nodeLabel;
		if (aNode.resolved)
		{
			nodeLabel = aNode.hostname;
		} else
		{
			nodeLabel = aNode.ip;
		}
		JLabel aComputer = new JLabel(nodeLabel, icon, JLabel.CENTER);
		aComputer.setName(aNode.ip); // no matter what the label has become
									 // the name stays its ip
		//  Set the position of the text, relative to the icon:
		aComputer.setVerticalTextPosition(JLabel.TOP);
		aComputer.setHorizontalTextPosition(JLabel.CENTER);
		//aComputer.setBounds(aNode.x - 100, aNode.y - 35, 200, 50);
		//aComputer.setBounds(0, 0, 200, 50);
		aComputer.setBounds(
			aNode.x + computerPanel.getWidth() / 2 - 100,
			aNode.y + computerPanel.getHeight() / 2 - 30,
			200,
			50);

		aComputer.setForeground(Color.MAGENTA);
		computerPanel.add(aComputer);
		computerPanel.validate();

		NodeRealLocation.put(aNode.ip.toString(), aComputer.getLocation());

		aComputer.addMouseMotionListener(this);

	}

	private int getx() {

		int x;
		x = random.nextInt(radius);

		if (random.nextDouble() < 0.5)
			x = -1 * x;
		//x = x + centerX;
		return x;
	}

	private int gety(int x) {

		//x = x - centerX;

		int y;
		y = (int) java.lang.Math.sqrt((double) (radius * radius - x * x));

		if (random.nextDouble() < 0.5)
			y = -1 * y;
		//y = y + centerY;

		return y;
	}

	private boolean NodeListContainsNode(LinkedList aNodeList, Node aNode) {
		Iterator listIterator = aNodeList.listIterator();

		while (listIterator.hasNext()) {
			Node currentNode = (Node) listIterator.next();
			if (currentNode.equals(aNode))
				return true;
		}
		return false;

	}

	private int NodeListx(LinkedList aNodeList, Node aNode) {
		Iterator listIterator = aNodeList.listIterator();
		while (listIterator.hasNext()) {
			Node currentNode = (Node) listIterator.next();
			if (currentNode.equals(aNode))
				return currentNode.x;
		}
		System.err.println("Error on X!");
		return -1;
	}

	private int NodeListy(LinkedList aNodeList, Node aNode) {
		Iterator listIterator = aNodeList.listIterator();
		while (listIterator.hasNext()) {
			Node currentNode = (Node) listIterator.next();
			if (currentNode.equals(aNode))
				return currentNode.y;
		}
		System.err.println("Error on Y!");
		return -1;
	}

	private Pair PairListContainsPair(LinkedList PairList, Pair aPair) {
		Iterator listIterator = PairList.listIterator();

		Pair foundPair;
		while (listIterator.hasNext()) {
			Pair currentPair = (Pair) listIterator.next();
			if (currentPair.equals(aPair)) {
				foundPair = currentPair;
				return foundPair;
			}
		}
		return null;
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	/* Copied from Java homepage */
	protected static ImageIcon createImageIcon(
		String path,
		String description) {
		java.net.URL imgURL = Traffic.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public void actionPerformed(ActionEvent e) {
		if ("rearrange".equals(e.getActionCommand())) {
			RearrangeComputers();
		} else {
		}
	}

	public void RearrangeComputers() {
		computerPanel.removeAll();

		Iterator listIterator = NodeList.listIterator();
		while (listIterator.hasNext()) {
			Node currentNode = (Node) listIterator.next();
			currentNode.x = getx();
			currentNode.y = gety(currentNode.x);

			addNodeToPanel(currentNode);
		}
		repaint();
	}

	public void mouseDragged(MouseEvent arg0) {

		if (arg0.getSource() instanceof JLabel) {
			JLabel theLabel = (JLabel) arg0.getSource();

			Point location = theLabel.getLocation();
			int gCenterX = location.x + arg0.getX();
			int gCenterY = location.y + arg0.getY();

			location.x = gCenterX - theLabel.getWidth() / 2;
			location.y = gCenterY - theLabel.getHeight() / 2;

			theLabel.setLocation(location);
			// need to use name to identify the entry in the NodeRealLocation
			// or else a new entry will be made, moving label won't work
			NodeRealLocation.put(theLabel.getName(), location);

			repaint();
		}

	}

	public void mouseMoved(MouseEvent arg0) {
	}

}

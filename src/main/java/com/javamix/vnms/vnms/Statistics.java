 
/*
 * This class keeps all the statistics of connections and packets
 */
package com.javamix.vnms.vnms;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Statistics extends JPanel {

	public int numIPSEC;
	public int numPPTP;
	public int numHTTPS;
	public int numSSH;
	public int numOthers;
	private int numTotal;

	private JPanel packetStat;
	private JLabel numIPSECLabel;
	private JLabel numPPTPLabel;
	private JLabel numHTTPSLabel;
	private JLabel numSSHLabel;
	private JLabel numOthersLabel;
	private JLabel numTotalLabel;

	private JPanel connectionStat;
	private JLabel IPSECconnectionLabel;
	private JLabel PPTPconnectionLabel;
	private JLabel HTTPSconnectionLabel;
	private JLabel SSHconnectionLabel;
	private JLabel OthersconnectionLabel;
	private JLabel TotalconnectionLabel;
	private int IPSECconnections;
	private int PPTPconnections;
	private int HTTPSconnections;
	private int SSHconnections;
	private int Othersconnections;
	private int Totalconnections;
	
	private JPanel packetConnectionPanel;
	
	private JPanel connections;
	private JTable connectionTable;
	public DefaultTableModel connectionModel;
	private JScrollPane connectionScrollPane;
	
	private JPanel hostnames;
	private JTable hostnameTable;
	public DefaultTableModel hostnameModel;
	private JScrollPane hostnameScrollPane;
	
	private JPanel connectionHostnamePanel;
	
	private VirtualWatch vpn;

	public Statistics(VirtualWatch vpn) {
		this.vpn = vpn;

		setLayout(new BorderLayout());

		/*
		 * Packet Statistic Panel
		 */
		packetStat = new JPanel();
		packetStat.setLayout(new BoxLayout(packetStat, BoxLayout.Y_AXIS));
		packetStat.setBorder(
			BorderFactory.createTitledBorder("Packet Statistics"));

		numIPSECLabel = new JLabel();
		numPPTPLabel = new JLabel();
		numHTTPSLabel = new JLabel();
		numSSHLabel = new JLabel();
		numOthersLabel = new JLabel();
		numTotalLabel = new JLabel();
		refreshPacketStatistics();
		packetStat.add(numIPSECLabel);
		packetStat.add(numPPTPLabel);
		packetStat.add(numHTTPSLabel);
		packetStat.add(numSSHLabel);
		packetStat.add(numOthersLabel);
		packetStat.add(numTotalLabel);

		/*
		 * Connection Statistic Panel
		 */
		connectionStat = new JPanel();
		connectionStat.setLayout(
			new BoxLayout(connectionStat, BoxLayout.Y_AXIS));
		connectionStat.setBorder(
			BorderFactory.createTitledBorder("Connection Statistics"));

		IPSECconnectionLabel = new JLabel();
		PPTPconnectionLabel = new JLabel();
		HTTPSconnectionLabel = new JLabel();
		SSHconnectionLabel = new JLabel();
		OthersconnectionLabel = new JLabel();
		TotalconnectionLabel = new JLabel();
		refreshConnectionStatistics();
		
		connectionStat.add(IPSECconnectionLabel);
		connectionStat.add(PPTPconnectionLabel);
		connectionStat.add(HTTPSconnectionLabel);
		connectionStat.add(SSHconnectionLabel);
		connectionStat.add(OthersconnectionLabel);
		connectionStat.add(TotalconnectionLabel);
		
		packetConnectionPanel = new JPanel();

		packetConnectionPanel.setLayout(new GridLayout(1,2));
		packetConnectionPanel.add(packetStat);
		packetConnectionPanel.add(connectionStat);
		
		/*
		 * connections
		 */
		connections = new JPanel();
		connectionModel = new DefaultTableModel();
		TableSorter sorter = new TableSorter(connectionModel);
		connectionTable = new JTable(sorter);
		sorter.addMouseListenerToHeaderInTable(connectionTable);
		connectionScrollPane = new JScrollPane(connectionTable);		
    
		connectionModel.addColumn("Host 1");
		connectionModel.addColumn("Host 2");
		connectionModel.addColumn("Connection Type");
		
		/*
		 * IP address / hostname resolution
		 */
		hostnames = new JPanel();
		hostnameModel = new DefaultTableModel();
		TableSorter hostnameSorter = new TableSorter(hostnameModel);
		hostnameTable = new JTable(hostnameSorter);
		hostnameSorter.addMouseListenerToHeaderInTable(hostnameTable);
		hostnameScrollPane = new JScrollPane(hostnameTable);
		
		hostnameModel.addColumn("IP Address");
		hostnameModel.addColumn("Hostname");
		
		connectionHostnamePanel = new JPanel();
		connectionHostnamePanel.setLayout(new GridLayout(1,2));
		connectionHostnamePanel.add(hostnameScrollPane);
		connectionHostnamePanel.add(connectionScrollPane);
		
		/*
		 * add everything into the big panel
		 */		 
		add(packetConnectionPanel, BorderLayout.NORTH);
		add(connectionHostnamePanel, BorderLayout.CENTER);		
		
	}

	public void paint(Graphics g) {
		refreshPacketStatistics();
		refreshConnectionStatistics();

		packetConnectionPanel.repaint();
		connectionHostnamePanel.repaint();
	}

	/* recalcualte the packet statistics */
	public void refreshPacketStatistics() {

		int total = numIPSEC + numPPTP + numHTTPS + numSSH + numOthers;
		numTotal = total;
		if (total == 0)
			total = 1;
		numIPSECLabel.setText(
			"IPSec Packets: "
				+ numIPSEC
				+ " ( "
				+ (numIPSEC * 100 / total)
				+ " % )");
		numPPTPLabel.setText(
			"PPTP Packets: "
				+ numPPTP
				+ " ( "
				+ (numPPTP * 100 / total)
				+ " % )");
		numHTTPSLabel.setText(
			"HTTPS Packets: "
				+ numHTTPS
				+ " ( "
				+ (numHTTPS * 100 / total)
				+ " % )");
		numSSHLabel.setText(
			"SSH Packets: "
				+ numSSH
				+ " ( "
				+ (numSSH * 100 / total)
				+ " % )");
		numOthersLabel.setText(
			"Other Packets: "
				+ numOthers
				+ " ( "
				+ (numOthers * 100 / total)
				+ " % )");
		numTotalLabel.setText(
			"Total Packets: "
				+ numTotal
				+ " ( "
				+ (numTotal * 100 / total)
				+ " % )");
	}

	/* recalculate connection statistics */
	public void refreshConnectionStatistics() {
		IPSECconnections = 0;
		PPTPconnections = 0;
		HTTPSconnections = 0;
		SSHconnections = 0;
		Othersconnections = 0;
		Totalconnections = 0;

		Iterator listIterator = vpn.trafficPanel.VPNPairList.listIterator();
		while (listIterator.hasNext()) {
			Pair aPair = (Pair) listIterator.next();
			if (aPair.type == Traffic.IPSEC) {
				IPSECconnections++;
			} else if (aPair.type == Traffic.PPTP) {
				PPTPconnections++;
			} else if (aPair.type == Traffic.HTTPS) {
				HTTPSconnections++;
			} else if (aPair.type == Traffic.SSH) {
				SSHconnections++;
			} else {
				Othersconnections++;
			}
			Totalconnections++;
		}

		int ctotal =
			IPSECconnections
				+ PPTPconnections
				+ HTTPSconnections
				+ SSHconnections
				+ Othersconnections;
		if (ctotal == 0)
			ctotal = 1;
		IPSECconnectionLabel.setText(
			"IPSEC Connections: "
				+ IPSECconnections
				+ " ( "
				+ (IPSECconnections * 100 / ctotal)
				+ " % )");
		PPTPconnectionLabel.setText(
			"PPTP Connections: "
				+ PPTPconnections
				+ " ( "
				+ (PPTPconnections * 100 / ctotal)
				+ " % )");
		HTTPSconnectionLabel.setText(
			"HTTPS Connections: "
				+ HTTPSconnections
				+ " ( "
				+ (HTTPSconnections * 100 / ctotal)
				+ " % )");
		SSHconnectionLabel.setText(
			"SSH Connections: "
				+ SSHconnections
				+ " ( "
				+ (SSHconnections * 100 / ctotal)
				+ " % )");
		OthersconnectionLabel.setText(
			"Other Connections: "
				+ Othersconnections
				+ " ( "
				+ (Othersconnections * 100 / ctotal)
				+ " % )");
		TotalconnectionLabel.setText(
			"Total Connections: "
				+ Totalconnections
				+ " ( "
				+ (Totalconnections * 100 / ctotal)
				+ " % )");
	}
	
	public void refreshHostname()
	{
		// remove all rows
		while (hostnameModel.getRowCount() != 0)
		{
			hostnameModel.removeRow(0);
		}
		
		Iterator listIterator = vpn.trafficPanel.NodeList.listIterator();
		while (listIterator.hasNext()) {
			Node aNode = (Node) listIterator.next();
			hostnameModel.addRow(
					new Object[]
					 {
					 	aNode.ip,
					 	aNode.hostname
					 });
		}
	}
}

/*
 * main class
 */
package com.javamix.vnms.vnms;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.Timer;

import net.sourceforge.jpcap.capture.PacketCapture;

public class VirtualWatch extends JPanel implements ActionListener {
	
	private static final String VERSION = "FireWall Monitoring Tool";

	// Menu
	private static JMenuBar menuBar;
	private JMenu infoMenu;
	private JMenuItem aboutItem;

	// GUI components
	private JTabbedPane tabbedPane;
	public JStatusBar statusBar;

	public Traffic trafficPanel;
	public PlainText plaintextPanel;
	public Statistics statisticsPanel;
	
	// control buttons
	public JToolBar toolBar;
	public JComboBox deviceCombo;
	public JButton rearrangeButton;
	public JButton startButton;
	public JButton stopButton;

	// GUI parameters
	final private static int WIDTH = 800;
	final private static int HEIGHT = 500;

	public boolean captureThreadisStopped;

	public VirtualWatch() {

		menuBar = new JMenuBar();
		infoMenu = new JMenu("Info");
		infoMenu.setMnemonic(KeyEvent.VK_I);
		aboutItem = new JMenuItem("About", KeyEvent.VK_A);
		aboutItem.setActionCommand("about");
		aboutItem.addActionListener(this);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(infoMenu);
		infoMenu.add(aboutItem);

		tabbedPane = new JTabbedPane();
		statusBar = new JStatusBar();
		statusBar.setStatus("FireWall Monitoring Tool started successfully");

		plaintextPanel = new PlainText(this);
		trafficPanel = new Traffic(this, WIDTH, HEIGHT);
		statisticsPanel = new Statistics(this);

		tabbedPane.addTab("Traffic", trafficPanel);
		tabbedPane.addTab("Plain Text", plaintextPanel);
		tabbedPane.addTab("Statistics", statisticsPanel);
		tabbedPane.setSelectedIndex(0);

	    setLayout(new BorderLayout()); //unnecessary
		
		toolBar = new JToolBar();		
		addToolBarButtons();
		add(toolBar, BorderLayout.NORTH);
		add(statusBar,	BorderLayout.SOUTH);
		add(tabbedPane,	BorderLayout.CENTER);
	}
	
	void addToolBarButtons()
	{
		JLabel deviceLabel = new JLabel("Device :");
		String[] devices = null;
		try {
			devices = PacketCapture.lookupDevices();
		} catch (Exception e) {
			e.printStackTrace();
		}
		deviceCombo = new JComboBox(devices);
		
		if (deviceCombo.getItemCount() == 0)
		{
			JOptionPane.showMessageDialog(null, "<html>No device found, make sure this program is run as <i>root</i></html>", "Warning", JOptionPane.WARNING_MESSAGE);
		}
		
		toolBar.add(deviceLabel);
		toolBar.add(deviceCombo);
		
		//start button
		ImageIcon icon = createImageIcon("images/Play24.gif", "");
		startButton = new JButton(icon);
		startButton.setToolTipText("Start monitoring");
		startButton.setActionCommand("start");
		startButton.addActionListener(this);
		toolBar.add(startButton);

		//stop button
		icon = createImageIcon("images/Stop24.gif", "");
		stopButton = new JButton(icon);
		stopButton.setToolTipText("Stop monitoring");
		stopButton.setEnabled(false);
		stopButton.setActionCommand("stop");
		stopButton.addActionListener(this);
		toolBar.add(stopButton);

		//rearrange button
		icon = createImageIcon("images/Refresh24.gif", "");
		rearrangeButton = new JButton(icon);
		rearrangeButton.setToolTipText("Re-arrange computers");
		rearrangeButton.setActionCommand("rearrange");
		rearrangeButton.addActionListener(trafficPanel);
		toolBar.add(rearrangeButton);

		rearrangeButton.addActionListener(this);
		
	}

	public static void main(String[] args) throws java.io.IOException {

		JFrame VirtualWatch = new JFrame(VERSION);
		VirtualWatch.setIconImage(Toolkit.getDefaultToolkit().getImage("images/logo.jpg"));
		VirtualWatch.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		VirtualWatch.getContentPane().add(new VirtualWatch(), BorderLayout.CENTER);
		VirtualWatch.setSize(WIDTH, HEIGHT);
		VirtualWatch.setJMenuBar(menuBar);
		VirtualWatch.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if ("start".equals(e.getActionCommand())) {
			captureThreadisStopped = false;
			run(); // change of button states occurs in CaptureThread
			trafficPanel.repaint();
		} else if ("stop".equals(e.getActionCommand())) {
			captureThreadisStopped = true;
			// change of button states occurs here because thread cannot be
			// terminated immediately
			stopButton.setEnabled(false);
			startButton.setEnabled(true);

			int colonIndex = statusBar.getStatus().indexOf(':');
			String packetCount =
				statusBar.getStatus().substring(colonIndex + 2);
			statusBar.setStatus("Finished: " + packetCount);
		} else if ("about".equals(e.getActionCommand())) {
			JOptionPane.showMessageDialog(
				null,
				"Thank you for using "+VERSION+"\nhttp://www.wellytechnologies.com",
				VERSION,
				JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void run() {
		new CaptureThread(this).start();

		Timer timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				trafficPanel.repaint();
				statisticsPanel.refreshPacketStatistics();
				statisticsPanel.refreshConnectionStatistics();
				statisticsPanel.refreshHostname();
			}
		});
		timer.start();
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
}

 
/*
 * This class is responsible for the panel of Plane Text
 */
package com.javamix.vnms.vnms;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import net.sourceforge.jpcap.net.IPPacket;
import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.util.AsciiHelper;

public class PlainText extends JPanel implements ActionListener {

	private VirtualWatch vpn;
	
	private static JTable table;
	private static JScrollPane scrollPane;
	private DefaultTableModel model;
	public DefaultTableModel savedModel = null;
	
	private static JTextField filterText;
	private static JButton filterButton;
	private static JButton clearFilterButton;
	private static JButton exportButton;
	
	private String[] words;
	private int wordCount;
	
	public PlainText(VirtualWatch vpn) {
		this.vpn = vpn;
		
		model = new DefaultTableModel();
		table = new JTable(model);

		// Create a couple of columns
		model.addColumn("ID");
		model.addColumn("Source");
		model.addColumn("Destination");
		model.addColumn("Text");

		/* allow a text to span multiple lines */
		table.getColumnModel().getColumn(3).setCellRenderer(
			new MultiLineCellRenderer());

		//Create the scroll pane and add the table to it. 
		scrollPane = new JScrollPane(table);

		setColumnWidth();
		
		filterButton = new JButton("Set filter");
		filterButton.setActionCommand("filter");
		filterButton.addActionListener(this);
		
		clearFilterButton = new JButton("Clear filter");
		clearFilterButton.setActionCommand("clear");
		clearFilterButton.addActionListener(this);
		
		exportButton = new JButton("Export text to file");
		exportButton.setActionCommand("export");
		exportButton.addActionListener(this);
		

		filterText = new JTextField();

		// add button to a panel, make it align to the left
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		buttonPane.add(filterText);
		buttonPane.add(filterButton);
		buttonPane.add(clearFilterButton);
		buttonPane.add(exportButton);

		// add everything to this panel
		setLayout(new BorderLayout());
		add(buttonPane, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		
		wordCount = 0;
	}

	/* main function that receives a packet and dump the text to the screen */
	public void DumpAddresses(Packet packet) {
		IPPacket ipPacket = (IPPacket) packet;
		//String aString = Integer.toString(ipPacket.getLength()) + ", " 
		//                         + Integer.toString(ipPacket.getHeaderLength());
		
		String data = AsciiHelper.toString(packet.getData());
		
		boolean hasAword = false;
		if (wordCount == 0) { // if the field is empty, means no filter
			hasAword = true;
		} else {
			for (int wordIndex = 0; wordIndex < wordCount; wordIndex++) {
				if (data.indexOf(words[wordIndex]) != -1) {
					hasAword = true;
				}
			}
		}
		
		// add the row only if it has a word
		if (hasAword) {
			model.insertRow(
				0,
				new Object[] {
					Integer.toString(model.getRowCount() + 1),
					ipPacket.getSourceAddress(),
					ipPacket.getDestinationAddress(),
					data });
		}
	}

	public void actionPerformed(ActionEvent e) {
		if ("filter".equals(e.getActionCommand())) {
			runFilter();
		} else if ("clear".equals(e.getActionCommand())) {
			filterText.setText("");
			wordCount = 0;
		} else if ("export".equals(e.getActionCommand())) {
			exportToFile();
		}
	}

//	public void getNewModel() {
//		// set up the newModel
//		model = new DefaultTableModel();
//		model.addColumn("ID");
//		model.addColumn("Source");
//		model.addColumn("Destination");
//		model.addColumn("Text");
//		table.setModel(model);
//
//		table.getColumnModel().getColumn(3).setCellRenderer(
//			new MultiLineCellRenderer());
//
//		setColumnWidth();
//	}
	private void exportToFile() {
		JFileChooser fc = new JFileChooser();
		fc.showSaveDialog(vpn);
		File file = fc.getSelectedFile();

		if (file != null) {

			try {
				// if file does not exist
				// and cannot create new file
				if ((!file.exists()) && (!file.createNewFile())) {
					JOptionPane.showMessageDialog(
						vpn,
						"Error creating file",
						"Error",
						JOptionPane.OK_OPTION);
					return;
				} else if ((file.exists()) && (!file.canWrite())) {
					// if file exists but cannot write to file
					JOptionPane.showMessageDialog(
						vpn,
						"Error writing to file",
						"Error",
						JOptionPane.OK_OPTION);
					return;
				} else {
					// ok to write to file
					FileOutputStream out; // declare a file output object 
					PrintStream p; // declare a print stream object
					try {
						// Create a new file output stream 
						// connected to the file 
						out = new FileOutputStream(file.getAbsolutePath());

						// Connect print stream to the output stream 
						p = new PrintStream(out);
						for (int i = 0; i < model.getRowCount(); i++) {
							String ips =
								(String) model.getValueAt(i, 1)
									+ "->"
									+ (String) model.getValueAt(i, 2);
							String text = (String) model.getValueAt(i, 3);
							p.println(ips);
							p.println(text);
						}
						p.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void runFilter() {
		words = new String[100];
		wordCount = 0;

		// remove trailing space and ,,,,,
		String filterWords = filterText.getText().trim();
		if (filterWords.length() > 0) {

			while (filterWords
				.substring(filterWords.length() - 1, filterWords.length())
				.compareTo(",")
				== 0) {
				filterWords =
					filterWords.substring(0, filterWords.length() - 1).trim();
			}
		}

		int oldIndex = -1;
		int newIndex = -1;

		// get all words into words[]
		boolean repeat = true;
		while (repeat) {
			newIndex = filterWords.indexOf(",", oldIndex + 1);
			if (newIndex != -1) {
				String newWord =
					((filterWords.substring((oldIndex + 1), newIndex))).trim();
				if (newWord.length() != 0) {
					words[wordCount] = newWord;
					wordCount++;
				}
				oldIndex = newIndex;
			} else {
				String newWord =
					(filterWords
						.substring((oldIndex + 1), filterWords.length()))
						.trim();
				if (newWord.length() != 0) {
					words[wordCount] = newWord;
					wordCount++;
					repeat = false;
				}
			}
		}
	}


	private void setColumnWidth() {
		//			set column width
		TableColumn column = null;
		for (int i = 0; i < 4; i++) {
			column = table.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(10);
			} else if (i == 3) {
				column.setPreferredWidth(500);
			} else {
				column.setPreferredWidth(100);
			}
		}
	}
}

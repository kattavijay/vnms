/*

 * This thread makes up the status bar
 */
package com.javamix.vnms.vnms;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class JStatusBar extends JPanel {
	JLabel text = new JLabel();
	public JStatusBar() {
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.add(text);
	}

	public void setStatus(String status) {
		text.setText(status);
	}

	public String getStatus() {
		return text.getText();
	}

}

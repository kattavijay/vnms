/* 
 * This class is grabbed off the Internet
 * used to allow plain text span multiple lines in the table
 */
package com.javamix.vnms.vnms;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;



public class MultiLineCellRenderer extends
            DefaultTableCellRenderer
{

    public MultiLineCellRenderer ()
    {
        this.myText = new JTextArea();
        this.myText.setLineWrap(true);
        this.myText.setWrapStyleWord(true);
    }

    public Component
    getTableCellRendererComponent ( JTable table,
                                    Object value,
                                    boolean isSelected,
                                    boolean hasFocus,
                                    int row,
                                    int col )
    {
        JPanel panel = new JPanel ();

        panel.setLayout (new GridBagLayout());
        panel.add (this.myText,
                   new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,

                                          GridBagConstraints.CENTER,
                                          GridBagConstraints.BOTH,
                                          new Insets(0, 0, 0, 0), 0,
                                          0));

        this.myText.setText(value==null?"":(String)value);

        int tableRowHeight = table.getRowHeight(row);

        // if the current height is different, resize the row height
        if ( (tableRowHeight < panel.getPreferredSize().getHeight
                () ) )
        {
            table.setRowHeight(row, (int)(panel.getPreferredSize
                                          ().getHeight()));
        }
        return panel;
    }

    //===========================================
    // Attributes
    //===========================================
    JTextArea myText;

} // end class MultiLineCellRenderer

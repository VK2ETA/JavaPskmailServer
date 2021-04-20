/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javapskmail;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author jdouyere
 */

public class RMsgTableRenderer extends JTextArea implements TableCellRenderer
{
    RMsgTableRenderer()
    {
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    Border paddingSent = BorderFactory.createEmptyBorder(0, 150, 0, 0);
    Border paddingReceived = BorderFactory.createEmptyBorder(0, 0, 0, 0);
    Color badCrcColor = new Color(255, 116, 72);
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (value != null) {
            RMsgDisplayItem mItem = (RMsgDisplayItem) value;
            setText((mItem == null) ? "" : mItem.mMessage.formatForList(false));//No tracking for now
            setSize(table.getColumnModel().getColumn(column).getWidth(), table.getRowHeight(row));
            //  Recalculate the preferred height now that the text and renderer width have been set.
            int preferredHeight = getPreferredSize().height;
            if (table.getRowHeight(row) != preferredHeight) {
                table.setRowHeight(row, preferredHeight);
            }
            setBorder(mItem.myOwn ? paddingSent : paddingReceived);
            if (isSelected) {
                this.setBackground(table.getSelectionBackground());
            } else {
                if (mItem.mMessage.crcValid || mItem.mMessage.crcValidWithPW) {
                    this.setBackground(table.getBackground());
                } else {
                    this.setBackground(badCrcColor);
                }
            }
        }
        return this;
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javapskmail;

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

    Border paddingMyOwn = BorderFactory.createEmptyBorder(0, 400, 0, 0);
    Border paddingReceived = BorderFactory.createEmptyBorder(0, 200, 0, 0);
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        RMsgDisplayItem mItem = (RMsgDisplayItem)value;
        setText( (mItem == null) ? "" : mItem.mMessage.formatForList(false));//No tracking for now
        setSize(table.getColumnModel().getColumn(column).getWidth(), table.getRowHeight(row));
        //  Recalculate the preferred height now that the text and renderer width have been set.
        int preferredHeight = getPreferredSize().height;
        if (table.getRowHeight(row) != preferredHeight)
        {
            table.setRowHeight(row, preferredHeight);
        }
        setBorder(mItem.myOwn ? paddingMyOwn : paddingReceived);
        return this;
    }
}
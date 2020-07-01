/**
 * Copyright (c) 2020 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 *
 * We have chosen to apply the GNU General Public License (GPL) Version 3 (https://www.gnu.org/licenses/gpl-3.0.html)
 * to the copyrightable parts of e!DAL, which are the source code, the executable software, the training and
 * documentation material. This means, you must give appropriate credit, provide a link to the license, and indicate
 * if changes were made. You are free to copy and redistribute e!DAL in any medium or format. You are also free to
 * adapt, remix, transform, and build upon e!DAL for any purpose, even commercially.
 *
 *  Contributors:
 *       Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany
 */
package de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class PushableTableHeader extends JPanel {

	public PushableTableHeader(TableColumn column, JTableHeader header) {
		setLayout(new BorderLayout());
		ButtonHeaderRenderer renderer = new ButtonHeaderRenderer();
		column.setHeaderRenderer(renderer);
		header.addMouseListener(new HeaderListener(header, renderer));
	}

	public class HeaderListener extends MouseAdapter {
		JTableHeader header;
		ButtonHeaderRenderer renderer;

		HeaderListener(JTableHeader header, ButtonHeaderRenderer renderer) {
			this.header = header;

			this.renderer = renderer;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			int col = header.columnAtPoint(e.getPoint());
			renderer.setPressedColumn(col);
			header.repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// int col = header.columnAtPoint(e.getPoint());
			renderer.setPressedColumn(-1);
			header.repaint();
		}

		@Override
		public void mouseClicked(MouseEvent e) {

			int col = header.getTable().columnAtPoint(e.getPoint());
			String name = header.getTable().getColumnName(col);
//			System.out.println("Column index selected " + col + " " + name);

			if (col == 1 && name.equals("ORCID")) {
				EdalHelpers.openURL("https://orcid.org");
			}

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			int col = header.getTable().columnAtPoint(e.getPoint());
			String name = header.getTable().getColumnName(col);
//			System.out.println("Column index selected " + col + " " + name);

			if (col == 1 && name.equals("ORCID")) {
				header.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
		}

	}

	private class ButtonHeaderRenderer extends JButton implements TableCellRenderer {

		private static final long serialVersionUID = 8797975701378444045L;
		int pushedColumn;

		public ButtonHeaderRenderer() {
			pushedColumn = -1;
			setMargin(new Insets(0, 0, 0, 0));
			setToolTipText("Please follow the link to learn more about ORCID");
//			addMouseListener(new MouseAdapter() {
//				public void mouseEntered(MouseEvent me) {
//					System.out.println("HERE");
//					setCursor(handCursor);
//				}
//
//				public void mouseExited(MouseEvent me) {
//					System.out.println("HERE 2");
//					setCursor(defaultCursor);
//				}
//			});
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			setText((value == null) ? "" : value.toString());
			setForeground(Color.BLUE);
			boolean isPressed = (column == pushedColumn);
			getModel().setPressed(isPressed);
			getModel().setArmed(isPressed);
			return this;
		}

		public void setPressedColumn(int col) {
			pushedColumn = col;
		}

	}

	public static void main(String[] args) {

	}
}

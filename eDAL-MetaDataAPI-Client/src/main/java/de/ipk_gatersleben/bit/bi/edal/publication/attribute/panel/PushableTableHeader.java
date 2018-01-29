/**
 * Copyright (c) 2018 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * which accompanies this distribution, and is available at http://creativecommons.org/licenses/by-nd/4.0/
 *
 * Contributors:
 *      Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany - RMI Client, FileChooser, PublicationTool
 */
package de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import de.ipk_gatersleben.bit.bi.edal.publication.Utils;

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
				Utils.openURL("https://orcid.org");
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

/**
 * Copyright (c) 2023 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Calendar;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDatePrecision;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.Const;

public class EdalDateChooser extends JDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Return value if cancel is chosen.
	 */
	public static final int CANCEL_OPTION = 1;
	/**
	 * Return value if approve (yes, ok) is chosen.
	 */
	public static final int APPROVE_OPTION = 0;

	public int returnvalue;

	private int width = 750;
	private int height = 280;

	private Color backGroundColor = Color.gray;

	private EdalDatePanel edalDatePanel;

	public EdalDateChooser(EdalDate edaldate, boolean showprecision) {
		addWindowListener(createAppCloser());
		setTitle("DatetimePicker");

		setLayout(new BorderLayout());
		setBackground(backGroundColor);

		if (showprecision) {

		} else {
			height = height - 80;
		}

		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		JButton savebtn = new JButton(okAction);
		JButton cancelbtn = new JButton(cancelAction);
		buttonPane.add(savebtn);
		buttonPane.add(cancelbtn);

		edalDatePanel = new EdalDatePanel(edaldate, showprecision);
		add(edalDatePanel, BorderLayout.CENTER);
		add(buttonPane, BorderLayout.SOUTH);

		this.setSize(width, height);
	}

	public int showDateChooser() {
		setModal(true);
		setLocationRelativeTo(null);
		setVisible(true);
		return returnvalue;
	}

	public void reset() {
		edalDatePanel.reset();
	}

	public Calendar getCalendar() {
		return edalDatePanel.getCalendar();
	}

	public EdalDatePrecision getPrecision() {
		return edalDatePanel.getPrecision();
	}

	private Action okAction = new AbstractAction(Const.OK_BTN_STR) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			edalDatePanel.refreshtime();
			returnvalue = APPROVE_OPTION;
			setVisible(false);
		}
	};

	private Action cancelAction = new AbstractAction(Const.CANCEL_BTN_STR) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			returnvalue = CANCEL_OPTION;
			setVisible(false);
		}
	};

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	private WindowListener createAppCloser() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent we) {
				returnvalue = CANCEL_OPTION;
			}
		};
	}
}

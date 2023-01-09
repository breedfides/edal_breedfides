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
package de.ipk_gatersleben.bit.bi.edal.publication;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDatePrecision;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.EdalDateChooser;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.EdalDateFormat;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.ImageUtil;

/**
 * <code>DatetimePicker</code> provides a swing panel for the user to choose
 * Datetime.
 * 
 * @version 2.0
 * @author chenj
 * @author arendd
 * 
 */
public class DateTimePicker extends JPanel {
	private static final long serialVersionUID = 1L;
	private EdalDateChooser dateChooser = null;
	
	private JTextField text;
	private JLabel label;

	/**
	 * Constructs a <code>DatetimePicker</code> that is initialized with
	 * <code>edaldate</code>. If the parameter are <code>null</code> this method
	 * will initialize the DatetimePicker with current time
	 * 
	 * @param edaldate
	 *            EDALDate object to show in DatetimePicker panel
	 */

	public DateTimePicker(final EdalDate edaldate) {
		setBorder(null);
		setCursor(new Cursor(Cursor.HAND_CURSOR));

		GridBagLayout gridBagLayout = new GridBagLayout();

		setLayout(gridBagLayout);

		text = new JTextField();
		text.setHorizontalAlignment(JTextField.CENTER);
		text.setEditable(false);

		ImageIcon icon = ImageUtil.createImageIcon("dateIcon.gif",
				"choose new datetime");
		label = new JLabel(icon);
		label.setToolTipText("choose new datetime");

		addComponent(this, gridBagLayout, text, 0, 0, 1, 1, 0.95, 1, 1, 1);
		addComponent(this, gridBagLayout, label, 1, 0, 1, 1, 0.05, 1, 1, 1);

		label.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (dateChooser == null) {
					dateChooser = new EdalDateChooser(edaldate,false);
				}
				int returnVal = dateChooser.showDateChooser();
				if (returnVal == EdalDateChooser.APPROVE_OPTION)
				{
					setDate(dateChooser.getCalendar().getTime());
				}
			}
		});
	}

	static void addComponent(Container cont, GridBagLayout gbl, Component c,
			int x, int y, int width, int height, double weightx,
			double weighty, int ipadx, int ipady) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.ipadx = ipadx;
		gbc.ipady = ipady;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbl.setConstraints(c, gbc);
		cont.add(c);
	}

	/**
	 * reset the time to current time
	 */
	public void reset() {
		if (dateChooser != null) {
			dateChooser.reset();
		}
		text.setText("");
	}

	/**
	 * Returns the Calendar, which is selected by user.
	 * 
	 * @return the Calendar, which is selected by user.
	 */
	public Calendar getCalendar() {
		if (dateChooser != null) {
			return dateChooser.getCalendar();
		}
		return null;
	}

	/**
	 * Returns the EDALDatePrecision, which is selected by user.
	 * 
	 * @return the EDALDatePrecision, which is selected by user.
	 */
	public EdalDatePrecision getPrecision() {
		if (dateChooser != null) {
			return dateChooser.getPrecision();
		}
		return null;
	}

	private void setDate(Date date) {
		text.setText(EdalDateFormat.getDefaultDateFormat(getPrecision()).format(date));
	}
}

/**
 * Copyright (c) 2022 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDatePrecision;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.EdalDateFormat;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.ImageUtil;

/**
 * <code>DatetimePicker</code> provides a swing panel for the user to choose
 * Datetime and set it's Precision.
 * 
 * The following codes show how to user it:
 * 
 * <pre>
 * DatetimePicker starttimepicker = new DatetimePicker(null);
 * Calendar startcalendar = starttimepicker.getCalendar();
 * EDALDatePrecision starttimeprecision = starttimepicker.getPrecision();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 * 
 */
public class EdalDateTimePicker extends JPanel {
	private static final long serialVersionUID = 1L;
	private EdalDateChooser dateChooser = null;
	
	private JTextField text;
	private JLabel label;


	/**
	 * Constructs a <code>DatetimePicker</code> that is initialized with
	 * <code>edaldate</code>. If the parameter are <code>null</code> this method
	 * will initialize the DatetimePicker with current time and
	 * EDALDatePrecision.SECOND as default Precision.
	 * 
	 * @param edaldate
	 *            EDALDate object to show in DatetimePicker panel
	 */

	public EdalDateTimePicker(final EdalDate edaldate) {
		setBorder(null);
		setCursor(new Cursor(Cursor.HAND_CURSOR));

		setLayout(new MigLayout("", "[20%!][20%!][20%!][20%!]1[20%!]", ""));
		text = new JTextField();
		text.setHorizontalAlignment(JTextField.CENTER);
		text.setEditable(true);
		add(text, "cell 0 0 4 1,width max(80%, 80%)");

		ImageIcon icon = ImageUtil.createImageIcon("dateIcon.gif","choose new datetime");
		label = new JLabel(icon);
		label.setToolTipText("choose new datetime");
		add(label, "cell 4 0 1 1");

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

	/**
	 * reset the time to current time and EDALDatePrecision.SECOND as current
	 * Precision
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

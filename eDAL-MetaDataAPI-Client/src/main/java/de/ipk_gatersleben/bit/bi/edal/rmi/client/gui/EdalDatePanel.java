/**
 * Copyright (c) 2021 Leibniz Institute of Plant Genetics and Crop Plant Research (IPK), Gatersleben, Germany.
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDatePrecision;

public class EdalDatePanel extends JPanel implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;

	private int startYear = 1;
	private int lastYear = 5000;

	private static final Font FONT = new Font("Courier New", Font.PLAIN, 10);

	private Color palletTableColor = Color.white;
	private Color todayBackColor = Color.orange;
	private Color weekFontColor = Color.blue;
	private Color dateFontColor = Color.black;
	private Color weekendFontColor = Color.red;
	private Color controlLineColor = Color.pink;
	private Color controlTextColor = Color.white;

	private static final String CENTURY = "Century";
	private static final String DECADE = "Decade";
	private static final String YEAR = "Year";
	private static final String MONTH = "Month";
	private static final String DAY = "Day";
	private static final String HOUR = "Hour";
	private static final String MINUTE = "Minute";
	private static final String SECOND = "Second";
	private static final String MILLISECOND = "Millisecond";

	private JSpinner yearSpin;
	private JSpinner monthSpin;
	private JSpinner daySpin;
	private JSpinner hourSpin;
	private JSpinner minuteSpin;
	private JSpinner secondSpin;
	private JSpinner millisecondSpin;

	private JButton[][] daysButton = new JButton[6][7];

	private EdalDatePrecision showprecision;
	private Calendar showcalendar;

	public EdalDatePanel(EdalDate edaldate, boolean showprecision) {
		if (edaldate != null) {
			this.showcalendar = edaldate.getStartDate();
			this.showprecision = edaldate.getStartPrecision();
		} else {
			this.showcalendar = Calendar.getInstance();
			this.showprecision = EdalDatePrecision.SECOND;
		}
		setLayout(new BorderLayout());

		if (showprecision) {
			JPanel toppanel = createprecisionPanel();
			add(toppanel, BorderLayout.NORTH);
			JPanel controlpanel = createYearAndMonthPanel();
			add(controlpanel, BorderLayout.CENTER);
			JPanel displaypanel = createWeekAndDayPanel();
			add(displaypanel, BorderLayout.SOUTH);
		} else {
			JPanel controlpanel = createYearAndMonthPanel();
			add(controlpanel, BorderLayout.NORTH);
			JPanel displaypanel = createWeekAndDayPanel();
			add(displaypanel, BorderLayout.CENTER);
		}

		flushWeekAndDay();
	}

	private JPanel createprecisionPanel() {
		JPanel result = new JPanel();
		result.setLayout(new FlowLayout());

		ButtonGroup g = new ButtonGroup();

		final JRadioButton centuryrb = new JRadioButton(CENTURY);
		if (showprecision != null && showprecision.equals(EdalDatePrecision.CENTURY)) {
			centuryrb.setSelected(true);
		}
		centuryrb.addActionListener(this);

		g.add(centuryrb);
		result.add(centuryrb);

		JRadioButton decaderb = new JRadioButton(DECADE);
		if (showprecision != null && showprecision.equals(EdalDatePrecision.DECADE)) {
			decaderb.setSelected(true);
		}
		decaderb.addActionListener(this);
		g.add(decaderb);
		result.add(decaderb);

		JRadioButton yearrb = new JRadioButton(YEAR);
		if (showprecision != null && showprecision.equals(EdalDatePrecision.YEAR)) {
			yearrb.setSelected(true);
		}
		yearrb.addActionListener(this);
		g.add(yearrb);
		result.add(yearrb);

		JRadioButton monthrb = new JRadioButton(MONTH);
		if (showprecision != null && showprecision.equals(EdalDatePrecision.MONTH)) {
			monthrb.setSelected(true);
		}
		monthrb.addActionListener(this);
		g.add(monthrb);
		result.add(monthrb);

		JRadioButton dayrb = new JRadioButton(DAY);
		if (showprecision != null && showprecision.equals(EdalDatePrecision.DAY)) {
			dayrb.setSelected(true);
		}
		dayrb.addActionListener(this);
		g.add(dayrb);
		result.add(dayrb);

		JRadioButton hourrb = new JRadioButton(HOUR);
		if (showprecision != null && showprecision.equals(EdalDatePrecision.HOUR)) {
			hourrb.setSelected(true);
		}
		hourrb.addActionListener(this);
		g.add(hourrb);
		result.add(hourrb);

		JRadioButton minuterb = new JRadioButton(MINUTE);
		if (showprecision != null && showprecision.equals(EdalDatePrecision.MINUTE)) {
			minuterb.setSelected(true);
		}
		minuterb.addActionListener(this);
		g.add(minuterb);
		result.add(minuterb);

		JRadioButton secondrb = new JRadioButton(SECOND);
		if (showprecision != null && showprecision.equals(EdalDatePrecision.SECOND)) {
			secondrb.setSelected(true);
		}
		secondrb.addActionListener(this);
		g.add(secondrb);
		result.add(secondrb);

		JRadioButton millisecondrb = new JRadioButton(MILLISECOND);
		if (showprecision != null && showprecision.equals(EdalDatePrecision.MILLISECOND)) {
			millisecondrb.setSelected(true);
		}
		millisecondrb.addActionListener(this);
		g.add(millisecondrb);
		result.add(millisecondrb);

		if (showprecision == null) {
			secondrb.setSelected(true);
		}

		return result;
	}

	private JPanel createYearAndMonthPanel() {
		Calendar c = getCalendar();
		int currentYear = c.get(Calendar.YEAR);
		int currentMonth = c.get(Calendar.MONTH) + 1;
		int currentday = c.get(Calendar.DAY_OF_MONTH);
		int currentHour = c.get(Calendar.HOUR_OF_DAY);
		int currentMinute = c.get(Calendar.MINUTE);
		int currentSecond = c.get(Calendar.SECOND);
		int currentMillisecond = c.get(Calendar.MILLISECOND);

		JPanel result = new JPanel();
		result.setLayout(new FlowLayout());
		result.setBackground(controlLineColor);

		JLabel yearLabel = new JLabel(YEAR);
		yearLabel.setForeground(controlTextColor);
		result.add(yearLabel);

		yearSpin = new JSpinner(new SpinnerNumberModel(currentYear, startYear, lastYear, 1));
		yearSpin.setPreferredSize(new Dimension(60, 20));
		yearSpin.setName(YEAR);
		yearSpin.setEditor(new JSpinner.NumberEditor(yearSpin, "####"));
		yearSpin.addChangeListener(this);
		result.add(yearSpin);

		JLabel monthLabel = new JLabel(MONTH);
		monthLabel.setForeground(controlTextColor);
		result.add(monthLabel);

		monthSpin = new JSpinner(new SpinnerNumberModel(currentMonth, 1, 12, 1));
		monthSpin.setPreferredSize(new Dimension(60, 20));
		monthSpin.setName(MONTH);
		monthSpin.addChangeListener(this);
		result.add(monthSpin);

		JLabel dayLabel = new JLabel(DAY);
		dayLabel.setForeground(controlTextColor);
		result.add(dayLabel);

		daySpin = new JSpinner(getdaymodel(currentYear, currentMonth, currentday));
		daySpin.setPreferredSize(new Dimension(60, 20));
		daySpin.setName(DAY);
		daySpin.addChangeListener(this);
		result.add(daySpin);

		JLabel hourLabel = new JLabel(HOUR);
		hourLabel.setForeground(controlTextColor);
		result.add(hourLabel);

		hourSpin = new JSpinner(new SpinnerNumberModel(currentHour, 0, 23, 1));
		hourSpin.setPreferredSize(new Dimension(60, 20));
		hourSpin.setName(HOUR);
		hourSpin.addChangeListener(this);
		result.add(hourSpin);

		JLabel minuteLabel = new JLabel(MINUTE);
		minuteLabel.setForeground(controlTextColor);
		result.add(minuteLabel);

		minuteSpin = new JSpinner(new SpinnerNumberModel(currentMinute, 0, 59, 1));
		minuteSpin.setPreferredSize(new Dimension(60, 20));
		minuteSpin.setName(MINUTE);
		minuteSpin.addChangeListener(this);
		result.add(minuteSpin);

		JLabel secondLabel = new JLabel(SECOND);
		secondLabel.setForeground(controlTextColor);
		result.add(secondLabel);

		secondSpin = new JSpinner(new SpinnerNumberModel(currentSecond, 0, 59, 1));
		secondSpin.setPreferredSize(new Dimension(60, 20));
		secondSpin.setName(SECOND);
		secondSpin.addChangeListener(this);
		result.add(secondSpin);

		JLabel millisecondLabel = new JLabel(MILLISECOND);
		millisecondLabel.setForeground(controlTextColor);
		result.add(millisecondLabel);

		millisecondSpin = new JSpinner(new SpinnerNumberModel(currentMillisecond, 0, 999, 1));
		millisecondSpin.setPreferredSize(new Dimension(60, 20));
		millisecondSpin.setName(MILLISECOND);
		millisecondSpin.addChangeListener(this);
		result.add(millisecondSpin);

		return result;
	}

	private JPanel createWeekAndDayPanel() {
		String colname[] = { "Sunday", "Monday", "Tuesday ", "Wednesday", "Thursday", "Friday", "Saturday " };
		JPanel result = new JPanel();

		result.setFont(FONT);
		result.setLayout(new GridLayout(7, 7));
		result.setBackground(Color.white);
		JLabel cell;

		for (int i = 0; i < 7; i++) {
			cell = new JLabel(colname[i]);
			cell.setHorizontalAlignment(JLabel.CENTER);
			if (i == 0 || i == 6)
				cell.setForeground(weekendFontColor);
			else
				cell.setForeground(weekFontColor);
			result.add(cell);
		}

		int actionCommandId = 0;
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 7; j++) {
				JButton numberButton = new JButton();
				numberButton.setBorder(null);
				numberButton.setHorizontalAlignment(SwingConstants.CENTER);
				numberButton.setActionCommand(String.valueOf(actionCommandId));
				numberButton.addActionListener(this);
				numberButton.setBackground(palletTableColor);
				numberButton.setForeground(dateFontColor);
				if (j == 0 || j == 6)
					numberButton.setForeground(weekendFontColor);
				else
					numberButton.setForeground(dateFontColor);

				daysButton[i][j] = numberButton;
				result.add(numberButton);
				actionCommandId++;
			}

		return result;
	}

	public Calendar getCalendar() {
		if (showcalendar == null) {
			showcalendar = Calendar.getInstance();
		}
		showcalendar.setTime(getDate());
		return showcalendar;
	}

	public EdalDatePrecision getPrecision() {
		return showprecision;
	}

	public void reset() {
		this.showcalendar = Calendar.getInstance();
		this.showprecision = EdalDatePrecision.SECOND;
	}

	private Date getDate() {
		return this.showcalendar.getTime();
	}

	private SpinnerNumberModel getdaymodel(int currentYear, int currentMonth, int currentday) {
		int minday = 1;
		int maxday = 30;
		if (currentMonth == 1 || currentMonth == 3 || currentMonth == 5 || currentMonth == 7 || currentMonth == 8 || currentMonth == 10 || currentMonth == 12) {
			maxday = 31;
		} else if (currentMonth == 2) {
			if (isleapyear(currentYear)) {
				maxday = 29;
			} else {
				maxday = 28;
			}
		}
		return new SpinnerNumberModel(currentday, minday, maxday, 1);
	}

	private boolean isleapyear(int year) {
		if (year % 100 == 0) {
			if (year % 400 == 0) {
				return true;
			}
		} else {
			if (year % 4 == 0) {
				return true;
			}
		}
		return false;
	}

	private int getSelectedYear() {
		return ((Integer) yearSpin.getValue()).intValue();
	}

	private int getSelectedMonth() {
		return ((Integer) monthSpin.getValue()).intValue();
	}

	private int getSelectedDay() {
		return ((Integer) daySpin.getValue()).intValue();
	}

	private int getSelectedHour() {
		return ((Integer) hourSpin.getValue()).intValue();
	}

	private int getSelectedMinite() {
		return ((Integer) minuteSpin.getValue()).intValue();
	}

	private int getSelectedSecond() {
		return ((Integer) secondSpin.getValue()).intValue();
	}

	private int getSelectedMillisecond() {
		return ((Integer) millisecondSpin.getValue()).intValue();
	}

	private void refreshdayspin(int currentYear, int currentMonth, int currentday) {
		daySpin.setModel(getdaymodel(currentYear, currentMonth, currentday));
		daySpin.updateUI();
	}

	public void flushWeekAndDay() {
		Calendar c = getCalendar();
		int curday = c.get(Calendar.DAY_OF_MONTH);
		c.set(Calendar.DAY_OF_MONTH, 1);
		int maxDayNo = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		int dayNo = 2 - c.get(Calendar.DAY_OF_WEEK);
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				String s = "";
				if (dayNo >= 1 && dayNo <= maxDayNo)
					s = String.valueOf(dayNo);
				daysButton[i][j].setText(s);
				dayNo++;

				if (j == 0 || j == 6)
					daysButton[i][j].setForeground(weekendFontColor);
				else
					daysButton[i][j].setForeground(dateFontColor);
			}
		}
		c.set(Calendar.DAY_OF_MONTH, curday);
	}

	public void stateChanged(ChangeEvent e) {
		JSpinner source = (JSpinner) e.getSource();
		Calendar c = getCalendar();

		if (source.getName().equals("Day")) {
			c.set(Calendar.DAY_OF_MONTH, getSelectedDay());
			return;
		}

		if (source.getName().equals("Hour")) {
			c.set(Calendar.HOUR_OF_DAY, getSelectedHour());
			return;
		}
		if (source.getName().equals("Minute")) {
			c.set(Calendar.MINUTE, getSelectedMinite());
			return;
		}
		if (source.getName().equals("Second")) {
			c.set(Calendar.SECOND, getSelectedSecond());
			return;
		}

		if (source.getName().equals("Millisecond")) {
			c.set(Calendar.MILLISECOND, getSelectedMillisecond());
			return;
		}

		if (source.getName().equals("Year")) {
			c.set(Calendar.YEAR, getSelectedYear());
			refreshdayspin(getSelectedYear(), getSelectedMonth(), getSelectedDay());
		} else if (source.getName().equals("Month")) {
			c.set(Calendar.MONTH, getSelectedMonth() - 1);
			refreshdayspin(getSelectedYear(), getSelectedMonth(), getSelectedDay());
		}
		flushWeekAndDay();
	}

	private void changeSpinnerStatus(boolean enableYearSpinner, boolean enableMonthSpinner, boolean enableDaySpinner, boolean enableHourSpinner, boolean enableMinuteSpinner, boolean enableSecondSpinner, boolean enableMillisecondSpinner, boolean enableDisplayButtons) {
		yearSpin.setEnabled(enableYearSpinner);
		monthSpin.setEnabled(enableMonthSpinner);
		daySpin.setEnabled(enableDaySpinner);
		hourSpin.setEnabled(enableHourSpinner);
		minuteSpin.setEnabled(enableMinuteSpinner);
		secondSpin.setEnabled(enableSecondSpinner);
		millisecondSpin.setEnabled(enableMillisecondSpinner);
		enabledisplaybuttons(enableDaySpinner);

	}

	private void enabledisplaybuttons(boolean enable) {
		for (int i = 0; i < daysButton.length; i++) {
			for (int j = 0; j < daysButton[i].length; j++) {
				daysButton[i][j].setEnabled(enable);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		String classname = e.getSource().getClass().getName();
		if (classname.equals("javax.swing.JButton")) {
			JButton source = (JButton) e.getSource();
			if (source.getText().length() == 0)
				return;
			if (!source.isEnabled()) {
				return;
			}

			source.setForeground(todayBackColor);
			int newDay = Integer.parseInt(source.getText());
			Calendar c = getCalendar();
			c.set(Calendar.DAY_OF_MONTH, newDay);
			daySpin.setValue(Integer.valueOf(newDay));
		} else if (classname.equals("javax.swing.JRadioButton")) {
			JRadioButton source = (JRadioButton) e.getSource();
			if (source.isSelected()) {
				switch (source.getText()) {
				case CENTURY:
					showprecision = EdalDatePrecision.CENTURY;
					changeSpinnerStatus(false, false, false, false, false, false, false, false);
					break;
				case DECADE:
					showprecision = EdalDatePrecision.DECADE;
					changeSpinnerStatus(false, false, false, false, false, false, false, false);
					break;
				case YEAR:
					showprecision = EdalDatePrecision.YEAR;
					changeSpinnerStatus(true, false, false, false, false, false, false, false);
					break;

				case MONTH:
					showprecision = EdalDatePrecision.MONTH;
					changeSpinnerStatus(true, true, false, false, false, false, false, false);
					break;
				case DAY:
					showprecision = EdalDatePrecision.DAY;
					changeSpinnerStatus(true, true, true, false, false, false, false, true);
					break;
				case HOUR:
					showprecision = EdalDatePrecision.HOUR;
					changeSpinnerStatus(true, true, true, true, false, false, false, true);
					break;
				case MINUTE:
					showprecision = EdalDatePrecision.MINUTE;
					changeSpinnerStatus(true, true, true, true, true, false, false, true);
					break;
				case SECOND:
					showprecision = EdalDatePrecision.SECOND;
					changeSpinnerStatus(true, true, true, true, true, true, false, true);
					break;
				case MILLISECOND:
					showprecision = EdalDatePrecision.MILLISECOND;
					changeSpinnerStatus(true, true, true, true, true, true, true, true);
					break;
				default:
					break;
				}
			}
		}
	}

	private int formatyeartocentury(int year) {
		if (year < 100) {
			return 0;
		}
		int century = year / 100;
		return (century) * 100;
	}

	private int formatyeartodecade(int year) {
		if (year < 10) {
			return 0;
		}
		int century = year / 10;
		return (century) * 10;
	}

	public void refreshtime() {
		if (showcalendar == null) {
			showcalendar = getCalendar();
		}

		if (showprecision != null && showprecision.equals(EdalDatePrecision.CENTURY)) {
			showcalendar.set(Calendar.YEAR, formatyeartocentury(getSelectedYear()));
		} else if (showprecision != null && showprecision.equals(EdalDatePrecision.DECADE)) {
			showcalendar.set(Calendar.YEAR, formatyeartodecade(getSelectedYear()));
		} else if (showprecision != null && showprecision.equals(EdalDatePrecision.YEAR)) {
			showcalendar.set(Calendar.YEAR, getSelectedYear());
		} else if (showprecision != null && showprecision.equals(EdalDatePrecision.MONTH)) {
			showcalendar.set(Calendar.YEAR, getSelectedYear());
			showcalendar.set(Calendar.MONTH, getSelectedMonth() - 1);
		} else if (showprecision != null && showprecision.equals(EdalDatePrecision.DAY)) {
			showcalendar.set(Calendar.YEAR, getSelectedYear());
			showcalendar.set(Calendar.MONTH, getSelectedMonth() - 1);
			showcalendar.set(Calendar.DAY_OF_MONTH, getSelectedDay());
		} else if (showprecision != null && showprecision.equals(EdalDatePrecision.HOUR)) {
			showcalendar.set(Calendar.YEAR, getSelectedYear());
			showcalendar.set(Calendar.MONTH, getSelectedMonth() - 1);
			showcalendar.set(Calendar.DAY_OF_MONTH, getSelectedDay());
			showcalendar.set(Calendar.HOUR, getSelectedHour());
		} else if (showprecision != null && showprecision.equals(EdalDatePrecision.MINUTE)) {
			showcalendar.set(Calendar.YEAR, getSelectedYear());
			showcalendar.set(Calendar.MONTH, getSelectedMonth() - 1);
			showcalendar.set(Calendar.DAY_OF_MONTH, getSelectedDay());
			showcalendar.set(Calendar.HOUR_OF_DAY, getSelectedHour());
			showcalendar.set(Calendar.MINUTE, getSelectedMinite());
		} else if (showprecision != null && showprecision.equals(EdalDatePrecision.SECOND)) {
			showcalendar.set(Calendar.YEAR, getSelectedYear());
			showcalendar.set(Calendar.MONTH, getSelectedMonth() - 1);
			showcalendar.set(Calendar.DAY_OF_MONTH, getSelectedDay());
			showcalendar.set(Calendar.HOUR_OF_DAY, getSelectedHour());
			showcalendar.set(Calendar.MINUTE, getSelectedMinite());
			showcalendar.set(Calendar.SECOND, getSelectedSecond());
		} else if (showprecision != null && showprecision.equals(EdalDatePrecision.MILLISECOND)) {
			showcalendar.set(Calendar.YEAR, getSelectedYear());
			showcalendar.set(Calendar.MONTH, getSelectedMonth() - 1);
			showcalendar.set(Calendar.DAY_OF_MONTH, getSelectedDay());
			showcalendar.set(Calendar.HOUR_OF_DAY, getSelectedHour());
			showcalendar.set(Calendar.MINUTE, getSelectedMinite());
			showcalendar.set(Calendar.SECOND, getSelectedSecond());
			showcalendar.set(Calendar.MILLISECOND, getSelectedMillisecond());
		}
	}
}

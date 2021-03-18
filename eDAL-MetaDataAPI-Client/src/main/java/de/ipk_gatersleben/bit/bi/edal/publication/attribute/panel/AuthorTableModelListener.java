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
package de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.NaturalPerson;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.ORCID;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.orcid.ORCIDException;
import de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel.AuthorsPanel.AuthorTableModel;

/**
 * {@link TableModelListener} to recognize if the first name and the last name
 * of an author was entered and try to find out his {@link ORCID}
 * 
 * @author arendd
 *
 */
public class AuthorTableModelListener implements TableModelListener {

	private final AuthorsPanel parent;
	public static boolean leavedOrcidField = true;
	public static boolean leavedName = true;

	public AuthorTableModelListener(AuthorsPanel parent) {
		this.parent = parent;
	}

	@Override
	public void tableChanged(TableModelEvent event) {

//		System.out.println(leavedName+"\t"+leavedOrcidField);
		
		if (event.getType() == TableModelEvent.UPDATE && (event.getColumn() == 1)
				&& AuthorTableModelListener.leavedOrcidField) {
			// System.out.println("ORCID muss geprüft werden");
			AuthorTableModel model = (AuthorTableModel) event.getSource();
			String orcidString = (String) model.getValueAt(event.getLastRow(), 1);

			if (!orcidString.isEmpty()) {
				try {
					new ORCID(orcidString);

					NaturalPerson np = new OrcidSearchForNameDialog(parent, model, orcidString).getNaturalPerson();

					if (((String) model.getValueAt(event.getLastRow(), 2)).trim().isEmpty()
							&& ((String) model.getValueAt(event.getLastRow(), 3)).trim().isEmpty()) {
						AuthorTableModelListener.leavedName = false;
						model.setValueAt(np.getGivenName(), event.getLastRow(), 2);
						AuthorTableModelListener.leavedName = false;
						model.setValueAt(np.getSureName(), event.getLastRow(), 3);
					} else if (((String) model.getValueAt(event.getLastRow(), 2)).trim().isEmpty()) {
						if (((String) model.getValueAt(event.getLastRow(), 3)).trim().equals(np.getSureName())) {
							AuthorTableModelListener.leavedName = false;
							model.setValueAt(np.getGivenName(), event.getLastRow(), 2);
						} else {

							Object[] options = { "Change Surname", "Correct ORCID" };
							int n = JOptionPane.showOptionDialog(this.parent,
									"The surname of the ORCID '" + orcidString + "' is '" + np.getSureName()
											+ "' and not '" + ((String) model.getValueAt(event.getLastRow(), 3)).trim()
											+ "' !?",
									"Surname is wrong !", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE,
									null, options, options[0]);
							if (n == 0) {
								AuthorTableModelListener.leavedName = false;
								model.setValueAt(np.getGivenName(), event.getLastRow(), 2);
								AuthorTableModelListener.leavedName = false;
								model.setValueAt(np.getSureName(), event.getLastRow(), 3);
							} else {
								final int currentRow = parent.getTable().convertRowIndexToView(event.getLastRow());
								final int currentColumn = parent.getTable().convertColumnIndexToView(1);
								AuthorTableModelListener.leavedOrcidField = false;
								AuthorTableModelListener.leavedName = false;
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										parent.getTable().changeSelection(currentRow, currentColumn, false, false);
										parent.getTable().editCellAt(currentRow, currentColumn);
									}
								});
							}
						}
					} else if (((String) model.getValueAt(event.getLastRow(), 3)).trim().isEmpty()) {
						if (((String) model.getValueAt(event.getLastRow(), 2)).trim().equals(np.getGivenName())) {
							AuthorTableModelListener.leavedName = false;
							model.setValueAt(np.getSureName(), event.getLastRow(), 3);
						} else {
							Object[] options = { "Change Given Name", "Correct ORCID" };
							int n = JOptionPane.showOptionDialog(this.parent,
									"The given name of the ORCID '" + orcidString + "' is '" + np.getGivenName()
											+ "' and not '" + ((String) model.getValueAt(event.getLastRow(), 2)).trim()
											+ "' !?",
									"Given Name is wrong !", JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.ERROR_MESSAGE, null, options, options[0]);

							if (n == 0) {
								AuthorTableModelListener.leavedName = false;
								model.setValueAt(np.getGivenName(), event.getLastRow(), 2);
								AuthorTableModelListener.leavedName = false;
								model.setValueAt(np.getSureName(), event.getLastRow(), 3);
							} else {
								final int currentRow = parent.getTable().convertRowIndexToView(event.getLastRow());
								final int currentColumn = parent.getTable().convertColumnIndexToView(1);
								AuthorTableModelListener.leavedOrcidField = false;
								AuthorTableModelListener.leavedName = false;
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										parent.getTable().changeSelection(currentRow, currentColumn, false, false);
										parent.getTable().editCellAt(currentRow, currentColumn);
									}
								});
							}

						}
					} else {

						// givenname and surename is set

						if (!((String) model.getValueAt(event.getLastRow(), 2)).trim().equals(np.getGivenName())
								|| !((String) model.getValueAt(event.getLastRow(), 3)).trim()
										.equals(np.getSureName())) {

							Object[] options = { "Change Given Name / Surname", "Correct ORCID" };
							int n = JOptionPane.showOptionDialog(this.parent,
									"The given name / surname of the ORCID '" + orcidString + "' is '"
											+ np.getGivenName() + " " + np.getSureName() + "' and not '"
											+ ((String) model.getValueAt(event.getLastRow(), 2)).trim() + " "
											+ ((String) model.getValueAt(event.getLastRow(), 3)).trim() + "' !?",
									"The Given Name / Surname is wrong !", JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.ERROR_MESSAGE, null, options, options[0]);

							if (n == 0) {
								AuthorTableModelListener.leavedName = false;
								model.setValueAt(np.getGivenName(), event.getLastRow(), 2);
								AuthorTableModelListener.leavedName = false;
								model.setValueAt(np.getSureName(), event.getLastRow(), 3);
							} else {
								final int currentRow = parent.getTable().convertRowIndexToView(event.getLastRow());
								final int currentColumn = parent.getTable().convertColumnIndexToView(1);
								AuthorTableModelListener.leavedOrcidField = false;
								AuthorTableModelListener.leavedName = false;
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										parent.getTable().changeSelection(currentRow, currentColumn, false, false);
										parent.getTable().editCellAt(currentRow, currentColumn);
									}
								});
							}
						} else {
							AuthorTableModelListener.leavedName = false;
							model.setValueAt(np.getGivenName(), event.getLastRow(), 2);
							AuthorTableModelListener.leavedName = false;
							model.setValueAt(np.getSureName(), event.getLastRow(), 3);

						}

					}

				} catch (ORCIDException e) {
					final int currentRow = parent.getTable().convertRowIndexToView(event.getLastRow());
					final int currentColumn = parent.getTable().convertColumnIndexToView(1);

					Object[] options = { "Remove ORCID", "Correct ORCID" };
					int n = JOptionPane.showOptionDialog(this.parent, e.getMessage(), "Wrong ORCID",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);

					if (n == 1) {
						AuthorTableModelListener.leavedOrcidField = false;
						AuthorTableModelListener.leavedName = false;
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								parent.getTable().changeSelection(currentRow, currentColumn, false, false);
								parent.getTable().editCellAt(currentRow, currentColumn);
							}
						});
					} else {
						model.setValueAt("", event.getLastRow(), 1);
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								parent.getTable().changeSelection(currentRow, currentColumn, false, false);
								parent.getTable().editCellAt(currentRow, currentColumn);
							}
						});
					}
				}
			}
		}

		else if (event.getType() == TableModelEvent.UPDATE && (event.getColumn() == 2 || event.getColumn() == 3)
				&& AuthorTableModelListener.leavedName) {

			// System.out.println("NAME geändert");

			AuthorTableModel model = (AuthorTableModel) event.getSource();
			if ((model.getValueAt(event.getLastRow(), 1) != null
					|| !((String) model.getValueAt(event.getLastRow(), 1)).trim().isEmpty())
					&& !((String) model.getValueAt(event.getLastRow(), 2)).trim().isEmpty()
					&& !((String) model.getValueAt(event.getLastRow(), 3)).trim().isEmpty()) {
				// System.out.println("SEARCH FOR ORCID");
				String firstName = ((String) model.getValueAt(event.getLastRow(), 2));
				String lastName = ((String) model.getValueAt(event.getLastRow(), 3));
				String selectedOrcid = new OrcidSearchForIdDialog(parent, firstName, lastName).getSelectedOrcid();

				if (!selectedOrcid.isEmpty() || selectedOrcid == null) {
					AuthorTableModelListener.leavedOrcidField = false;
					model.setValueAt(selectedOrcid, event.getLastRow(), 1);
				} else {
					model.setValueAt("", event.getLastRow(), 1);
				}
			}

		} else {
			AuthorTableModelListener.leavedOrcidField = true;
			AuthorTableModelListener.leavedName = true;

		}
	}
}
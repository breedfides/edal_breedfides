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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;

/**
 * The <code>SubjectsDialog</code> can be used to edit <code>Subjects</code>,
 * which implements the <code>MetadataeditDialog</code> class, we can use it
 * with a couple of lines of code:
 * 
 * <pre>
 * SubjectsDialog subjectsDialog = new SubjectsDialog(subjects, title);
 * subjectsDialog.showOpenDialog();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 */
public class SubjectsDialog extends MetaDataEditDialog {

	private static final long serialVersionUID = 1L;
	private Set<UntypedData> subjects;

	private JTextField subjecttext;
	private JTable table;
	private DefaultTableModel dm;

	/**
	 * Constructs a <code>SubjectsDialog</code> that is initialized with
	 * <code>subjects</code>.
	 * 
	 * @param subjects
	 *            UntypedData to set
	 * @param title
	 *            the title for the dialog
	 */
	public SubjectsDialog(Set<UntypedData> subjects, String title) {
		super();
		this.subjects = subjects;
		setTitle(title);

		initUi();
	}

	/**
	 * Returns the Subjects typed in by user.
	 * 
	 * @return the Subjects typed in by user.
	 */
	public Set<UntypedData> getSubjects() {
		Set<UntypedData> idlist = new HashSet<UntypedData>();

		int row = dm.getRowCount();
		for (int i = 0; i < row; i++) {
			idlist.add(new UntypedData(dm.getValueAt(i, 0).toString()));
		}

		return idlist;
	}

	@Override
	public void initdata() {

	}

	private void initUi() {
		JPanel contents = (JPanel) getContentPane();
		contents.setLayout(new BorderLayout());

		JPanel editPane = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		editPane.setLayout(gridbag);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 3;
		c.gridwidth = 4;
		c.weightx = 1.0;

		dm = new DefaultTableModel();
		Vector<String> dummyHeader = new Vector<String>();
		dummyHeader.addElement("");

		dm.setDataVector(collection2Vector(subjects), dummyHeader);
		table = new JTable(dm);
		table.setShowGrid(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollTable = new JScrollPane(table);
		scrollTable.setColumnHeader(null);
		scrollTable.setMinimumSize(new Dimension(320, 80));

		Box tableBox = new Box(BoxLayout.Y_AXIS);
		tableBox.add(scrollTable);

		gridbag.setConstraints(tableBox, c);
		editPane.add(tableBox);

		subjecttext = new JTextField();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 2;
		c.weightx = 0.5;

		gridbag.setConstraints(subjecttext, c);
		editPane.add(subjecttext);

		JButton addbtn = new JButton(addAction);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0.25;

		gridbag.setConstraints(addbtn, c);
		editPane.add(addbtn);

		JButton delbtn = new JButton(delAction);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 3;
		c.gridy = 3;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0.25;

		gridbag.setConstraints(delbtn, c);
		editPane.add(delbtn);

		contents.add(editPane, BorderLayout.CENTER);
		contents.add(createbuttonpanel(), BorderLayout.SOUTH);

		this.setMinimumSize(new Dimension(320, (int) (320 * 0.618)));
	}

	private Vector<Vector<String>> collection2Vector(Set<UntypedData> strs) {
		Vector<Vector<String>> vector = new Vector<Vector<String>>();
		if (strs != null) {
			for (UntypedData idenstr : strs) {
				Vector<String> v = new Vector<String>();
				v.addElement(idenstr.getString());
				vector.addElement(v);
			}
		}

		return vector;
	}

	private Action addAction = new AbstractAction("add") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if (subjecttext.getText().trim().length() > 0) {
				Vector<String> v = new Vector<String>();
				v.addElement(subjecttext.getText().trim());
				dm.addRow(v);
			}
			subjecttext.setText("");
		}
	};

	private Action delAction = new AbstractAction("remove") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if (table.getSelectedRow() > -1) {
				dm.removeRow(table.getSelectedRow());
			}
		}
	};
}

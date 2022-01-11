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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Set;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSum;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.CheckSumType;

/**
 * The <code>ChecksumDialog</code> can be used to edit <code>CheckSum</code>,
 * which implements the <code>MetadataeditDialog</code> class, we can use it
 * with a couple of lines of code:
 * 
 * <pre>
 * ChecksumDialog checksumDialog = new ChecksumDialog(checksum);
 * checksumDialog.showOpenDialog();
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 */
public class ChecksumDialog extends MetaDataEditDialog {
	private static final long serialVersionUID = 1L;
	private CheckSum checksum;

	private JTable table;
	private DefaultTableModel dm;

	/**
	 * Constructs a <code>ChecksumDialog</code> that is initialized with
	 * <code>checksum</code>.
	 * 
	 * @param checksum
	 *            <code>CheckSum</code> Object
	 */
	public ChecksumDialog(CheckSum checksum) {
		super();
		this.checksum = checksum;
		setTitle("CheckSum");

		initUi();
	}

	/**
	 * Returns the CheckSum inputted by user.
	 * 
	 * @return the CheckSum inputted by user.
	 */
	@SuppressWarnings("unchecked")
	public CheckSum getCheckSum() {
		CheckSum checkSum = new CheckSum();
		Vector<Vector> vector = dm.getDataVector();
		if (vector != null) {
			for (int i = 0; i < vector.size(); i++) {
				Vector<String> v = vector.get(i);
				checkSum.add(new CheckSumType(v.get(0), v.get(1)));
			}
		}

		return checkSum;
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
		dummyHeader.addElement("Algorithm");
		dummyHeader.addElement("CheckSum");

		dm.setDataVector(collection2Vector(checksum.getSet()), dummyHeader);
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
		/*
		 * algorithmlabel = new JLabel("Algorithm:", JLabel.LEFT); c.fill =
		 * GridBagConstraints.BOTH; c.gridx=0; c.gridy=3; c.gridheight=1;
		 * c.gridwidth=1; c.weightx=0.5;
		 * 
		 * gridbag.setConstraints(algorithmlabel, c);
		 * editPane.add(algorithmlabel);
		 * 
		 * algorithmtext = new JTextField(); c.fill = GridBagConstraints.BOTH;
		 * c.gridx=1; c.gridy=3; c.gridheight=1; c.gridwidth=3; c.weightx=0.5;
		 * 
		 * gridbag.setConstraints(algorithmtext, c);
		 * editPane.add(algorithmtext);
		 * 
		 * chesksumlabel = new JLabel("CheckSum:", JLabel.LEFT); c.fill =
		 * GridBagConstraints.BOTH; c.gridx=0; c.gridy=4; c.gridheight=1;
		 * c.gridwidth=1; c.weightx=0.5;
		 * 
		 * gridbag.setConstraints(chesksumlabel, c);
		 * editPane.add(chesksumlabel);
		 * 
		 * chesksumvaluetext = new JTextField(); c.fill =
		 * GridBagConstraints.BOTH; c.gridx=1; c.gridy=4; c.gridheight=1;
		 * c.gridwidth=3; c.weightx=0.5;
		 * 
		 * gridbag.setConstraints(chesksumvaluetext, c);
		 * editPane.add(chesksumvaluetext);
		 * 
		 * JButton addbtn = new JButton(addAction); c.fill =
		 * GridBagConstraints.BOTH; c.gridx=0; c.gridy=5; c.gridheight=1;
		 * c.gridwidth=1; c.weightx=0.25;
		 * 
		 * gridbag.setConstraints(addbtn, c); editPane.add(addbtn);
		 * 
		 * JButton delbtn= new JButton(delAction); c.fill =
		 * GridBagConstraints.BOTH; c.gridx=2; c.gridy=5; c.gridheight=1;
		 * c.gridwidth=1; c.weightx=0.25;
		 * 
		 * gridbag.setConstraints(delbtn, c); editPane.add(delbtn);
		 */
		contents.add(editPane, BorderLayout.CENTER);
		contents.add(createbuttonpanel(), BorderLayout.SOUTH);

		this.setMinimumSize(new Dimension(320, (int) (320 * 0.618)));
	}

	private Vector<Vector<String>> collection2Vector(Set<CheckSumType> set) {
		Vector<Vector<String>> vector = new Vector<Vector<String>>();
		if (set != null) {
			for (CheckSumType checkSumType : set) {
				Vector<String> v = new Vector<String>();
				v.addElement(checkSumType.getAlgorithm());
				v.addElement(checkSumType.getCheckSum());
				vector.addElement(v);
			}
		}

		return vector;
	}
}

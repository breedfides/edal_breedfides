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
package de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.ErrorDialog;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.MetadataDialog;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.JLinkLabel;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.MetaDescription;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.StackTraceUtil;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;

/**
 * <code>MetasaveDialog</code> provides a simple mechanism for the user to get
 * the Metadata of EDAL File or Directory.
 * 
 * The following code pops up a MetasaveDialog:
 * 
 * <pre>
 * MetasaveDialog metasaveDialog = new MetasaveDialog();
 * int returnVal = metasaveDialog.showOpenDialog();
 * if (returnVal == metasaveDialog.APPROVE_OPTION) {
 * 	Map&lt;EnumDublinCoreElements, UntypedData&gt; map = metasaveDialog.getMetadatavalue();
 * }
 * </pre>
 * 
 * @version 1.0
 * @author Jinbo Chen
 * 
 */
public class MetaDataSaveDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private static final Font FONT = new Font("Courier New", Font.PLAIN, 12);

	/**
	 * Return value if cancel is chosen.
	 */
	public static final int CANCEL_OPTION = 1;
	/**
	 * Return value if approve (yes, ok) is chosen.
	 */
	public static final int APPROVE_OPTION = 0;

	public int returnvalue;

	private Map<String, JTextField> detailmap = new HashMap<String, JTextField>();
	private Map<EnumDublinCoreElements, UntypedData> metadatavalue = new HashMap<EnumDublinCoreElements, UntypedData>();

	private JButton savebtn;
	private JButton cancelbtn;
	private ClientPrimaryDataEntity dataentry;

	/**
	 * Constructs a <code>MetasaveDialog</code>
	 */
	public MetaDataSaveDialog() {
		metadatavalue.clear();
		initUi();
	}

	/**
	 * Constructs a <code>MetasaveDialog</code> that is initialized with
	 * <code>dataentry</code> as the EDAL dataentry
	 * 
	 * @param dataentry
	 *            the EDAL dataentry
	 */
	public MetaDataSaveDialog(ClientPrimaryDataEntity dataentry) {
		this.dataentry = dataentry;
		initUi();
	}

	/**
	 * Returns metadata information. This can be when you want to save file
	 * 
	 * @return metadata informatin
	 */
	public Map<EnumDublinCoreElements, UntypedData> getMetaDataValues() {
		return metadatavalue;
	}

	/**
	 * pop up a Metadata Dialog
	 * 
	 * @return the result
	 */
	public int showOpenDialog() {
		setModal(true);
		setLocationRelativeTo(null);
		setVisible(true);
		return returnvalue;
	}

	private void initUi() {
		initEditors();

		JPanel contents = (JPanel) getContentPane();
		contents.setLayout(new BorderLayout());
		contents.setBorder(new TitledBorder(""));

		contents.add(new JScrollPane(createeditpanel()), BorderLayout.CENTER);
		contents.add(createbuttonpanel(), BorderLayout.SOUTH);

		this.setPreferredSize(new Dimension(1050, 650));
		this.setMinimumSize(new Dimension(1050, 650));

		addWindowListener(createAppCloser());
	}

	private JPanel buildFormui() {
		List<String> columnlist = new ArrayList<String>();

		for (EnumDublinCoreElements elem : EnumDublinCoreElements.values()) {
			try {
				String dataName = elem.toString();
				try {
					if (dataName.equalsIgnoreCase("title") && (dataentry != null) && (dataentry.isDirectory())) {
						// the directory dont't need show title
						continue;
					}
				} catch (RemoteException e) {
					ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
					ErrorDialog.showError(e);
				}
				columnlist.add(dataName);
			} catch (UnsupportedOperationException e) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
				ErrorDialog.showError(e);
			}
		}

		final Object[] columnNames = columnlist.toArray(new String[0]);

		int columnlen = columnNames.length;

		JPanel detialpanel = new JPanel();
		detialpanel.setFont(FONT);

		detialpanel.setLayout(new MigLayout("", "[80!][90%!]", ""));

		for (int i = 0; i < columnlen; i++) {
			JLabel label1 = new JLinkLabel(columnNames[i].toString() + ":", JLabel.LEFT);
			label1.setVerticalAlignment(JLabel.CENTER);
			label1.setToolTipText("<html>" + MetaDescription.getDescription(columnNames[i].toString()) + "</html>");
			label1.setFont(FONT);
			detialpanel.add(label1);

			JTextField text1 = new JTextField();
			text1.setFont(FONT);
			detialpanel.add(text1, "wrap,growx");
			detailmap.put(columnNames[i].toString(), text1);
		}

		return detialpanel;
	}

	private JPanel createeditpanel() {
		JPanel detialpanel = buildFormui();

		if (dataentry != null) {
			MetaData filemetadata;
			try {
				filemetadata = dataentry.getCurrentVersion().getMetaData();
				try {
					// setTitle(filemetadata.getElementValue(EnumDublinCoreElements.TITLE).toString());
					setTitle("Modify Metadata of '"
							+ filemetadata.getElementValue(EnumDublinCoreElements.TITLE).toString() + "'");
				} catch (Exception e1) {
					ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
					ErrorDialog.showError(e1);
				}
				for (final EnumDublinCoreElements elem : EnumDublinCoreElements.values()) {
					final String dataName = elem.toString();
					if (detailmap.containsKey(dataName)) {
						UntypedData data = null;
						try {
							data = filemetadata.getElementValue(elem);
							String value = data.toString();

							detailmap.get(dataName).setText(value);
							detailmap.get(dataName).setCaretPosition(0);
							if (value.trim().length() > 0) {
								detailmap.get(dataName).setToolTipText(value);
							}
						} catch (MetaDataException e) {
							ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
							ErrorDialog.showError(e);
						}
						metadatavalue.put(elem, data);
						final AbstractMetaDataEditor editor = EditorContainer
								.getEditor(EnumDublinCoreElements.valueOf(dataName));
						if (editor != null) {
							for (MouseListener al : detailmap.get(dataName).getMouseListeners()) {
								detailmap.get(dataName).removeMouseListener(al);
							}
							detailmap.get(dataName).addMouseListener(new MouseAdapter() {
								public void mouseClicked(MouseEvent e) {
									if (e.getClickCount() == 1) {
										editor.setTitle(dataName);
										editor.setValue(metadatavalue.get(elem));

										int ret = editor.showOpenDialog();
										if (ret == MetadataDialog.APPROVE_OPTION) {
											UntypedData untypedData = (UntypedData) editor.getValue();
											metadatavalue.put(elem, untypedData);

											if (untypedData != null) {
												detailmap.get(dataName).setText(untypedData.toString());
												detailmap.get(dataName).setCaretPosition(0);
												if (untypedData.toString().trim().length() > 0) {
													detailmap.get(dataName).setToolTipText(untypedData.toString());
												}
											}
										}
									}
								}
							});
							continue;
						} else {
							detailmap.get(dataName).setForeground(Color.GRAY);
						}
						detailmap.get(dataName).setEditable(false);
					}
				}
			} catch (RemoteException e1) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				JOptionPane.showMessageDialog(null, "Call remote Edal server function exception:" + e1.getMessage(),
						"EdalFileChooser", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			for (final EnumDublinCoreElements elem : EnumDublinCoreElements.values()) {
				final String dataName = elem.toString();
				if (detailmap.containsKey(dataName)) {
					final AbstractMetaDataEditor editor = EditorContainer
							.getEditor(EnumDublinCoreElements.valueOf(dataName));
					if (editor != null) {
						for (MouseListener al : detailmap.get(dataName).getMouseListeners()) {
							detailmap.get(dataName).removeMouseListener(al);
						}
						detailmap.get(dataName).addMouseListener(new MouseAdapter() {
							public void mouseClicked(MouseEvent e) {
								if (e.getClickCount() == 1) {
									editor.setValue(null);
									int ret = editor.showOpenDialog();
									if (ret == MetadataDialog.APPROVE_OPTION) {
										UntypedData untypedData = (UntypedData) editor.getValue();
										metadatavalue.put(elem, untypedData);
										if (untypedData != null) {
											detailmap.get(dataName).setText(untypedData.toString());
											detailmap.get(dataName).setCaretPosition(0);
											if (untypedData.toString().trim().length() > 0) {
												detailmap.get(dataName).setToolTipText(untypedData.toString());
											}
										}
									}
								}
							}
						});
						continue;
					}
					detailmap.get(dataName).setForeground(Color.GRAY);
					detailmap.get(dataName).setEditable(false);
				}
			}

		}

		return detialpanel;
	}

	private JPanel createbuttonpanel() {
		savebtn = new JButton(okAction);
		cancelbtn = new JButton(cancelAction);

		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		buttonPane.add(savebtn);
		buttonPane.add(cancelbtn);

		this.getRootPane().setDefaultButton(savebtn);

		return buttonPane;
	}

	private void initEditors() {
		// ViewerContainer.clear();

		EditorContainer.registerEditor(EnumDublinCoreElements.CREATOR, new PersonInfoEditor());
		EditorContainer.registerEditor(EnumDublinCoreElements.CONTRIBUTOR, new PersonInfoEditor());
		EditorContainer.registerEditor(EnumDublinCoreElements.PUBLISHER, new LegalPersonInfoEditor());
		EditorContainer.registerEditor(EnumDublinCoreElements.RELATION, new IdentifierRelationEditor());
		EditorContainer.registerEditor(EnumDublinCoreElements.DATE, new DateEventsEditor());
		EditorContainer.registerEditor(EnumDublinCoreElements.COVERAGE, new TextEditor());
		EditorContainer.registerEditor(EnumDublinCoreElements.DESCRIPTION, new TextEditor());
		/*
		 * EditorContainer.registerEditor(EnumDublinCoreElements.CHECKSUM, new
		 * ChecksumEditor());
		 */
		EditorContainer.registerEditor(EnumDublinCoreElements.IDENTIFIER, new IdentifierEditor());
		EditorContainer.registerEditor(EnumDublinCoreElements.LANGUAGE, new EdalLanguageEditor());
		EditorContainer.registerEditor(EnumDublinCoreElements.RIGHTS, new TextEditor());
		EditorContainer.registerEditor(EnumDublinCoreElements.SOURCE, new TextEditor());
		EditorContainer.registerEditor(EnumDublinCoreElements.SUBJECT, new SubjectsEditor());
		/*
		 * user can't modify file name
		 * EditorContainer.registerEditor(EnumDublinCoreElements.TITLE,new
		 * TextEditor());
		 */
	}

	private Action okAction = new AbstractAction("Ok") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			returnvalue = APPROVE_OPTION;
			dispose();
		}
	};

	private Action cancelAction = new AbstractAction("Cancel") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			returnvalue = CANCEL_OPTION;
			dispose();
		}
	};

	private void focusEvt(java.awt.event.WindowEvent evt) {
		savebtn.requestFocus();
	}

	private WindowListener createAppCloser() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent we) {
				returnvalue = CANCEL_OPTION;
				dispose();
			}

			@Override
			public void windowOpened(java.awt.event.WindowEvent evt) {
				focusEvt(evt);
			}
		};
	}
}

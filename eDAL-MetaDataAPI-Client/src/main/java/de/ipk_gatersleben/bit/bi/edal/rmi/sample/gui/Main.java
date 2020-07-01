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
package de.ipk_gatersleben.bit.bi.edal.rmi.sample.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataFileException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.EdalFileChooser;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.ErrorDialog;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor.MetaDataSaveDialog;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.StackTraceUtil;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.rmi.server.Authentication;
import de.ipk_gatersleben.bit.bi.edal.sample.EdalHelpers;

public class Main {

	public static JFrame frame;
	public static JFrame externalframe;
	private static JTextField localpathtext;
	private static JTextField localfilenametext;
	private static ClientPrimaryDataEntity fileobjtoupload;
	private static Map<EnumDublinCoreElements, UntypedData> metadatavalue;
	private static LookAndFeel defalutskin;
	private static ClientDataManager dataManagerClient;
	static Action openfileAction = new AbstractAction("Open EDAL File") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			Window window = frame.getContentPane() instanceof Window ? (Window) frame.getContentPane() : SwingUtilities.getWindowAncestor(frame.getContentPane());

			EdalFileChooser dlg = window instanceof Frame ? new EdalFileChooser((Frame) window, dataManagerClient) : new EdalFileChooser((Dialog) window, dataManagerClient);
			dlg.setMultiSelectionEnabled(true);
			dlg.showConnectionButton(false);
			/*
			 * EdalFileNameExtensionFilter filter = new
			 * EdalFileNameExtensionFilter("JPG & GIF Images", "jpg", "gif");
			 * dlg.setFileFilter(filter);
			 */
			dlg.setFileSelectionMode(EdalFileChooser.FILES_ONLY);
			int returnVal = dlg.showOpenDialog();
			if (returnVal == EdalFileChooser.APPROVE_OPTION) {
				ClientPrimaryDataEntity[] files = dlg.getSelectedFiles();
				if (files != null) {
					for (ClientPrimaryDataEntity file : files) {
						try {
							if (file != null && !file.isDirectory()) {
								savefiletolocal(file);
							}
						} catch (RemoteException e1) {
							ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
							ErrorDialog.showError(e1);
						}
					}
				}
			}

		}
	};

	static Action opendirAction = new AbstractAction("Choose EDAL Path") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			Window window = frame.getContentPane() instanceof Window ? (Window) frame.getContentPane() : SwingUtilities.getWindowAncestor(frame.getContentPane());

			EdalFileChooser dlg = window instanceof Frame ? new EdalFileChooser((Frame) window, dataManagerClient) : new EdalFileChooser((Dialog) window, dataManagerClient);
			dlg.setFileSelectionMode(EdalFileChooser.DIRECTORIES_ONLY);
			int returnVal = dlg.showSaveDialog();
			if (returnVal == EdalFileChooser.APPROVE_OPTION) {
				fileobjtoupload = dlg.getSelectedFile();
			}

		}
	};

	static Action selectlocalpathAction = new AbstractAction("Choose path to download") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			String dir = "";
			final Component parent = null;
			final JFileChooser chooser = new JFileChooser(dir);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
				dir = chooser.getSelectedFile().getAbsolutePath();
				if (dir != null) {
					localpathtext.setText(dir);
				}
			}
		}
	};

	static Action selectlocalfileAction = new AbstractAction("Select Local File") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			JFileChooser fileopen = new JFileChooser();
			int ret = fileopen.showOpenDialog(null);
			if (ret == JFileChooser.APPROVE_OPTION) {
				File file = fileopen.getSelectedFile();
				localfilenametext.setText(file.getPath());
			}
		}
	};

	static Action uploadAction = new AbstractAction("Upload") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if (fileobjtoupload != null && localfilenametext.getText().trim().length() > 0) {
				try {
					File inputFile = new File(localfilenametext.getText().trim());
					InputStream in = new FileInputStream(inputFile);
					if (fileobjtoupload.isDirectory()) {
						ClientPrimaryDataDirectory dir = (ClientPrimaryDataDirectory) fileobjtoupload;
						ClientPrimaryDataFile file;
						if (dir.exist(inputFile.getName())) {
							final String[] buttons = { "Yes", "No" };
							final int rc = JOptionPane.showOptionDialog(null, "Do you want to update version?", "Demo", JOptionPane.INFORMATION_MESSAGE, 0, null, buttons, buttons[0]);
							if (rc != 0) {
								in.close();
								return;
							}
							file = (ClientPrimaryDataFile) dir.getPrimaryDataEntity(inputFile.getName());

							try {
								file.store(in);

								MetaDataSaveDialog metadlg = new MetaDataSaveDialog(file);
								int returnVal = metadlg.showOpenDialog();
								if (returnVal == MetaDataSaveDialog.APPROVE_OPTION) {
									metadatavalue = metadlg.getMetaDataValues();
								} else {
									metadatavalue = null;
								}

								if (metadatavalue != null) {
									try {
										MetaData fileMetaData = file.getMetaData().clone();
										Iterator<Map.Entry<EnumDublinCoreElements, UntypedData>> iter = metadatavalue.entrySet().iterator();
										while (iter.hasNext()) {
											Map.Entry<EnumDublinCoreElements, UntypedData> entry = (Map.Entry<EnumDublinCoreElements, UntypedData>) iter.next();
											EnumDublinCoreElements key = entry.getKey();
											UntypedData val = entry.getValue();
											fileMetaData.setElementValue(key, val);
										}
										file.setMetaData(fileMetaData);
									} catch (Exception re) {
										ClientDataManager.logger.error(StackTraceUtil.getStackTrace(re));
										ErrorDialog.showError(re);
									}
								}

							} catch (java.security.AccessControlException se) {
								ClientDataManager.logger.error(StackTraceUtil.getStackTrace(se));
								ErrorDialog.showError(se);
							}
						} else {
							try {
								file = dir.createPrimaryDataFile(inputFile.getName());
								file.store(in);

								MetaDataSaveDialog metadlg = new MetaDataSaveDialog(file);
								int returnVal = metadlg.showOpenDialog();
								if (returnVal == MetaDataSaveDialog.APPROVE_OPTION) {
									metadatavalue = metadlg.getMetaDataValues();
								} else {
									metadatavalue = null;
								}

								if (metadatavalue != null) {
									try {
										MetaData fileMetaData = file.getMetaData().clone();
										Iterator<Map.Entry<EnumDublinCoreElements, UntypedData>> iter = metadatavalue.entrySet().iterator();
										while (iter.hasNext()) {
											Map.Entry<EnumDublinCoreElements, UntypedData> entry = (Map.Entry<EnumDublinCoreElements, UntypedData>) iter.next();
											EnumDublinCoreElements key = entry.getKey();
											if (key == EnumDublinCoreElements.TYPE || key == EnumDublinCoreElements.SIZE) {
												// the user can't change type
												// and size attribute
												continue;
											}
											UntypedData val = entry.getValue();
											fileMetaData.setElementValue(key, val);
										}
										file.setMetaData(fileMetaData);
									} catch (Exception re) {
										ClientDataManager.logger.error(StackTraceUtil.getStackTrace(re));
										ErrorDialog.showError(re);
									}
								}

							} catch (java.security.AccessControlException se) {
								ClientDataManager.logger.error(StackTraceUtil.getStackTrace(se));
								ErrorDialog.showError(se);
							}

						}
					} else {
						final String[] buttons = { "Yes", "No" };
						final int rc = JOptionPane.showOptionDialog(null, "Do you want to update version?", "Demo", JOptionPane.INFORMATION_MESSAGE, 0, null, buttons, buttons[0]);
						if (rc != 0) {
							in.close();
							return;
						}

						try {
							((ClientPrimaryDataFile) fileobjtoupload).store(in);

							MetaDataSaveDialog metadlg = new MetaDataSaveDialog(fileobjtoupload);
							int returnVal = metadlg.showOpenDialog();
							if (returnVal == MetaDataSaveDialog.APPROVE_OPTION) {
								metadatavalue = metadlg.getMetaDataValues();
							} else {
								metadatavalue = null;
							}

							if (metadatavalue != null) {
								try {
									MetaData fileMetaData = fileobjtoupload.getMetaData().clone();
									Iterator<Map.Entry<EnumDublinCoreElements, UntypedData>> iter = metadatavalue.entrySet().iterator();
									while (iter.hasNext()) {
										Map.Entry<EnumDublinCoreElements, UntypedData> entry = (Map.Entry<EnumDublinCoreElements, UntypedData>) iter.next();
										EnumDublinCoreElements key = entry.getKey();
										if (key == EnumDublinCoreElements.TYPE || key == EnumDublinCoreElements.SIZE) {
											// the user can't change type and
											// size attribute
											continue;
										}
										UntypedData val = entry.getValue();
										fileMetaData.setElementValue(key, val);
									}
									fileobjtoupload.setMetaData(fileMetaData);
								} catch (Exception re) {
									ClientDataManager.logger.error(StackTraceUtil.getStackTrace(re));
									JOptionPane.showMessageDialog(null, re.getMessage(), "Demo", JOptionPane.ERROR_MESSAGE);
								}
							}

						} catch (java.security.AccessControlException se) {
							ClientDataManager.logger.error(StackTraceUtil.getStackTrace(se));
							ErrorDialog.showError(se);
						}
					}

					JOptionPane.showMessageDialog(null, "Upload Data succeed!", "Demo", JOptionPane.INFORMATION_MESSAGE);
				} catch (FileNotFoundException e1) {
					ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
					ErrorDialog.showError(e1);
				} catch (IOException e1) {
					ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
					ErrorDialog.showError(e1);
				} catch (Exception e1) {
					ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
					ErrorDialog.showError(e1);
				}
			} else {
				if (fileobjtoupload == null) {
					JOptionPane.showMessageDialog(null, "Please choose the edal path you want to upload!", "Demo", JOptionPane.ERROR_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "Please choose the file you want to upload!", "Demo", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	};

	private static void savefiletolocal(ClientPrimaryDataEntity remotefile) {
		try {
			File file;
			if (localpathtext.getText().trim().length() == 0) {
				JOptionPane.showMessageDialog(null, "Please choose a directory to save the file!", "Demo", JOptionPane.ERROR_MESSAGE);
				return;
			} else {
				file = new File(localpathtext.getText().trim() + File.separator + remotefile.getName());
			}

			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e1) {
					ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				}
			} else {
				final String[] buttons = { "Yes", "No" };
				final int rc = JOptionPane.showOptionDialog(null, "Do you want to overwrite file:" + file.getName(), "Demo", JOptionPane.INFORMATION_MESSAGE, 0, null, buttons, buttons[0]);
				if (rc != 0) {
					return;
				}
			}
			OutputStream out = new FileOutputStream(file);
			((ClientPrimaryDataFile) remotefile).read(out);
			JOptionPane.showMessageDialog(null, "Save File " + remotefile.getName() + " succeed!", "Demo", JOptionPane.INFORMATION_MESSAGE);
		} catch (FileNotFoundException e1) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
			ErrorDialog.showError(e1);
		} catch (IOException e1) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
			ErrorDialog.showError(e1);
		} catch (PrimaryDataFileException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		}
	}


	public static void main(String[] args) {

		System.out.println("e!DAL FileChooser GUI\nIPK-Gatersleben.\nAll rights reserved.\n");
		// defalutskin = UIManager.getLookAndFeel();

		for (final LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
			if (laf.getName().equals("Nimbus")) {
				try {
					UIManager.setLookAndFeel(laf.getClassName());
				} catch (final Exception e) {

				}
			}
		}

		if (args.length == 0) {
			args = new String[2];
			args[0] = "localhost";
			args[1] = "1099";
		}

		try {
			Authentication authentication = new Authentication(EdalHelpers.authenticateWinOrUnixOrMacUser());
			dataManagerClient = new ClientDataManager(args[0], Integer.valueOf(args[1]), authentication);
		} catch (EdalAuthenticateException e1) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
			ErrorDialog.showError(e1);
		}

		frame = new JFrame("Demo");
		JTabbedPane tabbedPane = new JTabbedPane();

		JPanel p1 = new JPanel();
		p1.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.1;
		p1.add(new JButton(openfileAction), c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 0;

		localpathtext = new JTextField();
		p1.add(localpathtext, c);

		localpathtext.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

			}
		});

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		JButton selectlocalpathbtn = new JButton(selectlocalpathAction);
		p1.add(selectlocalpathbtn, c);

		tabbedPane.addTab("Get file from EDAL", p1);

		JPanel p2 = new JPanel();
		p2.setLayout(new GridBagLayout());

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.1;
		p2.add(new JButton(opendirAction), c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 0;

		localfilenametext = new JTextField();
		p2.add(localfilenametext, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		JButton selectremotepathbtn = new JButton(selectlocalfileAction);
		p2.add(selectremotepathbtn, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		JButton saveremotebtn = new JButton(uploadAction);
		p2.add(saveremotebtn, c);

		tabbedPane.addTab("Upload file to EDAL", p2);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		frame.setSize(600, 300);
		frame.setLocationRelativeTo(null);
		if (args.length == 0) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} else {
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.addWindowListener(Main.createAppCloser());
		}

		frame.setVisible(true);

	}

	public static void externalcall(JFrame externalframe) {
		Main.externalframe = externalframe;
		Main.externalframe.setEnabled(false);
		Main.main(new String[] { "" });
	}

	private static WindowListener createAppCloser() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent we) {
				Main.frame.dispose();
				if(Main.externalframe!=null)
				{
					Main.externalframe.setEnabled(true);
				}
				if (defalutskin != null) {
					try {
						UIManager.setLookAndFeel(defalutskin);
					} catch (UnsupportedLookAndFeelException e) {

					}
				}
			}
		};
	}

}

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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.AccessControlException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTaskPaneContainer;

import de.ipk_gatersleben.bit.bi.edal.primary_data.file.EdalException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataDirectoryException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.file.PrimaryDataEntityVersionException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.DateEvents;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EdalDate;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.EnumDublinCoreElements;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.MetaDataException;
import de.ipk_gatersleben.bit.bi.edal.primary_data.metadata.UntypedData;
import de.ipk_gatersleben.bit.bi.edal.primary_data.security.EdalAuthenticateException;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientDataManager;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataDirectory;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntity;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataEntityVersion;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.ClientPrimaryDataFile;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.editor.MetaDataSaveDialog;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer.CheckSumViewer;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer.DataFormatViewer;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer.DateEventsViewer;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer.IdentifierRelationViewer;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer.IdentifierViewer;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer.LanguageViewer;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer.LegalPersonInfoViewer;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer.MetadataViewer;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer.PersonInfoViewer;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer.SubjectViewer;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer.TextViewer;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.gui.viewer.ViewerContainer;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.Const;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.EdalAbstractFileFilter;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.EdalFileHelper;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.EdalTable;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.EdalTitleDialog;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.EdalTreeCellRenderer;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.InfiniteProgressPanel;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.JLinkLabel;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.JVersionLabel;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.LailapsJXTaskPane;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.MetaDescription;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.PrincipalUtil;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.StackTraceUtil;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.UiUtil;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.XStatusBar;
import de.ipk_gatersleben.bit.bi.edal.rmi.client.util.XStatusSeparator;

/**
 * <code>EDALFileChooser</code> provides a simple mechanism for the user to
 * choose a file from EDAL File System.
 * 
 * The following code pops up a file chooser for the user that sees only .jpg
 * and .gif images:
 * 
 * <pre>
 * EDALFileChooser chooser = new EDALFileChooser((Frame) window, rootDirectory);
 * EdalFileNameExtensionFilter filter = new EdalFileNameExtensionFilter(&quot;JPG &amp; GIF Images&quot;, &quot;jpg&quot;, &quot;gif&quot;);
 * chooser.setFileFilter(filter);
 * int returnVal = chooser.showOpenDialog();
 * if (returnVal == EDALFileChooser.APPROVE_OPTION) {
 * 	System.out.println(&quot;You chose to open this file: &quot; + chooser.getSelectedFile().getName());
 * }
 * </pre>
 * 
 * attribute: isContainer false description: A component which allows for the
 * interactive selection of a file.
 * 
 * @version 1.0
 * @author Jinbo Chen
 * 
 */

public class EdalFileChooser extends EdalTitleDialog {
	private class LinkMouseListener extends MouseAdapter {
		private final int versionidx;
		private final JPanel detailpanelparent;
		private final List<JVersionLabel> versionbuttonlist;

		public LinkMouseListener(final List<JVersionLabel> versionbuttonlist, final int versionidx,
				final JPanel detailpanelparent) {
			this.versionidx = versionidx;
			this.detailpanelparent = detailpanelparent;
			this.versionbuttonlist = versionbuttonlist;
		}

		@Override
		public void mouseClicked(final java.awt.event.MouseEvent evt) {
			if (this.versionbuttonlist != null) {
				for (int i = 0; i < this.versionbuttonlist.size(); i++) {
					if (i != this.versionidx) {
						this.versionbuttonlist.get(i).setSelect(false);
					} else {
						this.versionbuttonlist.get(i).setSelect(true);
					}
				}
			}
			EdalFileChooser.this.switchversion(this.versionidx);
			if (EdalFileChooser.this.selectedFile != null) {
				EdalFileChooser.this.buildDetail(this.detailpanelparent, EdalFileChooser.this.selectedFile, false);
			} else if (EdalFileChooser.this.filelist.size() > 0
					&& EdalFileChooser.this.filelist.get(EdalFileChooser.this.fileselectindex) != null) {
				EdalFileChooser.this.buildDetail(this.detailpanelparent,
						EdalFileChooser.this.filelist.get(EdalFileChooser.this.fileselectindex), false);
			}
		}
	}

	private class TableRowModelListener implements ListSelectionListener {
		private final JTable table;
		private final JPanel detailpanelparent;

		public TableRowModelListener(final JTable table, final JPanel detailpanelparent) {
			this.table = table;
			this.detailpanelparent = detailpanelparent;
		}

		public void valueChanged(final ListSelectionEvent e) {
			EdalFileChooser.this.isSelected = true;
			final boolean b = this.table.getSelectionModel().getValueIsAdjusting();
			if (!b) {
				if (this.table.getSelectedRow() > -1) {
					EdalFileChooser.this.selectrows = this.table.getSelectedRows();
					EdalFileChooser.this.fileselectindex = this.table.getSelectedRow();
					if (EdalFileChooser.this.selectrows.length == 1) {
						EdalFileChooser.this.buildDetail(this.detailpanelparent,
								EdalFileChooser.this.filelist.get(this.table.getSelectedRow()), true);
						try {
							EdalFileChooser.this.pathtext
									.setText(EdalFileChooser.this.filelist.get(this.table.getSelectedRow()).getPath());
						} catch (final RemoteException e1) {
							ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
							ErrorDialog.showError(e1);
						}
					} else {
						EdalFileChooser.this.disablemetadata();
					}

					if (EdalFileChooser.this.detailpanel != null) {
						if (!EdalFileChooser.this.detailpanel.isCollapsed()) {
							EdalFileChooser.this.detailpanel.setCollapsed(true);
							EdalFileChooser.this.detailpanel.setScrollOnExpand(true);
						}
					}

				}
			}
		}

	}

	private static final long serialVersionUID = 1L;
	private static final Font FONT = new Font("Courier New", Font.PLAIN, 12);
	private DefaultTableModel defaultModel = null;
	private ClientPrimaryDataDirectory rootDirectory = null;
	private JTable filemetatable = null;
	private final List<String> metadatatoshow = new ArrayList<String>();
	private final List<ClientPrimaryDataEntity> filelist = new ArrayList<ClientPrimaryDataEntity>();
	private final List<ClientPrimaryDataEntityVersion> versionlist = new ArrayList<ClientPrimaryDataEntityVersion>();
	private ClientPrimaryDataEntityVersion currentversion;
	private final Map<String, ClientPrimaryDataEntityVersion> currentversionmap = new HashMap<String, ClientPrimaryDataEntityVersion>();
	private final Map<String, JTextField> detailmap = new HashMap<String, JTextField>();
	private boolean bindui = false;
	private final JLabel pathlabel = new JLabel("Path:", JLabel.LEFT);
	protected final JTextField pathtext = new JTextField();
	protected ClientPrimaryDataEntity selectedFile = null;
	private final JTextField searchtext = new JTextField();
	protected ClientPrimaryDataDirectory currentdir;
	private DefaultMutableTreeNode currentnode;
	protected JButton okbutton;
	private int returnvalue = EdalFileChooser.CANCEL_OPTION;
	private int[] selectrows;
	private EdalAbstractFileFilter fileFilter = null;
	private int fileSelectionMode = EdalFileChooser.FILES_ONLY;
	private boolean savemodal = false;
	private int fileselectindex;
	private String username = null;
	private static final String CHANGEMETADATAMETHODNAME = "setMetaData";
	private static final String CHANGEPERMISSIONTHODNAME = "grantPermission";
	private JSplitPane dirshowpane;
	private JSplitPane tableshowpane;
	private JPanel detailpanelparent;
	private JScrollPane treepanelparent;
	private LailapsJXTaskPane versionpanel;
	private LailapsJXTaskPane detailpanel;
	private JTree tree;
	private JButton configbutton = new JButton("eDAL Configration");
	private JButton metadatabutton = new JButton("ChangeMetadata");
	private JButton permissionbutton = new JButton("ChangePermissions");
	private Color tablebackcolor = null;
	private XStatusBar statusbar = null;
	private boolean showconnbutton = false;

	private final JPanel centerPane = new JPanel();
	private boolean isSelected = false;
	/**
	 * Return value if cancel is chosen.
	 */
	public static final int CANCEL_OPTION = 1;

	/**
	 * Return value if approve (yes, ok) is chosen.
	 */
	public static final int APPROVE_OPTION = 0;

	/**
	 * Return value if an error occured.
	 */
	public static final int ERROR_OPTION = -1;

	/** Instruction to display only files. */
	public static final int FILES_ONLY = 0;

	/** Instruction to display only directories. */
	public static final int DIRECTORIES_ONLY = 1;
	/** Instruction to display both files and directories. */
	public static final int FILES_AND_DIRECTORIES = 2;
	/** the max length of description and date */
	private static final int TEXTLIMIT = 40;

	/** the max length of tooltips */
	private static final int TOOLTIPSLEN = 20;
	/** the max length of metadata abstract */
	private static final int METADATALEN = 80;
	private boolean multiSelectionEnabled = false;
	private final Map<String, String> tipsmap = new HashMap<String, String>();
	private int row;

	private int column;

	private ClientDataManager client = null;

	private final Action cancelAction = new AbstractAction(Const.CANCEL_BTN_STR) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(final ActionEvent e) {
			EdalFileChooser.this.selectedFile = null;
			EdalFileChooser.this.returnvalue = EdalFileChooser.CANCEL_OPTION;
			EdalFileChooser.this.dispose();
		}
	};

	private final Action searchAction = new AbstractAction(Const.SEARCH_BTN_STR) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(final ActionEvent e) {
			EdalFileChooser.this.search(EdalFileChooser.this.searchtext.getText().trim());
		}
	};

	private JXTaskPaneContainer metainfocontainer;

	/**
	 * Constructs a <code>EDALFileChooser</code> that is initialized with
	 * <code>owner</code> as the swing container owner(JDialog),
	 * <code>rootDirectory</code> as the root directory, and <code>client</code>
	 * as the rmi client datamanager. If any of the parameters are
	 * <code>null</code> this method will not initialize.
	 * 
	 * @param owner
	 *            the swing container owner
	 * @param client
	 *            the rmi client datamanager
	 */
	public EdalFileChooser(final Dialog owner, final ClientDataManager client) {
		super(owner);
		if (client != null) {
			this.client = client;
			try {
				this.rootDirectory = client.getRootDirectory();
			} catch (final RemoteException e1) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				ErrorDialog.showError(e1);
			} catch (final EdalAuthenticateException e1) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				ErrorDialog.showError(e1);
			}

			catch (final NotBoundException e1) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				ErrorDialog.showError(e1);
			} catch (final PrimaryDataDirectoryException e1) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				ErrorDialog.showError(e1);
			} catch (final EdalException e1) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				ErrorDialog.showError(e1);
			}
			this.username = client.getAuthentication().getName();
			this.statusbar = new XStatusBar(client.getServerAddress(), client.getRegistryPort(), this.username);
		}
	}

	/**
	 * Constructs a <code>EDALFileChooser</code> that is initialized with
	 * <code>owner</code> as the swing container owner(JFrame),
	 * <code>rootDirectory</code> as the root directory, and <code>client</code>
	 * as the rmi client datamanager. If any of the parameters are
	 * <code>null</code> this method will not initialize.
	 * 
	 * @param owner
	 *            the swing container owner
	 * @param client
	 *            the rmi client datamanager
	 */

	public EdalFileChooser(final Frame owner, final ClientDataManager client) {
		super(owner);
		if (client != null) {
			this.client = client;
			try {
				this.rootDirectory = client.getRootDirectory();
			} catch (final RemoteException e1) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				ErrorDialog.showError(e1);
			} catch (final EdalAuthenticateException e1) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				ErrorDialog.showError(e1);
			}

			catch (final NotBoundException e1) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				ErrorDialog.showError(e1);
			} catch (final PrimaryDataDirectoryException e1) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				ErrorDialog.showError(e1);
			} catch (final EdalException e1) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				ErrorDialog.showError(e1);
			}
			this.username = client.getAuthentication().getName();
			this.statusbar = new XStatusBar(client.getServerAddress(), client.getRegistryPort(), this.username);
		}
	}

	private DefaultMutableTreeNode addNodes(final DefaultMutableTreeNode curTop,
			final ClientPrimaryDataDirectory parentdir) {
		DefaultMutableTreeNode curDir = null;
		try {
			curDir = new EdalMutableTreeModel(new EdalNode(parentdir.getName(), parentdir.getPath()));
			if (curTop != null) {
				curTop.add(curDir);
			}

			if (parentdir.isDirectory()) {
				final List<ClientPrimaryDataEntity> dirlist = parentdir.listPrimaryDataEntities();
				final List<String> dirnamelist = new ArrayList<String>();
				final Map<String, ClientPrimaryDataEntity> dirnamemap = new HashMap<String, ClientPrimaryDataEntity>();

				if (dirlist != null) {
					for (final ClientPrimaryDataEntity dir : dirlist) {
						ClientPrimaryDataEntityVersion version = null;
						try {
							version = dir.getCurrentVersion();
							if (dir.isDirectory() && !version.isDeleted()) {
								dirnamelist.add(dir.getName());
								dirnamemap.put(dir.getName(), dir);
							}
						} catch (final Exception e) {
							// we don't have the permission to call
							// getCurrentVersion method
						}
					}
				}
				Collections.sort(dirnamelist, String.CASE_INSENSITIVE_ORDER);

				for (final String dirname : dirnamelist) {
					final DefaultMutableTreeNode child = new EdalMutableTreeModel(
							new EdalNode(dirname, ((ClientPrimaryDataDirectory) dirnamemap.get(dirname)).getPath()));
					curDir.add(child);
				}
			}
		} catch (final RemoteException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		} catch (final PrimaryDataDirectoryException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		}
		return curDir;
	}

	private void buidversionpanel(final LailapsJXTaskPane versionpanel,
			final ClientPrimaryDataEntityVersion[] versionarray) throws RemoteException {
		versionpanel.setJSplitPane(this.tableshowpane);
		versionpanel.setFriendpanel(this.detailpanel);
		this.detailpanel.setFriendpanel(versionpanel);
		versionpanel.setName("Version Information");
		((JComponent) versionpanel.getContentPane()).setBorder(BorderFactory.createEmptyBorder());
		versionpanel.setTitle("Version Information");
		if (!versionpanel.isCollapsed()) {
			versionpanel.setCollapsed(true);
			versionpanel.setScrollOnExpand(true);
		}

		final int iversionlen = versionarray.length;

		int versioncolumnlen = iversionlen / 2;

		if (versioncolumnlen * 2 < iversionlen) {
			versioncolumnlen++;
		}

		final List<JVersionLabel> versionbuttonlist = new ArrayList<JVersionLabel>();

		versionpanel.removeAll();
		versionpanel.setLayout(new MigLayout("", "[45%!][10%!][45%!]", "[]0"));

		for (int i = 0; i < versioncolumnlen; i++) {
			String strdate = null;
			final SimpleDateFormat dataformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			if (versionarray[i * 2].getRevisionDate() != null) {
				strdate = dataformat.format(versionarray[i * 2].getRevisionDate().getTime());
			}
			final JVersionLabel label1 = new JVersionLabel("Version " + versionarray[i * 2].getRevision() + " : "
					+ strdate + (versionarray[i * 2].isDeleted() ? " - deleted." : ""));
			label1.setVerticalAlignment(SwingConstants.CENTER);
			label1.setFont(EdalFileChooser.FONT);
			versionbuttonlist.add(label1);
			label1.addMouseListener(new LinkMouseListener(versionbuttonlist, i * 2, this.detailpanelparent));

			versionpanel.add(label1, "cell 0 " + i + " 1 1");
			this.versionlist.add(versionarray[i * 2]);

			if (i * 2 + 1 < versionarray.length) {
				strdate = dataformat.format(versionarray[i * 2 + 1].getRevisionDate().getTime());
				final JVersionLabel label2 = new JVersionLabel("Version " + versionarray[i * 2 + 1].getRevision()
						+ " : " + strdate + (versionarray[i * 2 + 1].isDeleted() ? " - deleted." : ""));
				label2.setVerticalAlignment(SwingConstants.CENTER);
				label2.setFont(EdalFileChooser.FONT);
				label2.addMouseListener(new LinkMouseListener(versionbuttonlist, i * 2 + 1, this.detailpanelparent));
				versionpanel.add(label2, "cell 2 " + i + " 1 1");
				this.versionlist.add(versionarray[i * 2 + 1]);
				versionbuttonlist.add(label2);
			}

		}

		if (versionbuttonlist.size() > 0) {
			versionbuttonlist.get(versionbuttonlist.size() - 1).setSelect(true);
			String strdate = null;
			final SimpleDateFormat dataformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			if (versionlist.get(versionlist.size() - 1).getRevisionDate() != null) {
				strdate = dataformat.format(versionlist.get(versionlist.size() - 1).getRevisionDate().getTime());
			}
			this.versionpanel.setTitle("Version " + versionlist.get(versionlist.size() - 1).getRevision() + " :  "
					+ strdate + (versionlist.get(versionlist.size() - 1).isDeleted() ? " - deleted." : ""));
		}
	}

	private void buildDetail(final JPanel detailpanelparent, final ClientPrimaryDataEntity dataentry,
			final boolean refreshversion) {
		this.showmetabutton(dataentry);
		this.showpermissionbutton(dataentry);

		final List<String> columnlist = new ArrayList<String>();

		for (final EnumDublinCoreElements elem : EnumDublinCoreElements.values()) {
			try {
				final String dataName = elem.toString();
				columnlist.add(dataName);
			} catch (final UnsupportedOperationException e) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
				ErrorDialog.showError(e);
			}
		}

		final Object[] columnNames = columnlist.toArray(new String[0]);

		final int ilen = columnNames.length;

		int columnlen = ilen / 2;

		if (columnlen * 2 < ilen) {
			columnlen++;
		}

		if (!this.bindui) {
			this.metainfocontainer = new JXTaskPaneContainer();
			this.metainfocontainer.setBackground(new Color(238, 238, 238));

			this.detailpanel = new LailapsJXTaskPane();
			this.detailpanel.setBackground(new Color(238, 238, 238));
			this.detailpanel.setJSplitPane(this.tableshowpane);

			this.detailpanel.setName("Metadata Information");
			((JComponent) detailpanel.getContentPane()).setBorder(BorderFactory.createEmptyBorder());
			String metaabstract;
			try {
				metaabstract = dataentry.getMetaData().toString();
				if (metaabstract.length() > EdalFileChooser.METADATALEN) {
					metaabstract = metaabstract.substring(0, EdalFileChooser.METADATALEN) + "...";
				}
				this.detailpanel.setTitle("Metadata : " + metaabstract);
			} catch (final RemoteException e1) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				ErrorDialog.showError(e1);
			}

			if (!this.detailpanel.isCollapsed()) {
				this.detailpanel.setCollapsed(true);
				this.detailpanel.setScrollOnExpand(true);
			}

			this.detailpanel.removeAll();
			this.detailpanel.setLayout(new MigLayout("", "[80][50%][80][50%]", "[]0"));

			for (int i = 0; i < columnlen; i++) {
				final JLabel label1 = new JLinkLabel(columnNames[i * 2].toString() + ":", SwingConstants.LEFT);
				label1.setVerticalAlignment(SwingConstants.CENTER);
				label1.setFont(EdalFileChooser.FONT);
				label1.setToolTipText(
						"<html>" + MetaDescription.getDescription(columnNames[i * 2].toString()) + "</html>");
				this.detailpanel.add(label1, "cell 0 " + i + " 1 1");

				final JTextField text1 = new JTextField();
				text1.setFont(EdalFileChooser.FONT);
				this.detailpanel.add(text1, "cell 1 " + i + " 1 1,growx");
				this.detailmap.put(columnNames[i * 2].toString(), text1);

				if (i * 2 + 1 < columnNames.length) {
					final JLabel label2 = new JLinkLabel(columnNames[i * 2 + 1].toString() + ":", SwingConstants.LEFT);
					label2.setVerticalAlignment(SwingConstants.CENTER);
					label2.setFont(EdalFileChooser.FONT);
					label2.setToolTipText(
							"<html>" + MetaDescription.getDescription(columnNames[i * 2 + 1].toString()) + "</html>");
					this.detailpanel.add(label2, "cell 2 " + i + " 1 1");

					final JTextField text2 = new JTextField();
					text2.setFont(EdalFileChooser.FONT);
					this.detailpanel.add(text2, "cell 3 " + i + " 1 1,growx");
					this.detailmap.put(columnNames[i * 2 + 1].toString(), text2);
				}

			}
			this.metainfocontainer.add(this.detailpanel);
			SortedSet<ClientPrimaryDataEntityVersion> versions;
			try {
				versions = dataentry.getVersions();
				if (versions != null) {
					final ClientPrimaryDataEntityVersion[] versionarray = versions
							.toArray(new ClientPrimaryDataEntityVersion[0]);
					this.versionpanel = new LailapsJXTaskPane();
					// versionpanel.setBackground(new Color(238,238,238));
					this.buidversionpanel(this.versionpanel, versionarray);

					this.metainfocontainer.add(this.versionpanel);
				}

			} catch (final RemoteException e) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
				ErrorDialog.showError(e);
			}
			detailpanelparent.add(new JScrollPane(this.metainfocontainer), BorderLayout.CENTER);

			this.bindui = true;
		}
		String metaabstract;
		try {
			metaabstract = dataentry.getMetaData().toString();
			if (metaabstract.length() > EdalFileChooser.METADATALEN) {
				metaabstract = metaabstract.substring(0, EdalFileChooser.METADATALEN) + "...";
			}
			this.detailpanel.setTitle("Metadata : " + metaabstract);
		} catch (final RemoteException e1) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
			ErrorDialog.showError(e1);
		}
		// bind event to ui
		MetaData filemetadata;
		try {
			if (this.currentversionmap.containsKey(dataentry.getPath())) {
				filemetadata = this.getEntityVersionMetaData(dataentry,
						this.currentversionmap.get(dataentry.getPath()));
			} else {
				filemetadata = dataentry.getCurrentVersion().getMetaData();
			}
			for (final EnumDublinCoreElements elem : EnumDublinCoreElements.values()) {
				final String dataName = elem.toString();
				try {
					final UntypedData data = filemetadata.getElementValue(elem);

					if (this.detailmap.containsKey(dataName)) {
						final MetadataViewer viewer = ViewerContainer
								.getViewer(EnumDublinCoreElements.valueOf(dataName));
						if (viewer != null) {
							for (final MouseListener al : this.detailmap.get(dataName).getMouseListeners()) {
								this.detailmap.get(dataName).removeMouseListener(al);
							}
							this.detailmap.get(dataName).addMouseListener(new MouseAdapter() {
								@Override
								public void mouseClicked(final MouseEvent e) {
									if (e.getClickCount() == 2) {
										viewer.setValue(data);
										viewer.setTitle(dataName);
										viewer.showOpenDialog();
									}
								}
							});
							continue;
						}
						this.detailmap.get(dataName).setEditable(false);
					}
				} catch (final MetaDataException e) {
					ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
					ErrorDialog.showError(e);
				}
			}
		} catch (final RemoteException e1) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
			ErrorDialog.showError(e1);
		}
		// bind value to ui

		try {
			if (this.currentversionmap.containsKey(dataentry.getPath())) {
				filemetadata = this.getEntityVersionMetaData(dataentry,
						this.currentversionmap.get(dataentry.getPath()));
			} else {
				filemetadata = dataentry.getCurrentVersion().getMetaData();
			}

			for (final EnumDublinCoreElements elem : EnumDublinCoreElements.values()) {
				final String dataName = elem.toString();
				try {
					final UntypedData data = filemetadata.getElementValue(elem);
					final String value = data.toString();
					if (this.detailmap.containsKey(dataName)) {
						this.detailmap.get(dataName).setText(value);
						this.detailmap.get(dataName).setCaretPosition(0);
						if (value.trim().length() > 0) {
							this.detailmap.get(dataName).setToolTipText(this.formattooltips(value));
						}
					}
				} catch (final MetaDataException e) {
					ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
					ErrorDialog.showError(e);
				}
			}
		} catch (final RemoteException e1) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
			ErrorDialog.showError(e1);
		}

		this.selectedFile = dataentry;

		if (refreshversion) {
			SortedSet<ClientPrimaryDataEntityVersion> versions;
			try {
				versions = dataentry.getVersions();
				this.versionlist.clear();
				if (versions != null) {
					final ClientPrimaryDataEntityVersion[] versionarray = versions
							.toArray(new ClientPrimaryDataEntityVersion[0]);
					this.buidversionpanel(this.versionpanel, versionarray);
				}

			} catch (final RemoteException e) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
				ErrorDialog.showError(e);
			}
		}
	}

	private EdalTableModel buildtable(final List<ClientPrimaryDataEntity> dirlist) {
		this.tipsmap.clear();
		this.row = 0;
		this.column = 0;

		final List<String> columnlist = new ArrayList<String>();
		columnlist.add("TITLE");

		for (final EnumDublinCoreElements elem : EnumDublinCoreElements.values()) {
			final String dataName = elem.toString();
			if (this.metadatatoshow.contains(dataName)) {
				columnlist.add(dataName);
			}
		}

		final Object[] columnNames = columnlist.toArray(new String[0]);

		final List<List<String>> datalist = new ArrayList<List<String>>();

		if (dirlist != null) {
			Collections.sort(dirlist);
			for (final ClientPrimaryDataEntity dataentry : dirlist) {
				try {
					try {
						dataentry.getCurrentVersion();
					} catch (final Exception e) {
						continue;
					}
					if (dataentry.getCurrentVersion().isDeleted()) {
						continue;
					}
				} catch (final RemoteException e1) {
					ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
					ErrorDialog.showError(e1);
				}

				if (this.fileSelectionMode == EdalFileChooser.FILES_AND_DIRECTORIES) {
					try {
						if (!dataentry.isDirectory() && (this.fileFilter == null
								|| this.fileFilter.accept((ClientPrimaryDataFile) dataentry))) {
							final List<String> rowlist = new ArrayList<String>();

							rowlist.add(dataentry.getName());

							final MetaData filemetadata = dataentry.getCurrentVersion().getMetaData();
							for (final EnumDublinCoreElements elem : EnumDublinCoreElements.values()) {
								final String dataName = elem.toString();
								try {
									if (this.metadatatoshow.contains(dataName)) {
										if ("DATE".equals(dataName)) {
											String tvalue = null;
											final DateEvents tdataevents = (DateEvents) filemetadata
													.getElementValue(elem);
											final Set<EdalDate> dataset = tdataevents.getSet();
											for (final EdalDate edaldate : dataset) {
												if ("UPDATED".equals(edaldate.getEvent())) {
													tvalue = edaldate.toString();
													break;
												}
											}
											if (tvalue == null) {
												for (final EdalDate edaldate : dataset) {
													if ("CREATED".equals(edaldate.getEvent())) {
														tvalue = edaldate.toString();
														break;
													}
												}
											}
											rowlist.add(tvalue);
										} else {
											final String value = filemetadata.getElementValue(elem).toString();
											if (value != null && value.length() > EdalFileChooser.TEXTLIMIT) {
												final int itemprow = datalist.size() + 1;
												final int itempcol = rowlist.size() + 1;
												this.tipsmap.put(itemprow + "," + itempcol, value);
												rowlist.add(value.substring(0, EdalFileChooser.TEXTLIMIT) + "...");
											} else {
												rowlist.add(value);
											}
										}

									}
								} catch (final MetaDataException e) {
									ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
									ErrorDialog.showError(e);
								}
							}

							datalist.add(rowlist);

							this.filelist.add(dataentry);
						} else if (dataentry.isDirectory()) {
							final List<String> rowlist = new ArrayList<String>();

							rowlist.add(dataentry.getName());

							final MetaData filemetadata = dataentry.getCurrentVersion().getMetaData();
							for (final EnumDublinCoreElements elem : EnumDublinCoreElements.values()) {
								final String dataName = elem.toString();
								try {
									if (this.metadatatoshow.contains(dataName)) {
										if ("DATE".equals(dataName)) {
											String tvalue = null;
											final DateEvents tdataevents = (DateEvents) filemetadata
													.getElementValue(elem);
											final Set<EdalDate> dataset = tdataevents.getSet();
											for (final EdalDate edaldate : dataset) {
												if ("UPDATED".equals(edaldate.getEvent())) {
													tvalue = edaldate.toString();
													break;
												}
											}
											if (tvalue == null) {
												for (final EdalDate edaldate : dataset) {
													if ("CREATED".equals(edaldate.getEvent())) {
														tvalue = edaldate.toString();
														break;
													}
												}
											}
											rowlist.add(tvalue);
										} else {
											final String value = filemetadata.getElementValue(elem).toString();
											if (value != null && value.length() > EdalFileChooser.TEXTLIMIT) {
												final int itemprow = datalist.size() + 1;
												final int itempcol = rowlist.size() + 1;
												this.tipsmap.put(itemprow + "," + itempcol, value);
												rowlist.add(value.substring(0, EdalFileChooser.TEXTLIMIT) + "...");
											} else {
												rowlist.add(value);
											}
										}

									}
								} catch (final MetaDataException e) {
									ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
									ErrorDialog.showError(e);
								}
							}

							datalist.add(rowlist);

							this.filelist.add(dataentry);
						}
					} catch (final RemoteException e) {
						ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
						ErrorDialog.showError(e);
					}
				} else if (this.fileSelectionMode == EdalFileChooser.DIRECTORIES_ONLY) {
					try {
						if (dataentry.isDirectory()) {
							final List<String> rowlist = new ArrayList<String>();

							rowlist.add(dataentry.getName());

							final MetaData filemetadata = dataentry.getCurrentVersion().getMetaData();
							for (final EnumDublinCoreElements elem : EnumDublinCoreElements.values()) {
								final String dataName = elem.toString();
								try {
									if (this.metadatatoshow.contains(dataName)) {
										if ("DATE".equals(dataName)) {
											String tvalue = null;
											final DateEvents tdataevents = (DateEvents) filemetadata
													.getElementValue(elem);
											final Set<EdalDate> dataset = tdataevents.getSet();
											for (final EdalDate edaldate : dataset) {
												if ("UPDATED".equals(edaldate.getEvent())) {
													tvalue = edaldate.toString();
													break;
												}
											}
											if (tvalue == null) {
												for (final EdalDate edaldate : dataset) {
													if ("CREATED".equals(edaldate.getEvent())) {
														tvalue = edaldate.toString();
														break;
													}
												}
											}
											rowlist.add(tvalue);
										} else {
											final String value = filemetadata.getElementValue(elem).toString();
											if (value != null && value.length() > EdalFileChooser.TEXTLIMIT) {
												final int itemprow = datalist.size() + 1;
												final int itempcol = rowlist.size() + 1;
												this.tipsmap.put(itemprow + "," + itempcol, value);
												rowlist.add(value.substring(0, EdalFileChooser.TEXTLIMIT) + "...");
											} else {
												rowlist.add(value);
											}
										}

									}
								} catch (final MetaDataException e) {
									ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
									ErrorDialog.showError(e);
								}
							}

							datalist.add(rowlist);

							this.filelist.add(dataentry);
						}
					} catch (final RemoteException e) {
						ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
						ErrorDialog.showError(e);
					}
				} else if (this.fileSelectionMode == EdalFileChooser.FILES_ONLY) {
					try {
						if (!dataentry.isDirectory() && (this.fileFilter == null
								|| this.fileFilter.accept((ClientPrimaryDataFile) dataentry))) {
							final List<String> rowlist = new ArrayList<String>();

							rowlist.add(dataentry.getName());

							final MetaData filemetadata = dataentry.getCurrentVersion().getMetaData();
							for (final EnumDublinCoreElements elem : EnumDublinCoreElements.values()) {
								try {
									final String dataName = elem.toString();
									if (this.metadatatoshow.contains(dataName)) {
										final String value = filemetadata.getElementValue(elem).toString();
										if ("DATE".equals(dataName)) {
											String tvalue = null;
											final DateEvents tdataevents = (DateEvents) filemetadata
													.getElementValue(elem);
											final Set<EdalDate> dataset = tdataevents.getSet();
											for (final EdalDate edaldate : dataset) {
												if ("UPDATED".equals(edaldate.getEvent())) {
													tvalue = edaldate.toString();
													break;
												}
											}
											if (tvalue == null) {
												for (final EdalDate edaldate : dataset) {
													if ("CREATED".equals(edaldate.getEvent())) {
														tvalue = edaldate.toString();
														break;
													}
												}
											}
											rowlist.add(tvalue);
										} else {
											if (value != null && value.length() > EdalFileChooser.TEXTLIMIT) {
												final int itemprow = datalist.size() + 1;
												final int itempcol = rowlist.size() + 1;
												this.tipsmap.put(itemprow + "," + itempcol, value);
												rowlist.add(value.substring(0, EdalFileChooser.TEXTLIMIT) + "...");
											} else {
												rowlist.add(value);
											}
										}
									}
								} catch (final MetaDataException e) {
									ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
									ErrorDialog.showError(e);
								}
							}

							datalist.add(rowlist);

							this.filelist.add(dataentry);
						}
					} catch (final RemoteException e) {
						ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
						ErrorDialog.showError(e);
					}
				}
			}
		}

		final Object[][] rowData = new Object[datalist.size()][columnNames.length];
		for (int i = 0; i < datalist.size(); i++) {
			final List<String> rowlist = datalist.get(i);
			for (int j = 0; j < rowlist.size(); j++) {
				rowData[i][j] = rowlist.get(j);
			}
		}

		return new EdalTableModel(rowData, columnNames);

	}

	private void changemetadata(final ClientPrimaryDataEntity dataentry,
			final Map<EnumDublinCoreElements, UntypedData> metadatavalue, final boolean refreshversion) {
		if (metadatavalue != null) {
			try {
				final MetaData fileMetaData = dataentry.getMetaData().clone();
				final Iterator<Map.Entry<EnumDublinCoreElements, UntypedData>> iter = metadatavalue.entrySet()
						.iterator();
				while (iter.hasNext()) {
					final Map.Entry<EnumDublinCoreElements, UntypedData> entry = iter.next();
					final EnumDublinCoreElements key = entry.getKey();
					final UntypedData val = entry.getValue();
					fileMetaData.setElementValue(key, val);

				}
				dataentry.setMetaData(fileMetaData);
				this.buildDetail(this.detailpanelparent, dataentry, refreshversion);

			} catch (final Exception re) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(re));
				ErrorDialog.showError(re);
			}
		}
	}

	private void checkpanelRightClick(final MouseEvent e) {
		if (e.isPopupTrigger()) {

			final JPanel panel = (JPanel) e.getComponent();

			final JMenuItem metadatamenu = new JMenuItem(Const.CHANGEMETA_BTN_STR);

			JPopupMenu popupMenu1;
			popupMenu1 = new JPopupMenu();

			if (this.filelist.size() > 0 && this.filelist.get(this.fileselectindex) != null) {
				if (!PrincipalUtil.checkPermission(this.filelist.get(this.fileselectindex), this.username,
						EdalFileChooser.CHANGEMETADATAMETHODNAME)) {
					return;
				}
				popupMenu1.add(metadatamenu);
			} else if (this.currentdir != null) {
				if (!PrincipalUtil.checkPermission(this.currentdir, this.username,
						EdalFileChooser.CHANGEMETADATAMETHODNAME)) {
					return;
				}
				popupMenu1.add(metadatamenu);
			}

			metadatamenu.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					if (EdalFileChooser.this.filelist.size() > 0
							&& EdalFileChooser.this.filelist.get(EdalFileChooser.this.fileselectindex) != null) {
						final MetaDataSaveDialog metadlg = new MetaDataSaveDialog(
								EdalFileChooser.this.filelist.get(EdalFileChooser.this.fileselectindex));
						final int returnVal = metadlg.showOpenDialog();
						if (returnVal == MetaDataSaveDialog.APPROVE_OPTION) {
							final Map<EnumDublinCoreElements, UntypedData> metadatavalue = metadlg.getMetaDataValues();
							EdalFileChooser.this.changemetadata(
									EdalFileChooser.this.filelist.get(EdalFileChooser.this.fileselectindex),
									metadatavalue, true);
						}
					} else {
						final MetaDataSaveDialog metadlg = new MetaDataSaveDialog(EdalFileChooser.this.currentdir);
						final int returnVal = metadlg.showOpenDialog();
						if (returnVal == MetaDataSaveDialog.APPROVE_OPTION) {
							final Map<EnumDublinCoreElements, UntypedData> metadatavalue = metadlg.getMetaDataValues();
							EdalFileChooser.this.changemetadata(EdalFileChooser.this.currentdir, metadatavalue, false);
						}
					}

				}
			});

			popupMenu1.show(panel, e.getX(), e.getY());
		}
	}

	private void checktableRightClick(final MouseEvent evt, final JTree tree) {
		if (evt.isPopupTrigger()) {
			final int rightclickrow = evt.getY() / this.filemetatable.getRowHeight();

			final JMenuItem del = new JMenuItem(Const.DEL_BTN_STR);
			final JMenuItem rename = new JMenuItem(Const.RENAME_BTN_STR);
			final JMenuItem permissionmenu = new JMenuItem(Const.CHANGEPERMISION_BTN_STR);

			JPopupMenu popupMenu;
			popupMenu = new JPopupMenu();

			if (this.savemodal) {
				popupMenu.add(del);
				popupMenu.add(rename);
			}

			if (this.filelist.size() > 0 && PrincipalUtil.checkPermission(this.currentdir, this.username,
					EdalFileChooser.CHANGEPERMISSIONTHODNAME) && this.filelist.get(rightclickrow) != null) {
				popupMenu.add(permissionmenu);
			}

			final JMenuItem metadatamenu = new JMenuItem("Change Metadata");

			if (this.filelist.size() > 0 && this.filelist.get(this.fileselectindex) != null) {
				popupMenu.add(metadatamenu);
				if (!PrincipalUtil.checkPermission(this.filelist.get(this.fileselectindex), this.username,
						EdalFileChooser.CHANGEMETADATAMETHODNAME)) {
					metadatamenu.setEnabled(false);
				}

			} else if (this.currentdir != null) {
				popupMenu.add(metadatamenu);
				if (!PrincipalUtil.checkPermission(this.currentdir, this.username,
						EdalFileChooser.CHANGEMETADATAMETHODNAME)) {
					metadatamenu.setEnabled(false);
				}
			}

			metadatamenu.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					if (EdalFileChooser.this.filelist.size() > 0
							&& EdalFileChooser.this.filelist.get(EdalFileChooser.this.fileselectindex) != null) {
						final MetaDataSaveDialog metadlg = new MetaDataSaveDialog(
								EdalFileChooser.this.filelist.get(EdalFileChooser.this.fileselectindex));
						final int returnVal = metadlg.showOpenDialog();
						if (returnVal == MetaDataSaveDialog.APPROVE_OPTION) {
							final Map<EnumDublinCoreElements, UntypedData> metadatavalue = metadlg.getMetaDataValues();
							EdalFileChooser.this.changemetadata(
									EdalFileChooser.this.filelist.get(EdalFileChooser.this.fileselectindex),
									metadatavalue, true);
						}
					} else {
						final MetaDataSaveDialog metadlg = new MetaDataSaveDialog(EdalFileChooser.this.currentdir);
						final int returnVal = metadlg.showOpenDialog();
						if (returnVal == MetaDataSaveDialog.APPROVE_OPTION) {
							final Map<EnumDublinCoreElements, UntypedData> metadatavalue = metadlg.getMetaDataValues();
							EdalFileChooser.this.changemetadata(EdalFileChooser.this.currentdir, metadatavalue, false);
						}
					}

				}
			});

			del.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					final String[] buttons = { "Yes", "No" };
					final int rc = JOptionPane.showOptionDialog(null, "Do you want to delete?", Const.EDAL_TITLE_STR,
							JOptionPane.INFORMATION_MESSAGE, 0, null, buttons, buttons[1]);
					if (rc == 0) {
						// delete current Directory or File
						try {
							if (EdalFileChooser.this.filelist.size() > 0) {
								boolean shouldfresh = false;
								if (EdalFileChooser.this.filelist.get(rightclickrow).isDirectory()) {
									shouldfresh = true;
								}
								EdalFileChooser.this.filelist.get(rightclickrow).delete();
								EdalFileChooser.this.filelist.remove(rightclickrow);

								List<ClientPrimaryDataEntity> dirlist = null;
								try {
									dirlist = EdalFileChooser.this.currentdir.listPrimaryDataEntities();
								} catch (final PrimaryDataDirectoryException e1) {
									ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
									ErrorDialog.showError(e1);
								}
								if (dirlist != null) {
									EdalFileChooser.this.defaultModel = EdalFileChooser.this.buildtable(dirlist);
									EdalFileChooser.this.filemetatable.setModel(EdalFileChooser.this.defaultModel);
									EdalFileChooser.this.defaultModel.fireTableDataChanged();
									UiUtil.fitTableColumns(EdalFileChooser.this.filemetatable, 631);
								}
								if (shouldfresh) {
									currentnode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
									EdalFileChooser.this.refreshtreenode(tree);
								}
							}
						} catch (final RemoteException e1) {
							ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
							ErrorDialog.showError(e1);
						} catch (final PrimaryDataEntityVersionException e1) {
							ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
							ErrorDialog.showError(e1);
						} catch (final PrimaryDataDirectoryException e1) {
							ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
							ErrorDialog.showError(e1);
						}
					}
				}
			});

			rename.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					String s = null;
					try {
						s = JOptionPane.showInputDialog(null, "Please input directoy name:",
								EdalFileChooser.this.filelist.get(rightclickrow).getName());
					} catch (final RemoteException e2) {
						ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e2));
						ErrorDialog.showError(e2);
					}
					if (s != null && s.trim().length() > 0) {
						// rename current directory
						try {
							try {
								if (EdalFileChooser.this.currentdir.exist(s.trim())) {
									JOptionPane.showMessageDialog(null,
											"Error:[" + s.trim()
													+ "] already exists!\nPlease specify a different name.",
											Const.EDAL_TITLE_STR, JOptionPane.ERROR_MESSAGE);
								} else {
									try {
										EdalFileChooser.this.filelist.get(rightclickrow).rename(s.trim());
										List<ClientPrimaryDataEntity> dirlist = null;
										try {
											dirlist = EdalFileChooser.this.currentdir.listPrimaryDataEntities();
										} catch (final PrimaryDataDirectoryException e1) {
											ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
											ErrorDialog.showError(e1);
										}
										if (dirlist != null) {
											EdalFileChooser.this.defaultModel = EdalFileChooser.this
													.buildtable(dirlist);
											EdalFileChooser.this.filemetatable
													.setModel(EdalFileChooser.this.defaultModel);
											EdalFileChooser.this.defaultModel.fireTableDataChanged();
											UiUtil.fitTableColumns(EdalFileChooser.this.filemetatable, 631);
										}
										if (EdalFileChooser.this.filelist.get(rightclickrow).isDirectory()) {
											EdalFileChooser.this.refreshtreenode(tree);
										}
									} catch (final PrimaryDataEntityVersionException e1) {
										ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
										ErrorDialog.showError(e1);
									}
								}
							} catch (final PrimaryDataDirectoryException e1) {
								ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
								ErrorDialog.showError(e1);
							}
						} catch (final RemoteException e1) {
							ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
							ErrorDialog.showError(e1);
						}
					}
				}
			});

			permissionmenu.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					final PermissionDialog permission = new PermissionDialog(
							EdalFileChooser.this.filelist.get(rightclickrow), EdalFileChooser.this.client);
					permission.showOpenDialog();
				}
			});

			popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}

	private void checktreeRightClick(final MouseEvent e) {
		if (e.isPopupTrigger()) {
			final JTree tree = (JTree) e.getComponent();
			final TreePath path = tree.getPathForLocation(e.getX(), e.getY());
			tree.setSelectionPath(path);

			final JMenuItem del = new JMenuItem(Const.DELDIR_BTN_STR);
			final JMenuItem rename = new JMenuItem(Const.RENAMEDIR_BTN_STR);
			final JMenuItem newFile = new JMenuItem(Const.NEWDIR_BTN_STR);
			final JMenuItem permissionmenu = new JMenuItem(Const.CHANGEPERMISION_BTN_STR);
			final JMenuItem metadatamenu = new JMenuItem(Const.CHANGEMETA_BTN_STR);

			JPopupMenu popupMenu1;
			popupMenu1 = new JPopupMenu();

			if (this.savemodal) {
				if (this.currentnode != null) {
					popupMenu1.add(newFile);
				}

				if (path != null && path.getParentPath() != null) {
					// not root currentnode
					popupMenu1.add(del);
					popupMenu1.add(rename);
				}

			}

			if (this.currentnode != null) {
				if (PrincipalUtil.checkPermission(this.currentdir, this.username,
						EdalFileChooser.CHANGEPERMISSIONTHODNAME)) {
					popupMenu1.add(permissionmenu);
				}
			}

			permissionmenu.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					final PermissionDialog permission = new PermissionDialog(EdalFileChooser.this.currentdir,
							EdalFileChooser.this.client);
					permission.showOpenDialog();
				}
			});

			if (this.currentdir != null) {
				popupMenu1.add(metadatamenu);
				if (!PrincipalUtil.checkPermission(this.currentdir, this.username,
						EdalFileChooser.CHANGEMETADATAMETHODNAME)) {
					metadatamenu.setEnabled(false);
				}
			}

			metadatamenu.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					final MetaDataSaveDialog metadlg = new MetaDataSaveDialog(EdalFileChooser.this.currentdir);
					final int returnVal = metadlg.showOpenDialog();
					if (returnVal == MetaDataSaveDialog.APPROVE_OPTION) {
						final Map<EnumDublinCoreElements, UntypedData> metadatavalue = metadlg.getMetaDataValues();
						EdalFileChooser.this.changemetadata(EdalFileChooser.this.currentdir, metadatavalue, false);
					}
				}
			});

			del.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					final String[] buttons = { "Yes", "No" };
					final int rc = JOptionPane.showOptionDialog(null, "Do you want to delete current directory?",
							Const.EDAL_TITLE_STR, JOptionPane.INFORMATION_MESSAGE, 0, null, buttons, buttons[1]);
					if (rc == 0) {
						// delete current directory
						try {
							EdalFileChooser.this.currentdir.delete();
							((DefaultMutableTreeNode) EdalFileChooser.this.currentnode.getParent())
									.remove(EdalFileChooser.this.currentnode);
							EdalFileChooser.this.cleartable();
							tree.updateUI();
						} catch (final RemoteException e1) {
							ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
							ErrorDialog.showError(e1);
						} catch (final PrimaryDataEntityVersionException e1) {
							ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
							ErrorDialog.showError(e1);
						} catch (final PrimaryDataDirectoryException e1) {
							ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
							ErrorDialog.showError(e1);
						}
					}
				}
			});

			newFile.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					String s;
					s = JOptionPane.showInputDialog("Please input directoy name:");
					if (s != null && s.trim().length() > 0) {
						try {
							try {
								if (EdalFileChooser.this.currentdir.exist(s.trim())) {
									JOptionPane.showMessageDialog(null,
											"Error:Directory [" + s.trim()
													+ "] already exists!\nPlease specify a different name.",
											Const.EDAL_TITLE_STR, JOptionPane.ERROR_MESSAGE);
								} else if (s.indexOf("\\") >= 0 || s.indexOf("/") >= 0 || s.indexOf(":") >= 0
										|| s.indexOf("?") >= 0 || s.indexOf(">") >= 0 || s.indexOf("<") >= 0
										|| s.indexOf("|") >= 0 || s.indexOf("\"") >= 0 || s.indexOf("\\") >= 0) {
									JOptionPane.showMessageDialog(null,
											"Error:Directory name cannot contain any of the following characters:\\ / : * ? \" < > | !\nPlease specify a different name.",
											Const.EDAL_TITLE_STR, JOptionPane.ERROR_MESSAGE);
								} else {
									try {
										if (isexistdeleteversion(EdalFileChooser.this.currentdir, s.trim())) {
											JOptionPane.showMessageDialog(null,
													"Error:A deleted directory [" + s.trim()
															+ "] already exists!\nPlease specify a different name.",
													Const.EDAL_TITLE_STR, JOptionPane.ERROR_MESSAGE);
											return;
										}
										final ClientPrimaryDataDirectory childdir = EdalFileChooser.this.currentdir
												.createPrimaryDataDirectory(s.trim());
										// show modify metadata dialog
										Map<EnumDublinCoreElements, UntypedData> metadatavalue;
										final MetaDataSaveDialog metadlg = new MetaDataSaveDialog(childdir);
										final int returnVal = metadlg.showOpenDialog();
										if (returnVal == MetaDataSaveDialog.APPROVE_OPTION) {
											metadatavalue = metadlg.getMetaDataValues();
										} else {
											metadatavalue = null;
										}

										if (metadatavalue != null) {
											try {
												final MetaData fileMetaData = childdir.getMetaData().clone();
												final Iterator<Map.Entry<EnumDublinCoreElements, UntypedData>> iter = metadatavalue
														.entrySet().iterator();
												while (iter.hasNext()) {
													final Map.Entry<EnumDublinCoreElements, UntypedData> entry = iter
															.next();
													final EnumDublinCoreElements key = entry.getKey();
													final UntypedData val = entry.getValue();
													fileMetaData.setElementValue(key, val);
												}
												childdir.setMetaData(fileMetaData);
											} catch (final Exception re) {
												ClientDataManager.logger.error(StackTraceUtil.getStackTrace(re));
												ErrorDialog.showError(re);
											}
										}

										final DefaultMutableTreeNode newChild = new EdalMutableTreeModel(
												new EdalNode(s.trim(), childdir.getPath()));
										/*
										 * EdalFileChooser.this.currentnode.add(
										 * newChild); tree.updateUI();
										 * EdalFileChooser.this.refreshtable();
										 */
										EdalFileChooser.this.refreshtreenode(tree);

										// tree.expandPath(new
										// TreePath(newChild.getPath()));
										EdalFileChooser.this.refreshtable();
										EdalFileChooser.this.selectedFile = childdir;

										javax.swing.SwingUtilities.invokeLater(new Runnable() {
											public void run() {
												tree.expandPath(new TreePath(newChild.getPath()));
												scrollPathToVisible(new TreePath(newChild.getPath()));
												tree.setSelectionPath(new TreePath(newChild.getPath()));
											}
										});

									} catch (final java.security.AccessControlException se) {
										JOptionPane.showMessageDialog(null, "Can't create directory:" + se.getMessage(),
												Const.EDAL_TITLE_STR, JOptionPane.ERROR_MESSAGE);
									}

								}
							} catch (final PrimaryDataDirectoryException e1) {
								ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
								ErrorDialog.showError(e1);
							}
						} catch (final RemoteException e1) {
							ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
							ErrorDialog.showError(e1);
						}
					}
				}
			});

			rename.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					String s = null;
					try {
						s = JOptionPane.showInputDialog(null, "Please input directoy name:",
								EdalFileChooser.this.currentdir.getName());
					} catch (final RemoteException e2) {
						ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e2));
						ErrorDialog.showError(e2);
					}
					if (s != null && s.trim().length() > 0) {
						// rename current directory
						try {
							try {
								if (EdalFileChooser.this.currentdir.getParentDirectory().exist(s.trim())) {
									JOptionPane.showMessageDialog(null,
											"Error:Directory [" + s.trim()
													+ "] already exists!\nPlease specify a different name.",
											Const.EDAL_TITLE_STR, JOptionPane.ERROR_MESSAGE);
								} else if (s.indexOf("\\") >= 0 || s.indexOf("/") >= 0 || s.indexOf(":") >= 0
										|| s.indexOf("?") >= 0 || s.indexOf(">") >= 0 || s.indexOf("<") >= 0
										|| s.indexOf("|") >= 0 || s.indexOf("\"") >= 0 || s.indexOf("\\") >= 0) {
									JOptionPane.showMessageDialog(null,
											"Error:Directory name cannot contain any of the following characters:\\ / : * ? \" < > | !\nPlease specify a different name.",
											Const.EDAL_TITLE_STR, JOptionPane.ERROR_MESSAGE);
								} else {
									try {
										EdalFileChooser.this.currentdir.rename(s.trim());
										final EdalNode mynode = (EdalNode) EdalFileChooser.this.currentnode
												.getUserObject();
										mynode.setName(s.trim());
										mynode.setPath(EdalFileChooser.this.currentdir.getPath());
										EdalFileChooser.this.currentnode.setUserObject(mynode);
										tree.updateUI();
									} catch (final PrimaryDataEntityVersionException e1) {
										ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
										ErrorDialog.showError(e1);
									}
								}
							} catch (final PrimaryDataDirectoryException e1) {
								ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
								ErrorDialog.showError(e1);
							}
						} catch (final RemoteException e1) {
							ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
							ErrorDialog.showError(e1);
						}
					}
				}
			});

			popupMenu1.show(tree, e.getX(), e.getY());
		}
	}

	private void scrollPathToVisible(TreePath treePath) {
		if (treePath != null) {
			tree.makeVisible(treePath);
			tree.expandPath(treePath);

			Rectangle bounds = tree.getPathBounds(treePath);
			if (bounds != null) {
				bounds.x = 0;
				tree.scrollRectToVisible(bounds);
			}
		}
	}

	@SuppressWarnings("unused")
	private void cleanbuttonevent(final JButton button) {
		if (button != null) {
			for (final ActionListener al : button.getActionListeners()) {
				button.removeActionListener(al);
			}
		}
	}

	private void cleartable() {
		if (this.defaultModel != null) {
			final int irow = this.defaultModel.getRowCount();
			for (int i = 0; i < irow; i++) {
				this.defaultModel.removeRow(0);
			}
		}
		this.filelist.clear();
		this.selectedFile = null;
	}

	private void disablemetadata() {
		this.metadatabutton.setVisible(false);
		this.permissionbutton.setVisible(false);
		/*
		 * Component[] components = getComponents(metainfocontainer); for
		 * (Component component : components) { component.setEnabled(false); }
		 * metainfocontainer.setEnabled(false);
		 */
	}

	private String formattooltips(final String tooltips) {
		if (tooltips == null) {
			return "";
		}
		if (tooltips.length() <= EdalFileChooser.TOOLTIPSLEN) {
			return tooltips;
		} else {
			return tooltips.substring(0, EdalFileChooser.TOOLTIPSLEN) + "...";
		}
	}

	private MetaData getEntityVersionMetaData(final ClientPrimaryDataEntity dataentity,
			final ClientPrimaryDataEntityVersion version) {
		try {
			Iterator<ClientPrimaryDataEntityVersion> it;
			it = dataentity.getVersions().iterator();
			while (it.hasNext()) {
				final ClientPrimaryDataEntityVersion curr = it.next();
				if (curr.getRevision().longValue() == version.getRevision().longValue()) {
					return curr.getMetaData();
				}
			}
			return dataentity.getMetaData();
		} catch (final RemoteException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		}
		return null;
	}

	/**
	 * Returns the currently selected file filter.
	 * 
	 * @return the current file filter
	 * @see #setFileFilter
	 */

	public EdalAbstractFileFilter getFileFilter() {
		return this.fileFilter;
	}

	/**
	 * Returns the current file-selection mode. The default is
	 * <code>EDALFileChooser.FILES_ONLY</code>.
	 * 
	 * @return the type of files to be displayed, one of the following:
	 *         <ul>
	 *         <li>EDALFileChooser.FILES_ONLY
	 *         <li>EDALFileChooser.DIRECTORIES_ONLY
	 *         <li>EDALFileChooser.FILES_AND_DIRECTORIES
	 *         </ul>
	 * @see #setFileSelectionMode
	 */

	public int getFileSelectionMode() {
		return this.fileSelectionMode;
	}

	/**
	 * Returns the selected file. This can be set by a user action, such as
	 * selecting the file from a list in the UI.
	 * 
	 * @return the selected file
	 */

	public ClientPrimaryDataEntity getSelectedFile() {
		if (!this.isSelected) {
			return null;
		}
		if (this.fileSelectionMode == EdalFileChooser.DIRECTORIES_ONLY) {
			try {
				if (this.selectedFile.isDirectory()) {
					return this.selectedFile;
				}
			} catch (final RemoteException e) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
				ErrorDialog.showError(e);
			}
			return null;
		} else if (this.fileSelectionMode == EdalFileChooser.FILES_ONLY) {
			try {
				if (!this.selectedFile.isDirectory()) {
					return this.selectedFile;
				}
			} catch (final RemoteException e) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
				ErrorDialog.showError(e);
			}
			return null;
		} else {
			if (this.selectedFile != null) {
				return this.selectedFile;
			} else {
				return this.currentdir;
			}
		}
	}

	/**
	 * Returns a list of selected files if the file chooser is set to allow
	 * multiple selection.
	 * 
	 * @return the selected files
	 */

	public ClientPrimaryDataEntity[] getSelectedFiles() {
		if (this.selectrows != null && this.filelist.size() > 0) {
			if (this.fileSelectionMode == EdalFileChooser.DIRECTORIES_ONLY) {
				final ClientPrimaryDataEntity[] selectedFiles = new ClientPrimaryDataEntity[1];
				selectedFiles[0] = this.currentdir;
				return selectedFiles;
			} else if (this.fileSelectionMode == EdalFileChooser.FILES_ONLY) {
				final ClientPrimaryDataEntity[] selectedFiles = new ClientPrimaryDataEntity[this.selectrows.length];
				for (int i = 0; i < selectedFiles.length; i++) {
					selectedFiles[i] = this.filelist.get(this.selectrows[i]);
				}
				return selectedFiles;
			} else {
				final ClientPrimaryDataEntity[] selectedFiles = new ClientPrimaryDataEntity[this.selectrows.length];
				for (int i = 0; i < selectedFiles.length; i++) {
					selectedFiles[i] = this.filelist.get(this.selectrows[i]);
				}
				return selectedFiles;
			}

		} else {
			if (this.fileSelectionMode == EdalFileChooser.FILES_ONLY) {
				return new ClientPrimaryDataFile[0];
			} else {
				final ClientPrimaryDataEntity[] selectedFiles = new ClientPrimaryDataEntity[1];
				selectedFiles[0] = this.currentdir;
				return selectedFiles;
			}
		}
	}

	protected void init(final ClientPrimaryDataDirectory rootDirectory) {
		this.currentdir = rootDirectory;

		// JPanel maincontents = (JPanel) getContentPane();
		final JPanel maincontents = new JPanel();
		this.setContentPane(maincontents);

		maincontents.setLayout(new BorderLayout());

		maincontents.add(this.centerPane, BorderLayout.CENTER);
		maincontents.add(this.statusbar, BorderLayout.SOUTH);

		this.centerPane.removeAll();
		this.centerPane.setLayout(new MigLayout("insets 1 1 1 1", "[35!][8%!][9%!][36%!][5%!][11!][grow,fill][10][5]",
				"[30]0[grow,fill]0[20]0"));

		if (rootDirectory != null) {
			this.pathlabel.setVerticalAlignment(SwingConstants.CENTER);
			this.pathlabel.setFont(EdalFileChooser.FONT);
			this.centerPane.add(this.pathlabel, "cell 0 0 1 1");

			this.pathtext.setFont(EdalFileChooser.FONT);
			this.centerPane.add(this.pathtext, "cell 1 0 4 1,width max(60%, 60%)");

			this.centerPane.add(new XStatusSeparator(), "cell 5 0 1 1");
		}

		this.searchtext.setFont(EdalFileChooser.FONT);
		if (rootDirectory != null) {
			this.centerPane.add(this.searchtext, "cell 6 0 1 1,width max(100%, 100%)");
			this.searchtext.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					EdalFileChooser.this.search(EdalFileChooser.this.searchtext.getText().trim());
				}
			});
		}

		final JButton searchbutton = new JButton(this.searchAction);
		searchbutton.setFont(EdalFileChooser.FONT);
		if (rootDirectory != null) {
			this.centerPane.add(searchbutton, "cell 8 0 1 1");
		}

		this.detailpanelparent = new JPanel();
		this.detailpanelparent.setFont(EdalFileChooser.FONT);
		this.detailpanelparent.setLayout(new BorderLayout());

		if (rootDirectory != null) {
			final ClientPrimaryDataDirectory f_rootDirectory = rootDirectory;
			this.tree = new JTree(this.addNodes(null, rootDirectory));
			final EdalTreeCellRenderer renderer = new EdalTreeCellRenderer(this.tree.getCellRenderer());
			this.tree.setCellRenderer(renderer);
			this.tree.setOpaque(true);
			this.tree.setBackground(Color.white);
			final UIDefaults treeDefaults = new UIDefaults();
			treeDefaults.put("Tree.selectionBackground", null);
			this.tree.putClientProperty("Nimbus.Overrides", treeDefaults);
			this.tree.putClientProperty("Nimbus.Overrides.InheritDefaults", false);
			// final JScrollPane treepanelparent = new JScrollPane(tree);
			this.treepanelparent = new JScrollPane(this.tree);

			this.tree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(final TreeSelectionEvent e) {
					EdalFileChooser.this.isSelected = true;
					EdalFileChooser.this.currentnode = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
					EdalFileChooser.this.treenodeclick(EdalFileChooser.this.currentnode, f_rootDirectory);
				}
			});

			this.tree.addTreeWillExpandListener(new TreeWillExpandListenerAction(this.tree, rootDirectory));

			this.tree.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					if (EdalFileChooser.this.filemetatable != null) {
						EdalFileChooser.this.filemetatable.clearSelection();

						final JTree ttree = (JTree) e.getComponent();
						final TreePath tpath = ttree.getPathForLocation(e.getX(), e.getY());
						if (tpath != null && tpath.getLastPathComponent() != null) {
							EdalFileChooser.this.currentnode = (DefaultMutableTreeNode) tpath.getLastPathComponent();
							final EdalNode mynode = (EdalNode) EdalFileChooser.this.currentnode.getUserObject();
							EdalFileChooser.this.pathtext.setText(mynode.getPath());
						}
					}
					EdalFileChooser.this.treenodeclick(EdalFileChooser.this.currentnode, f_rootDirectory);
				}

				@Override
				public void mousePressed(final MouseEvent e) {
					// linux
					EdalFileChooser.this.checktreeRightClick(e);
				}

				@Override
				public void mouseReleased(final MouseEvent e) {
					// windows
					EdalFileChooser.this.checktreeRightClick(e);
				}

			});

			try {
				this.defaultModel = this.buildtable(rootDirectory.listPrimaryDataEntities());
			} catch (final RemoteException e1) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				ErrorDialog.showError(e1);
			} catch (final PrimaryDataDirectoryException e1) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				ErrorDialog.showError(e1);
			}
			this.filemetatable = new EdalTable(this.defaultModel);
			// filemetatable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

			if (this.multiSelectionEnabled) {
				this.filemetatable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			} else {
				this.filemetatable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			}

			this.filemetatable.getSelectionModel()
					.addListSelectionListener(new TableRowModelListener(this.filemetatable, this.detailpanelparent));

			this.filemetatable.addMouseMotionListener(new MouseMotionListener() {
				public void mouseDragged(final MouseEvent e) {
				}

				public void mouseMoved(final MouseEvent e) {
					final Point point = e.getPoint();
					final int x = EdalFileChooser.this.filemetatable.rowAtPoint(point);
					final int y = EdalFileChooser.this.filemetatable.columnAtPoint(point);
					if (x != EdalFileChooser.this.row || y != EdalFileChooser.this.column) {
						EdalFileChooser.this.row = x;
						EdalFileChooser.this.column = y;
					}
					if (EdalFileChooser.this.tipsmap
							.containsKey(EdalFileChooser.this.row + 1 + "," + (EdalFileChooser.this.column + 1))) {
						EdalFileChooser.this.filemetatable
								.setToolTipText(EdalFileChooser.this.formattooltips(EdalFileChooser.this.tipsmap
										.get(EdalFileChooser.this.row + 1 + "," + (EdalFileChooser.this.column + 1))));
					} else if (EdalFileChooser.this.row > 0 && EdalFileChooser.this.column > 0) {
						final Object tip = EdalFileChooser.this.filemetatable.getValueAt(EdalFileChooser.this.row,
								EdalFileChooser.this.column);
						EdalFileChooser.this.filemetatable
								.setToolTipText(EdalFileChooser.this.formattooltips(tip.toString()));
					}
				}
			});

			this.filemetatable.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(final MouseEvent evt) {
					// linux
					EdalFileChooser.this.checktableRightClick(evt, EdalFileChooser.this.tree);
				}

				@Override
				public void mouseReleased(final MouseEvent evt) {
					// windows
					EdalFileChooser.this.checktableRightClick(evt, EdalFileChooser.this.tree);
				}
			});

			if (this.tablebackcolor == null) {
				this.tablebackcolor = this.filemetatable.getSelectionBackground();
			}

			/*
			 * 
			 * filemetatable.addFocusListener(new FocusListener(){ public void
			 * focusLost(FocusEvent e) { if (!e.isTemporary()) { //
			 * filemetatable.clearSelection(); //
			 * filemetatable.setSelectionBackground(new Color(135,206,250)); } }
			 * 
			 * @Override public void focusGained(FocusEvent e) {
			 * filemetatable.setSelectionBackground(tablebackcolor); } });
			 */
			JScrollPane filepanel = new JScrollPane(this.filemetatable);
			this.tableshowpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, filepanel, this.detailpanelparent);
			this.tableshowpane.setDividerSize(3);
			UiUtil.fitTableColumns(this.filemetatable, 631);

			if (this.currentdir != null) {
				this.buildDetail(this.detailpanelparent, this.currentdir, true);
			}

			this.tableshowpane.setDividerLocation(0.75);
			this.tableshowpane.setResizeWeight(1);
			this.tableshowpane.resetToPreferredSizes();

			this.dirshowpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.treepanelparent, this.tableshowpane);
			this.dirshowpane.setDividerSize(3);

			treepanelparent.setMinimumSize(new Dimension(250, (int) (250 * 0.618)));
			tableshowpane.setMinimumSize(new Dimension(500, (int) (500 * 0.618)));
			filepanel.setMinimumSize(new Dimension(180, (int) (180 * 0.618)));
			detailpanelparent.setMinimumSize(new Dimension(200, 100));

			this.centerPane.add(this.dirshowpane, "cell 0 1 9 1,width max(100%, 100%)");

			this.detailpanelparent.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(final MouseEvent e) {
					// linux
					EdalFileChooser.this.checkpanelRightClick(e);
				}

				@Override
				public void mouseReleased(final MouseEvent e) {
					// windows
					EdalFileChooser.this.checkpanelRightClick(e);
				}

			});
		}

		this.configbutton = new JButton(Const.CHANGECONN_BTN_STR);
		this.configbutton.setFont(EdalFileChooser.FONT);

		this.configbutton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				EdalFileChooser.this.showlogindialog();
			}
		});

		this.permissionbutton = new JButton(Const.CHANGEPERMISION_BTN_STR);
		this.permissionbutton.setFont(EdalFileChooser.FONT);

		this.permissionbutton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				/*
				 * if (EdalFileChooser.this.filelist.size() > 0 &&
				 * EdalFileChooser.this.filelist.get(EdalFileChooser.this.
				 * fileselectindex) != null) { final PermissionDialog permission
				 * = new PermissionDialog(EdalFileChooser.this.filelist.get(
				 * EdalFileChooser.this.fileselectindex),
				 * EdalFileChooser.this.client); permission.showOpenDialog(); }
				 * else { final PermissionDialog permission = new
				 * PermissionDialog(EdalFileChooser.this.currentdir,
				 * EdalFileChooser.this.client); permission.showOpenDialog(); }
				 */
				if (EdalFileChooser.this.selectedFile != null) {
					final PermissionDialog permission = new PermissionDialog(EdalFileChooser.this.selectedFile,
							EdalFileChooser.this.client);
					permission.showOpenDialog();
				} else if (EdalFileChooser.this.filelist.size() > 0
						&& EdalFileChooser.this.filelist.get(EdalFileChooser.this.fileselectindex) != null) {
					final PermissionDialog permission = new PermissionDialog(
							EdalFileChooser.this.filelist.get(EdalFileChooser.this.fileselectindex),
							EdalFileChooser.this.client);
					permission.showOpenDialog();
				}
			}
		});

		this.metadatabutton = new JButton(Const.CHANGEMETA_BTN_STR);
		this.metadatabutton.setFont(EdalFileChooser.FONT);

		this.metadatabutton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (EdalFileChooser.this.selectedFile != null) {
					final MetaDataSaveDialog metadlg = new MetaDataSaveDialog(EdalFileChooser.this.selectedFile);
					final int returnVal = metadlg.showOpenDialog();
					if (returnVal == MetaDataSaveDialog.APPROVE_OPTION) {
						final Map<EnumDublinCoreElements, UntypedData> metadatavalue = metadlg.getMetaDataValues();
						EdalFileChooser.this.changemetadata(EdalFileChooser.this.selectedFile, metadatavalue, false);
					}
				}
				/*
				 * if (filelist.size() > 0 && filelist.get(fileselectindex) !=
				 * null) { MetaDataSaveDialog metadlg = new MetaDataSaveDialog(
				 * filelist.get(fileselectindex)); int returnVal =
				 * metadlg.showOpenDialog(); if (returnVal ==
				 * MetaDataSaveDialog.APPROVE_OPTION) {
				 * Map<EnumDublinCoreElements, UntypedData> metadatavalue =
				 * metadlg .getMetadatavalue();
				 * changemetadata(filelist.get(fileselectindex), metadatavalue,
				 * true); } } else { MetaDataSaveDialog metadlg = new
				 * MetaDataSaveDialog(currentdir); int returnVal =
				 * metadlg.showOpenDialog(); if (returnVal ==
				 * MetaDataSaveDialog.APPROVE_OPTION) {
				 * Map<EnumDublinCoreElements, UntypedData> metadatavalue =
				 * metadlg .getMetadatavalue(); changemetadata(currentdir,
				 * metadatavalue, false); } }
				 */
			}
		});

		this.okbutton = new JButton(Const.OK_BTN_STR);
		this.okbutton.setFont(EdalFileChooser.FONT);

		this.okbutton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (EdalFileChooser.this.getSelectedFile() == null) {
					return;
				}
				EdalFileChooser.this.returnvalue = EdalFileChooser.APPROVE_OPTION;
				EdalFileChooser.this.dispose();
			}
		});

		final JButton cancelbutton = new JButton(this.cancelAction);
		cancelbutton.setFont(EdalFileChooser.FONT);

		final JPanel functionButtons = new JPanel();

		functionButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		if (showconnbutton) {
			functionButtons.add(this.configbutton);
		}
		if (rootDirectory != null) {
			functionButtons.add(this.permissionbutton);
			functionButtons.add(this.metadatabutton);

			this.showmetabutton(rootDirectory);
			this.showpermissionbutton(rootDirectory);
		}
		functionButtons.add(this.okbutton);
		functionButtons.add(cancelbutton);

		this.centerPane.add(functionButtons, "cell 0 2 9 1,align right ");

		this.setPreferredSize(new Dimension(950, 575));
		this.setMinimumSize(new Dimension(950, 575));

		if (rootDirectory != null) {
			this.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(final ComponentEvent e) {
					EdalFileChooser.this.dirshowpane.setDividerLocation(0.3);
				}
			});
		}

		this.statusbar.updateStatus(this.client.getServerAddress(), this.client.getRegistryPort(), this.username);
		maincontents.updateUI();
		this.pack();
		if (rootDirectory != null) {
			this.dirshowpane.setDividerLocation(0.3);
		}
	}

	protected void initViewers() {
		// EditorContainer.clear();
		this.metadatatoshow.add("DATE");
		this.metadatatoshow.add("FORMAT");
		ViewerContainer.registerViewer(EnumDublinCoreElements.CHECKSUM, new CheckSumViewer());
		ViewerContainer.registerViewer(EnumDublinCoreElements.CREATOR, new PersonInfoViewer());
		ViewerContainer.registerViewer(EnumDublinCoreElements.CONTRIBUTOR, new PersonInfoViewer());
		ViewerContainer.registerViewer(EnumDublinCoreElements.PUBLISHER, new LegalPersonInfoViewer());
		ViewerContainer.registerViewer(EnumDublinCoreElements.RELATION, new IdentifierRelationViewer());
		ViewerContainer.registerViewer(EnumDublinCoreElements.DATE, new DateEventsViewer());
		ViewerContainer.registerViewer(EnumDublinCoreElements.COVERAGE, new TextViewer());
		ViewerContainer.registerViewer(EnumDublinCoreElements.DESCRIPTION, new TextViewer());
		ViewerContainer.registerViewer(EnumDublinCoreElements.FORMAT, new DataFormatViewer());
		ViewerContainer.registerViewer(EnumDublinCoreElements.IDENTIFIER, new IdentifierViewer());
		ViewerContainer.registerViewer(EnumDublinCoreElements.LANGUAGE, new LanguageViewer());
		ViewerContainer.registerViewer(EnumDublinCoreElements.RIGHTS, new TextViewer());
		ViewerContainer.registerViewer(EnumDublinCoreElements.SOURCE, new TextViewer());
		ViewerContainer.registerViewer(EnumDublinCoreElements.SUBJECT, new SubjectViewer());
		ViewerContainer.registerViewer(EnumDublinCoreElements.TITLE, new TextViewer());

	}

	private void refreshtable() {
		this.cleartable();
		List<ClientPrimaryDataEntity> dirlist = null;
		try {
			dirlist = this.currentdir.listPrimaryDataEntities();
		} catch (final PrimaryDataDirectoryException e1) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
			ErrorDialog.showError(e1);
		} catch (final RemoteException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		}
		if (dirlist != null) {
			this.defaultModel = this.buildtable(dirlist);
			this.filemetatable.setModel(this.defaultModel);
			this.defaultModel.fireTableDataChanged();
			UiUtil.fitTableColumns(this.filemetatable, 631);
		}
	}

	private void refreshtreenode(final JTree tree) {
		if (this.currentnode != null) {
			this.currentnode.removeAllChildren();
			if (this.currentdir != null) {
				try {
					final List<ClientPrimaryDataEntity> dirlist = this.currentdir.listPrimaryDataEntities();
					final List<String> dirnamelist = new ArrayList<String>();
					final Map<String, ClientPrimaryDataEntity> dirnamemap = new HashMap<String, ClientPrimaryDataEntity>();

					if (dirlist != null) {
						for (final ClientPrimaryDataEntity dir : dirlist) {
							if (dir.isDirectory() && !dir.getCurrentVersion().isDeleted()) {
								dirnamelist.add(dir.getName());
								dirnamemap.put(dir.getName(), dir);
							}
						}
					}
					Collections.sort(dirnamelist, String.CASE_INSENSITIVE_ORDER);

					for (final String dirname : dirnamelist) {
						final DefaultMutableTreeNode child = new EdalMutableTreeModel(new EdalNode(dirname,
								((ClientPrimaryDataDirectory) dirnamemap.get(dirname)).getPath()));
						this.currentnode.add(child);
					}
					tree.updateUI();
				} catch (final RemoteException e1) {
					ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
					ErrorDialog.showError(e1);
				} catch (final PrimaryDataDirectoryException e1) {
					ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
					ErrorDialog.showError(e1);
				}
			}
		}
	}

	private void search(final String keyword) {
		this.cleartable();

		final Set<ClientPrimaryDataEntity> results = new HashSet<ClientPrimaryDataEntity>();
		final List<ClientPrimaryDataEntity> resultslist = new ArrayList<ClientPrimaryDataEntity>();

		if (this.currentdir == null) {
			this.currentdir = this.rootDirectory;
		}

		final EdalFileChooser thisdialog = this;
		final Cursor cursor = thisdialog.getCursor();
		thisdialog.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		final InfiniteProgressPanel glassPane = new InfiniteProgressPanel();
		glassPane.setWidth(this.getWidth());
		glassPane.setHeight(this.getHeight() - 150);
		this.setGlassPane(glassPane);
		glassPane.setText("search for:" + keyword);
		glassPane.start();

		new Thread() {
			@Override
			public void run() {
				try {
					final ClientPrimaryDataEntity[] result = EdalFileChooser.this.currentdir
							.searchByKeyword(keyword, true, true).toArray(new ClientPrimaryDataEntity[0]);
					if (result != null) {
						for (final ClientPrimaryDataEntity data : result) {
							results.add(data);
						}
					}

					for (final ClientPrimaryDataEntity data : results) {
						resultslist.add(data);
					}

					EdalFileChooser.this.defaultModel = EdalFileChooser.this.buildtable(resultslist);
					EdalFileChooser.this.filemetatable.setModel(EdalFileChooser.this.defaultModel);
					EdalFileChooser.this.defaultModel.fireTableDataChanged();
					UiUtil.fitTableColumns(EdalFileChooser.this.filemetatable, 631);
				} catch (final RemoteException e) {
					ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
					ErrorDialog.showError(e);
				} catch (final PrimaryDataDirectoryException e) {
					ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
					ErrorDialog.showError(e);
				} finally {
					thisdialog.setCursor(cursor);
					glassPane.stop();
				}
			}
		}.start();

	}

	/**
	 * Sets the current file filter. The file filter is used by the file chooser
	 * to filter out files from the user's view.
	 * 
	 * Beaninfo preferred: true bound: true description: Sets the File Filter
	 * used to filter out files of type.
	 * 
	 * @param filter
	 *            the new current file filter to use
	 * @see #getFileFilter
	 */

	public void setFileFilter(final EdalAbstractFileFilter filter) {
		this.fileFilter = filter;
	}

	/**
	 * Sets the <code>EDALFileChooser</code> to allow the user to just select
	 * files, just select directories, or select both files and directories. The
	 * default is <code>EDALFileChooser.FILES_ONLY</code>.
	 * 
	 * @param mode
	 *            the type of files to be displayed:
	 *            <ul>
	 *            <li>EDALFileChooser.FILES_ONLY
	 *            <li>EDALFileChooser.DIRECTORIES_ONLY
	 *            <li>EDALFileChooser.FILES_AND_DIRECTORIES
	 *            </ul>
	 * 
	 * @exception IllegalArgumentException
	 *                if <code>mode</code> is an illegal file selection mode
	 *                Beaninfo preferred: true bound: true description: Sets the
	 *                types of files that the JFileChooser can choose. enum:
	 *                FILES_ONLY EDALFileChooser.FILES_ONLY DIRECTORIES_ONLY
	 *                EDALFileChooser.DIRECTORIES_ONLY
	 *                EDALFileChooser.FILES_AND_DIRECTORIES
	 * 
	 * 
	 * @see #getFileSelectionMode
	 */
	public void setFileSelectionMode(final int mode) {
		if (mode == EdalFileChooser.FILES_ONLY || mode == EdalFileChooser.DIRECTORIES_ONLY
				|| mode == EdalFileChooser.FILES_AND_DIRECTORIES) {
			this.fileSelectionMode = mode;
		} else {
			throw new IllegalArgumentException("Incorrect Mode for file selection: " + mode);
		}
	}

	/**
	 * Sets the file chooser to allow multiple file selections.
	 * 
	 * @param b
	 *            true if multiple files may be selected Beaninfo bound: true
	 *            description: Sets multiple file selection mode.
	 */

	public void setMultiSelectionEnabled(final boolean b) {
		this.multiSelectionEnabled = b;
	}

	private int showlogindialog() {

		final EdalConfigDialog eDALConfigUi = new EdalConfigDialog();
		final int returnVal = eDALConfigUi.showOpenDialog();
		if (returnVal == EdalConfigDialog.APPROVE_OPTION) {
			/*
			 * EdalLoginHelper helper = new EdalLoginHelper();
			 * helper.savedata(eDALConfigUi.getServeraddress(),
			 * eDALConfigUi.getServerport(), eDALConfigUi.getUsername(),
			 * eDALConfigUi.getPassword()); bindui = false; cleartable();
			 * init(EdalConfigDialog.rootDirectory); if (EdalConfigDialog.client
			 * != null) { client = EdalConfigDialog.client; username =
			 * client.getAuthentication().getName(); pathtext.setText(""); }
			 */
			return EdalFileChooser.APPROVE_OPTION;

		}
		return EdalFileChooser.CANCEL_OPTION;
	}

	private void showmetabutton(final ClientPrimaryDataEntity dataentry) {
		if (!PrincipalUtil.checkPermission(dataentry, this.username, EdalFileChooser.CHANGEMETADATAMETHODNAME)) {
			this.metadatabutton.setVisible(false);
		} else {
			this.metadatabutton.setVisible(true);
		}
	}

	/**
	 * Pops up an "Open File" file chooser dialog. Note that the text that
	 * appears in the approve button is determined by the L and F.
	 * 
	 * @return the return state of the file chooser on popdown:
	 *         <ul>
	 *         <li>EDALFileChooser.CANCEL_OPTION
	 *         <li>EDALFileChooser.APPROVE_OPTION
	 *         <li>EDALFileChooser.ERROR_OPTION if an error occurs or the dialog
	 *         is dismissed
	 *         </ul>
	 */

	public int showOpenDialog() {

		if (this.rootDirectory == null) {
			this.dispose();
			return EdalFileChooser.ERROR_OPTION;
		}

		try {
			this.initViewers();
			this.savemodal = false;
			this.setTitle("Open File");
			this.init(this.rootDirectory);
			if (this.client == null) {
				if (EdalFileChooser.CANCEL_OPTION == this.showlogindialog()) {
					this.dispose();
					return this.returnvalue;
				}
			}
			this.setModal(true);
			this.setLocationRelativeTo(null);
			this.setVisible(true);
			return this.returnvalue;
		} catch (final Exception e) {
			this.dispose();
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
			return EdalFileChooser.ERROR_OPTION;
		}

	}

	private void showpermissionbutton(final ClientPrimaryDataEntity dataentry) {
		if (!PrincipalUtil.checkPermission(dataentry, this.username, EdalFileChooser.CHANGEPERMISSIONTHODNAME)) {
			this.permissionbutton.setVisible(false);
		} else {
			this.permissionbutton.setVisible(true);
		}
	}

	/**
	 * Pops up a "Save File" file chooser dialog. Note that the text that
	 * appears in the approve button is determined by the L and F.
	 * 
	 * @return the return state of the file chooser on popdown:
	 *         <ul>
	 *         <li>EDALFileChooser.CANCEL_OPTION
	 *         <li>EDALFileChooser.APPROVE_OPTION
	 *         <li>EDALFileChooser.ERROR_OPTION if an error occurs or the dialog
	 *         is dismissed
	 *         </ul>
	 */

	public int showSaveDialog() {

		if (this.rootDirectory == null) {
			this.dispose();
			return EdalFileChooser.ERROR_OPTION;
		}

		try {
			this.initViewers();
			this.savemodal = true;
			this.setTitle("Save File");
			this.init(this.rootDirectory);
			this.setModal(true);
			if (this.client == null) {
				if (EdalFileChooser.CANCEL_OPTION == this.showlogindialog()) {
					this.dispose();
					return this.returnvalue;
				}
			}
			this.setLocationRelativeTo(null);
			this.setVisible(true);
			return this.returnvalue;
		} catch (final Exception e) {
			this.dispose();
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
			return EdalFileChooser.ERROR_OPTION;
		}
	}

	/**
	 * show the "changeconnection" or not.
	 * 
	 * @param showconnbutton
	 *            false if you want to hidden the button
	 */
	public void showConnectionButton(boolean showconnbutton) {
		this.showconnbutton = showconnbutton;
	}

	private void switchversion(final int versionidx) {
		if (this.selectedFile != null) {
			this.currentversion = this.versionlist.get(versionidx);
			try {
				String strdate = null;
				final SimpleDateFormat dataformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				if (this.currentversion.getRevisionDate() != null) {
					strdate = dataformat.format(this.currentversion.getRevisionDate().getTime());
				}
				this.selectedFile.switchCurrentVersion(this.currentversion);
				this.currentversionmap.put(this.selectedFile.getPath(), this.currentversion);
				this.versionpanel.setTitle("Version " + this.currentversion.getRevision() + " :  " + strdate
						+ (this.currentversion.isDeleted() ? " - deleted." : ""));

			} catch (final RemoteException e) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
				ErrorDialog.showError(e);
			} catch (final AccessControlException e) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			} catch (final PrimaryDataEntityVersionException e) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			}
		}
	}

	private boolean isexistdeleteversion(ClientPrimaryDataDirectory parentdir, String newfoldername) {
		try {
			final List<ClientPrimaryDataEntity> childdirlist = parentdir.listPrimaryDataEntities();
			if (childdirlist != null) {
				for (final ClientPrimaryDataEntity dir : childdirlist) {
					ClientPrimaryDataEntityVersion version = null;
					try {
						version = dir.getCurrentVersion();
						if (version.isDeleted() && dir.getName().equals(newfoldername)) {
							return true;
						}
					} catch (final Exception e) {
						// we don't have the permission to call
						// getCurrentVersion method
					}
				}
			}
		} catch (final RemoteException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		} catch (final PrimaryDataDirectoryException e) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e));
			ErrorDialog.showError(e);
		}

		return false;
	}

	private void treenodeclick(final DefaultMutableTreeNode currentnode,
			final ClientPrimaryDataDirectory f_rootDirectory) {
		if (currentnode == null || currentnode.getUserObject() == null) {
			return;
		}
		final EdalNode mynode = (EdalNode) currentnode.getUserObject();
		this.pathtext.setText(mynode.getPath());
		this.cleartable();
		try {
			try {
				this.currentdir = (ClientPrimaryDataDirectory) EdalFileHelper.getEntity(mynode.getPath(),
						f_rootDirectory);
			} catch (final PrimaryDataDirectoryException e1) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				ErrorDialog.showError(e1);
			}
			List<ClientPrimaryDataEntity> dirlist = null;
			try {
				dirlist = this.currentdir.listPrimaryDataEntities();
			} catch (final PrimaryDataDirectoryException e1) {
				ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
				ErrorDialog.showError(e1);
			} catch (final AccessControlException e1) {
				ErrorDialog.showError(e1);
			}
			if (dirlist != null) {
				this.defaultModel = this.buildtable(dirlist);
				this.filemetatable.setModel(this.defaultModel);
				this.defaultModel.fireTableDataChanged();
				UiUtil.fitTableColumns(this.filemetatable, 631);

				if (this.detailpanel != null) {
					if (!this.detailpanel.isCollapsed()) {
						this.detailpanel.setCollapsed(true);
						this.detailpanel.setScrollOnExpand(true);
					}
				}

			}
			if (this.currentdir != null) {
				this.buildDetail(this.detailpanelparent, this.currentdir, true);
			}
			this.showmetabutton(this.currentdir);
			this.showpermissionbutton(this.currentdir);
			// jinbo tree.scrollPathToVisible(e.getPath());
		} catch (final RemoteException e1) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
			ErrorDialog.showError(e1);
		} catch (final NotBoundException e1) {
			ClientDataManager.logger.error(StackTraceUtil.getStackTrace(e1));
			ErrorDialog.showError(e1);
		}
	}
}

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

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.ipk_gatersleben.bit.bi.edal.publication.attribute.panel.RelatedIdentifierPanel;

public class RelatedIdDragDropListener implements DropTargetListener {

	@Override
	public void drop(DropTargetDropEvent dtde) {
		try {
			Transferable tr = dtde.getTransferable();
			DataFlavor[] flavors = tr.getTransferDataFlavors();
			ArrayList<File> fileNames = new ArrayList<File>();
			for (int i = 0; i < flavors.length; i++) {
				if (flavors[i].isFlavorJavaFileListType()) {
					dtde.acceptDrop(dtde.getDropAction());
					@SuppressWarnings("unchecked")
					java.util.List<File> files = (java.util.List<File>) tr.getTransferData(flavors[i]);
					for (int k = 0; k < files.size(); k++) {
						fileNames.add(files.get(k));
						// Die Dateien werden mit dem spezifischen Programm geöffnet.
						// Desktop.getDesktop().open(files.get(k));
						// Der Dateiname wird in der Comandozeile ausgegeben.

						System.out.println(files.get(k));

						File file = files.get(k);

						FileInputStream fileInputStream = new FileInputStream(file);
						Workbook workbook = new XSSFWorkbook(fileInputStream);

						Sheet sheet = workbook.getSheetAt(0);

						Map<Integer, List<String>> data = new HashMap<>();

						Object[][] datatable = new Object[3][3];

						for (Row row : sheet) {
							data.put(i, new ArrayList<String>());
							for (Cell cell : row) {

								System.out.println("Zeile: " + row.getRowNum() + " Spalte: " + cell.getColumnIndex());
								switch (cell.getCellType()) {

								case STRING:
									System.out.println(cell.getStringCellValue());
									datatable[row.getRowNum()][cell.getColumnIndex()] = cell.getStringCellValue();

									break;
								case NUMERIC:
									System.out.println("2");
									break;
								case BOOLEAN:
									System.out.println("3");
									break;
								case FORMULA:
									System.out.println("4");
									break;
								default:
									;
								}
							}

						}

						for (int zeilennummer = 0; zeilennummer < datatable.length; zeilennummer++) {
							for (int spaltennummer = 0; spaltennummer < datatable[zeilennummer].length; spaltennummer++) {
								System.out.println("DATA (" + zeilennummer + ")(" + spaltennummer + ")"
										+ datatable[zeilennummer][spaltennummer]);
							}

						}

						JTable table = new JTable(datatable, datatable[0]);

						JScrollPane scrollPane = new JScrollPane(table);

//						PublicationMainPanel.relatedIdentifierPanel.setScrollpane(scrollPane);

					

//						
//						((AttributeSplitPane) PublicationMainPanel.relatedIdentifierPublisherPanel).setRightComponent(null);
//						
//						((AttributeSplitPane) PublicationMainPanel.relatedIdentifierPublisherPanel)
//						.setRightComponent(scrollPane);
						
					}

					dtde.dropComplete(true);
				}
			}
			return;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		dtde.rejectDrop();

	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
	}

}

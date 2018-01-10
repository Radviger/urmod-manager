package mod.manager;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 *
 */
public abstract class FileTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;

	/**
	 * 把row物件轉成String
	 * 
	 * @param c
	 * @return
	 */
	protected abstract String exportString(JComponent c);

	/**
	 * 把String轉回row物件
	 * 
	 * @param c
	 * @param str
	 */
	protected abstract void importString(JComponent c, String str);
	
	/**
	 * 把Files轉成row
	 * 
	 * @param c
	 * @param files
	 */
	protected abstract void importFile(JComponent c, List<File> files);

	/**
	 * 清理原本該筆資料
	 * 
	 * @param c
	 * @param remove
	 */
	protected abstract void cleanup(JComponent c, boolean remove);

	@Override
	protected Transferable createTransferable(JComponent c) {
		// 轉成StringSelection
		return new StringSelection(exportString(c));
	}

	@Override
	public int getSourceActions(JComponent c) {
		return MOVE; // 只允許移動
	}

	@Override
	public boolean importData(JComponent c, Transferable t) {
		if (canImportFile(c, t.getTransferDataFlavors())) {
			try {
				// 接受Files
				@SuppressWarnings("unchecked")
				List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
				importFile(c, files);
				return true;
			} catch (UnsupportedFlavorException ufe) {
			} catch (IOException ioe) {
			}
		} else if (canImport(c, t.getTransferDataFlavors())) {
			try {
				// 接受String
				String str = (String) t.getTransferData(DataFlavor.stringFlavor);
				importString(c, str);
				return true;
			} catch (UnsupportedFlavorException ufe) {
			} catch (IOException ioe) {
			}
		}

		return false;
	}

	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
		// 移動完成要清理資料
		cleanup(c, action == MOVE);
	}

	@Override
	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		for (int i = 0; i < flavors.length; i++) {
			if (DataFlavor.stringFlavor.equals(flavors[i])) {
				return true;
			}
		}
		
		if (canImportFile(c, flavors)) {
			return true;
		}
		
		return false;
	}
	
	public boolean canImportFile(JComponent c, DataFlavor[] flavors) {
		for (int i = 0; i < flavors.length; i++) {
			if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
				return true;
			}
		}
		return false;
	}
}

package mod.manager;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 *
 */
public abstract class StringTransferHandler extends TransferHandler {
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
		if (canImport(c, t.getTransferDataFlavors())) {
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
		return false;
	}
}

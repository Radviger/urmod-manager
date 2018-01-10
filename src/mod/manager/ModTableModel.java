package mod.manager;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import mod.manager.constant.ModIcon;
import mod.manager.constant.ModTableColumn;
import mod.manager.util.Utils;
import mod.manager.vo.ModVO;

/**
 * 
 */
public class ModTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private List<ModVO> mods;
	private ModEnableListener modEnableListener;

	public ModTableModel(List<ModVO> mods) {
		this.mods = new ArrayList<ModVO>(mods);
	}

	/**
	 * @param modEnableListener
	 */
	public void addModEnableListener(ModEnableListener modEnableListener) {
		this.modEnableListener = modEnableListener;
	}
	
	/**
	 * 
	 */
	public void removeEnableListener() {
		this.modEnableListener = null;
	}
	
	/**
	 * @param index
	 * @return
	 */
	public ModVO getRow(int index) {
		return mods.get(index);
	}

	/**
	 * @return
	 */
	public List<ModVO> getModList() {
		return mods;
	}

	/**
	 * @param mod
	 */
	public void addMod(ModVO mod) {
		// 檢查是否已存在
		ModVO existMod = null;
		for (ModVO modVO : mods) {
			if (modVO.getName().equals(mod.getName())) {
				existMod = modVO;
				break;
			}
		}
		if (existMod != null) {
			int index = mods.indexOf(existMod);
			this.mods.remove(index);
			mods.add(index, mod);
		} else {
			mods.add(mod);
		}
	}
	
	public void insertRow(int row, ModVO mod) {
		mods.add(row, mod);
	}

	/**
	 * @param index
	 */
	public void removeRow(int index) {
		mods.remove(index);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return ModTableColumn.fromIndex(columnIndex) == ModTableColumn.ENABLE;
	}

	@Override
	public int getRowCount() {
		return mods.size();
	}

	@Override
	public int getColumnCount() {
		return ModTableColumn.values().length;
	}

	@Override
	public String getColumnName(int col) {
		return ModTableColumn.fromIndex(col).getName();
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return ModTableColumn.fromIndex(col).getClazzType();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ModVO mod = mods.get(rowIndex);

		switch (ModTableColumn.fromIndex(columnIndex)) {
		case NUM:
			return rowIndex + 1;
		case ENABLE:
			return mod.isEnable();
		case RATE:
			switch (mod.getRate()) {
			case 1:
				return ModIcon.RATE1;
			case 2:
				return ModIcon.RATE2;
			case 3:
				return ModIcon.RATE9;
			default:
				return ModIcon.RATE0;
			}
		case COVER:
			return Utils.loadCover(mod);
		case NAME:
			return mod.getName();
		case VERSION:
			return mod.getVersion();
		case STATUS:
			return mod.getStatus().label;
		case INSTALLDATE:
			return mod.getInstallDate();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (aValue instanceof Boolean && ModTableColumn.fromIndex(column) == ModTableColumn.ENABLE) {
			if (modEnableListener != null) {
				modEnableListener.modEnableUpdated(mods.get(row), (Boolean) aValue);
			}
			fireTableCellUpdated(row, column);
		}
	}

	interface ModEnableListener {
		public void modEnableUpdated(ModVO mod, boolean enable);
	}
}

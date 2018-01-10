package mod.manager;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import mod.manager.constant.TrainerTableColumn;
import mod.manager.vo.CarTrainerVO;

/**
 * 
 */
public class TrainerTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private List<CarTrainerVO> cars;

	public TrainerTableModel(List<CarTrainerVO> cars) {
		this.cars = new ArrayList<CarTrainerVO>(cars);
	}

	/**
	 * @param index
	 * @return
	 */
	public CarTrainerVO getRow(int index) {
		return cars.get(index);
	}

	/**
	 * @return
	 */
	public List<CarTrainerVO> getModList() {
		return cars;
	}

	
	public void insertRow(int row, CarTrainerVO mod) {
		cars.add(row, mod);
	}

	/**
	 * @param index
	 */
	public void removeRow(int index) {
		cars.remove(index);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return TrainerTableColumn.fromIndex(columnIndex) == TrainerTableColumn.ENABLE ||
				TrainerTableColumn.fromIndex(columnIndex) == TrainerTableColumn.CALL ||
				TrainerTableColumn.fromIndex(columnIndex) == TrainerTableColumn.NAME;
	}

	@Override
	public int getRowCount() {
		return cars.size();
	}

	@Override
	public int getColumnCount() {
		return TrainerTableColumn.values().length;
	}

	@Override
	public String getColumnName(int col) {
		return TrainerTableColumn.fromIndex(col).getName();
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return TrainerTableColumn.fromIndex(col).getClazzType();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		CarTrainerVO mod = cars.get(rowIndex);

		switch (TrainerTableColumn.fromIndex(columnIndex)) {
		case NUM:
			return rowIndex + 1;
		case ENABLE:
			return "1".equals(mod.getEnable());
		case CALL:
			return mod.getModelName();
		case NAME:
			return mod.getDisplayName();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (aValue instanceof Boolean && TrainerTableColumn.fromIndex(column) == TrainerTableColumn.ENABLE) {
			boolean enable = (Boolean) aValue;
			cars.get(row).setEnable(enable ? "1" : "0");
			fireTableCellUpdated(row, column);
		} else if (aValue instanceof String && TrainerTableColumn.fromIndex(column) == TrainerTableColumn.CALL){
			cars.get(row).setModelName((String) aValue);
			fireTableCellUpdated(row, column);
		} else if (aValue instanceof String && TrainerTableColumn.fromIndex(column) == TrainerTableColumn.NAME){
			cars.get(row).setDisplayName((String) aValue);
			fireTableCellUpdated(row, column);
		}
	}
	
}

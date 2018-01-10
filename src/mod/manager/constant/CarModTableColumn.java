package mod.manager.constant;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

/**
 * Car Mod Table的Column定義
 */
public enum CarModTableColumn {

	NUM(0, "#", Integer.class),
	ENABLE(1, "啟用", Boolean.class),
	RATE(2, "評分", ImageIcon.class),
	COVER(3, "截圖", ImageIcon.class),
	NAME(4, "Mod名稱", String.class),
	VERSION(5, "版本", String.class),
	DLCNAME(6, "DLC", String.class),
	STATUS(7, "狀態", String.class),
	INSTALLDATE(8, "安裝日期", Date.class);
	
	private int index;
	private String name;
	private Class<?> clazzType;
	private CarModTableColumn(int index, String name, Class<?> clazzType) {
		this.index = index;
		this.name = name;
		this.clazzType = clazzType;
	}
	
	private static final Map<Integer, CarModTableColumn> COLUMN_INDEX_NAME_MAP = new HashMap<>();
	private static final List<String> NAMES = new ArrayList<>();
	
	static {
		for (CarModTableColumn column : CarModTableColumn.values()) {
			COLUMN_INDEX_NAME_MAP.put(column.index, column);
			NAMES.add(column.name);
		}
	}
	
	public static CarModTableColumn fromIndex(int index) {
		return COLUMN_INDEX_NAME_MAP.get(index);
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getName() {
		return name;
	}
	
	public Class<?> getClazzType() {
		return clazzType;
	}
	
}

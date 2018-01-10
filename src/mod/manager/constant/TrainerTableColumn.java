package mod.manager.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Trainer Table的Column定義
 */
public enum TrainerTableColumn {

	NUM(0, "#", Integer.class),
	ENABLE(1, "啟用", Boolean.class),
	CALL(2, "車輛呼叫名稱(ModelName)", String.class),
	NAME(3, "車輛顯示名稱(DisplayName)", String.class);
	
	private int index;
	private String name;
	private Class<?> clazzType;
	private TrainerTableColumn(int index, String name, Class<?> clazzType) {
		this.index = index;
		this.name = name;
		this.clazzType = clazzType;
	}
	
	private static final Map<Integer, TrainerTableColumn> COLUMN_INDEX_NAME_MAP = new HashMap<>();
	private static final List<String> NAMES = new ArrayList<>();
	
	static {
		for (TrainerTableColumn column : TrainerTableColumn.values()) {
			COLUMN_INDEX_NAME_MAP.put(column.index, column);
			NAMES.add(column.name);
		}
	}
	
	public static TrainerTableColumn fromIndex(int index) {
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

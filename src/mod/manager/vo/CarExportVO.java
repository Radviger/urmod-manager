package mod.manager.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 匯出CarVO
 */
@Data
public class CarExportVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private boolean enable;
}

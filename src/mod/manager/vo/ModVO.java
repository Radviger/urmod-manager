package mod.manager.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import mod.manager.constant.Status;

/**
 * 已安裝的ModVO
 */
@Data
public class ModVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String version;
	private String newVersion;
	private String zipName;
	private Date installDate;
	private boolean enable;
	private Status status;
	private int rate;
	private String html;
	
	private String[] fileArray;
}

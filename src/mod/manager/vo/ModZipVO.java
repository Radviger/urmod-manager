package mod.manager.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import mod.manager.constant.Status;

/**
 * Mod的zip檔案VO
 */
@Data
public class ModZipVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String fileUrl;
	private Date downloadDate;
	private Status status;
	private String html;
	private String version;
}

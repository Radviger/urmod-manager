package mod.manager.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class CarTrainerVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String enable;
	private String modelName;
	private String displayName;
}

package mod.manager.vo;

import java.io.Serializable;

import lombok.Data;

/**
 *
 */
@Data
public class CarTrainerArray implements Serializable {
	private static final long serialVersionUID = 1L;

	private CarTrainerVO[] carArray;
}

package mod.manager.vo;

import java.io.Serializable;

import lombok.Data;

/**
 *
 */
@Data
public class CarArray implements Serializable {
	private static final long serialVersionUID = 1L;

	private CarVO[] carArray;
}

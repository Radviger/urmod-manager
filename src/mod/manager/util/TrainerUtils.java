package mod.manager.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import mod.manager.constant.Constant;
import mod.manager.vo.CarTrainerVO;
import mod.manager.vo.CarVO;

public final class TrainerUtils {

	private static StringBuffer pre = new StringBuffer();
	private static StringBuffer post = new StringBuffer();
	private static StringBuffer content = new StringBuffer();
	private static List<String> contents = new ArrayList<>();
	private static List<CarTrainerVO> cars = new ArrayList<>();
	private static int status = 0;
	
	/**
	 * @return
	 */
	public static List<CarTrainerVO> loadTrainer() {
		pre = new StringBuffer();
		post = new StringBuffer();
		content = new StringBuffer();
		contents = new ArrayList<>();
		cars = new ArrayList<>();
		status = 0;
		
		FileInputStream fis = null;
		try {
			String path = Utils.prop.getProperty(Constant.GAME_DIR) + File.separator;
			//String path = "C:\\Users\\Neo\\Desktop\\UrModManager\\GameDir";
			File fromFile = new File(path, Constant.TRAINER_FILE);
			fis = new FileInputStream(fromFile);
			try (Scanner scanner = new Scanner(fis, "BIG5")) {
				while (scanner.hasNextLine()) {
					processLine(scanner.nextLine());
				}
			}
			
			processContent();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            try { if (fis != null) fis.close(); } catch (IOException e) {}
        }
		
		return cars;
	}
	
	private static void processLine(String aLine) {
		if ("[AddedCars]".equals(aLine)) {
			status = 1;
			pre.append(aLine).append("\r\n");
		} else {
			if (status == 0) {
				pre.append(aLine).append("\r\n");
			} else if (status == 1) {
				if ("//Animations//".equals(aLine)) {
					status = 2;
					post.append(aLine).append("\r\n");
				} else if (!"".equals(aLine)) {
					contents.add(aLine);
				}
			} else if (status == 2) {
				post.append(aLine).append("\r\n");
			}
		}
	}
	
	private static void processContent() {
		CarTrainerVO car = null;
		for (String c : contents) {
			Scanner scanner = null;
			try {
				scanner = new Scanner(c);
				scanner.useDelimiter("=");
				if (scanner.hasNext()) {
					String name = scanner.next();
					String value = scanner.next();
					if (name.startsWith("Enable")) {
						car = new CarTrainerVO();
						car.setEnable(value.trim());
					} else if (name.startsWith("ModelName")) {
						car.setModelName(value.trim());
					} else if (name.startsWith("DisplayName")) {
						if (value.matches("Car \\d+")) {
							car.setDisplayName("Car");
						} else {
							car.setDisplayName(value.trim());
						}
						cars.add(car);
					}
				}
			} finally {
				if (scanner != null) {
					scanner.close();
				}
			}
		}
	}
	
	/**
	 * @param cars
	 */
	public static void saveTrainer(List<CarTrainerVO> cars) {
		try {
			content = new StringBuffer();
			content.append(pre);
			
			for (int i=0; i<cars.size(); i++) {
				CarTrainerVO car = cars.get(i);
				content.append("Enable").append(i+1).append("=").append(car.getEnable()).append("\r\n");
				content.append("ModelName").append(i+1).append("=").append(car.getModelName()).append("\r\n");
				if ("Car".equals(car.getDisplayName())) {
					content.append("DisplayName").append(i+1).append("=").append(car.getDisplayName() + " " + (i+1)).append("\r\n");
				} else {
					content.append("DisplayName").append(i+1).append("=").append(car.getDisplayName()).append("\r\n");
				}
			}
			
			content.append(post);
			
			String path = Utils.prop.getProperty(Constant.GAME_DIR) + File.separator;
			//String path = "C:\\Users\\Neo\\Desktop\\UrModManager\\GameDir";
			File outputFile = new File(path, Constant.TRAINER_FILE);
			//File outputFile = new File(path, "trainerv2.ini");
	        FileOutputStream fos = null;
	        try {
	            fos = new FileOutputStream(outputFile);
	            fos.write(content.toString().getBytes("BIG5"));
	        } finally {
	            try { if (fos != null) fos.close(); } catch (IOException e) {}
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 啟用存在trainerv.ini中的車輛
	 * 
	 * @param car
	 * @return
	 */
	public static int enableCar(CarVO car) {
		List<CarTrainerVO> carTrainers = loadTrainer();
		List<String> callNames = Utils.getCallNames(car.getCallName());
		
		int cnt = 0;
		for (CarTrainerVO carTrainer : carTrainers) {
			for (String callName : callNames) {
				if (callName.equals(carTrainer.getModelName())) {
					carTrainer.setEnable("1");
					cnt++;
					if (carTrainer.getDisplayName().startsWith("[DEL]")) {
						carTrainer.setDisplayName(carTrainer.getDisplayName().replaceFirst("\\[DEL\\]", ""));
					}
				}
			}
		}
		
		saveTrainer(carTrainers);
		return cnt;
	}
	
	/**
	 * 停用存在trainerv.ini中的車輛
	 * 
	 * @param car
	 * @return
	 */
	public static int disableCar(CarVO car) {
		List<CarTrainerVO> carTrainers = loadTrainer();
		List<String> callNames = Utils.getCallNames(car.getCallName());
		
		int cnt = 0;
		for (CarTrainerVO carTrainer : carTrainers) {
			for (String callName : callNames) {
				if (callName.equals(carTrainer.getModelName())) {
					carTrainer.setEnable("0");
					cnt++;
					if (!carTrainer.getDisplayName().startsWith("[DEL]")) {
						carTrainer.setDisplayName("[DEL]" + carTrainer.getDisplayName());
					}
				}
			}
		}
		
		saveTrainer(carTrainers);
		return cnt;
	}
	
}

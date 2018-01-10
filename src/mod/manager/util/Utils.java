package mod.manager.util;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;

import mod.manager.LogTextPanel;
import mod.manager.constant.Constant;
import mod.manager.constant.Status;
import mod.manager.vo.BrandArray;
import mod.manager.vo.CarArray;
import mod.manager.vo.CarExportArray;
import mod.manager.vo.CarExportVO;
import mod.manager.vo.CarVO;
import mod.manager.vo.CarZipArray;
import mod.manager.vo.CarZipVO;
import mod.manager.vo.ModArray;
import mod.manager.vo.ModVO;
import mod.manager.vo.ModZipArray;
import mod.manager.vo.ModZipVO;

/**
 * Utils
 */
public final class Utils {
	public static final Color DARK_GREEN = new Color(64, 173, 24);
	
	private static LogTextPanel logPanel;
	
	public static Properties prop = new Properties();
	private static Map<String, ModZipVO> modMap = new HashMap<>();
	private static Map<String, CarZipVO> carMap = new HashMap<>();
	
	public static void setLogTextPanel(LogTextPanel logPanel) {
		Utils.logPanel = logPanel;
	}
	
	/**
	 * 讀取properties
	 * 
	 * @return
	 */
	public static void loadProperties() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			File file = new File(Constant.CONFIG_FILE);
			log("load config.properties from: " + file.getAbsolutePath());
			//JOptionPane.showMessageDialog(null, file.getAbsolutePath());
			input = new FileInputStream(file);
			prop.load(input);
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Utils.prop = prop;
	}
	
	/**
	 * 保存properties
	 * 
	 * @param prop
	 */
	public static void saveProperties(Properties prop) {
		OutputStream output = null;
		try {
			File file = new File(Constant.CONFIG_FILE);
			log("save config.properties path: " + file.getAbsolutePath());
			//JOptionPane.showMessageDialog(null, file.getAbsolutePath());
			output = new FileOutputStream(file);
			prop.store(output, null);
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * log訊息
	 * 
	 * @param log
	 */
	public static void log(String log) {
		System.out.println(log);
		if (logPanel != null) {
			logPanel.log(log);
		}
	}
	
	/**
	 * 顯示alert訊息
	 * 
	 * @param msg
	 */
	public static void showAlert(String msg) {
		log(msg);
		JOptionPane.showMessageDialog(null, msg);
	}
	
	/**
	 * 顯示選擇視窗
	 * 
	 * @param msg
	 * @param title
	 * @return
	 */
	public static boolean showComfirm(String msg, String title) {
		int n = JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.YES_NO_OPTION);
		return n == JOptionPane.YES_OPTION;
	}
	
	/**
     * 使用 JAXB 將物件轉換為 XML (不需Annotation的版本)
     * 
     * NOTE: jaxbObject2XmlWithoutAnno,jaxbXml2ObjectWithoutAnno 此二函式
     * 可不需 annotation 在物件和XML互轉, 但遇到特定型態 (目前已知無法處理 java.util.List) 會轉換失敗
     * 請務必在使用前先進行測試
     * 
     * @param clz
     * @param obj
     * @param rootTag 產生的XML之Root tag名稱
     * @return
     */
    public static <T> String jaxbObject2XmlWithoutAnno(Class<T> clz, T obj, String rootTag, boolean formated_output) {
    	try {
	        JAXBContext jaxbContext = JAXBContext.newInstance(clz);
	        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	
	        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.valueOf(formated_output));
	        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
	
	        JAXBElement<T> je2 = new JAXBElement<T>(new QName(rootTag), clz, obj);
	
	        StringWriter sw = new StringWriter();
	        jaxbMarshaller.marshal(je2, sw);
	        return sw.toString();
    	} catch (Exception e) {
    		e.printStackTrace();
    		Utils.log(e.getMessage());
    		return "";
    	}
    }
    
    /**
     * 使用 JAXB 將 XML 字串轉換回物件 (不需Annotation的版本)
     * 
     * NOTE: jaxbObject2XmlWithoutAnno,jaxbXml2ObjectWithoutAnno 此二函式
     * 可不需 annotation 在物件和XML互轉, 但遇到特定型態 (目前已知無法處理 java.util.List) 會轉換失敗
     * 請務必在使用前先進行測試
     * 
     * @param clz
     * @param inputXml
     * @return
     * @throws Exception
     */
    public static <T> T jaxbXml2ObjectWithoutAnno(Class<T> clz, String inputXml) {
    	try {
	        JAXBContext jaxbContext = JAXBContext.newInstance(clz);
	        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	
	        JAXBElement<T> je1 = jaxbUnmarshaller.unmarshal(new StreamSource (new StringReader (inputXml)), clz);
	        T obj = je1.getValue();
	
	        return obj;
    	} catch (Exception e) {
    		e.printStackTrace();
    		log(e.getMessage());
    		return null;
    	}
    }
    
    /**
     * 將obj轉成xml
     * 
     * @param clazz
     * @param vo
     * @return
     */
    public static <T> String objToXml(Class<T> clazz, T vo) {
    	return jaxbObject2XmlWithoutAnno(clazz, vo, "MOD", true);
    }
    
    /**
     * 將xml轉成obj
     * 
     * @param clz
     * @param inputXml
     * @return
     */
    public static <T> T xmlToObj(Class<T> clz, String inputXml) {
    	return jaxbXml2ObjectWithoutAnno(clz, inputXml);
    }
    
    /**
     * 將VO寫成xml檔案.
     */
    public static <T> void writeXmlFile(String fileName, Class<T> clazz, T vo) throws Exception {
        // 將VO轉成xml字串
        String xml = jaxbObject2XmlWithoutAnno(clazz, vo, "MOD", true);
        
        // 輸出到檔案
        String filePath = prop.getProperty(Constant.MOD_DIR) + File.separator;
        File outputFile = new File(filePath, fileName);
        log("寫入xml: " + outputFile.getAbsolutePath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile);
            fos.write(xml.getBytes("utf-8"));
        } finally {
            try { if (fos != null) fos.close(); } catch (IOException e) {}
        }
    }
    
    /**
     * 從xml檔案取回VO.
     */
    public static <T> T readXmlFile(String fileName, Class<T> expectedType) throws Exception {
        // 從檔案讀取
        String filePath = prop.getProperty(Constant.MOD_DIR) + File.separator;
        File fromFile = new File(filePath, fileName);
        log("讀取xml: " + fromFile.getAbsolutePath());
        
        FileInputStream fis = null;
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            
            fis = new FileInputStream(fromFile);
            byte[] buf = new byte[1024];
            int size;
            while ((size = fis.read(buf)) != -1)
                byteStream.write(buf, 0, size);
            
            byte[] bytes = byteStream.toByteArray();
            String xml = new String(bytes, "UTF-8");
            return (T) jaxbXml2ObjectWithoutAnno(expectedType, xml);

        } finally {
            try { if (fis != null) fis.close(); } catch (IOException e) {}
        }
    }
    
    /**
     * 從xml讀取ModZipVO list
     * 
	 * @return
	 */
	public static List<ModZipVO> loadModZipList() {
		try {
			ModZipArray zipArray = Utils.readXmlFile(Constant.MODZIP_XML, ModZipArray.class);
			if (zipArray == null || zipArray.getZipArray() == null) {
				return new ArrayList<ModZipVO>();
			}
			for (ModZipVO zip : zipArray.getZipArray()) {
				File file = new File(zip.getFileUrl());
				if (file.exists()) {
					zip.setStatus(Status.OK);
				} else {
					log(zip.getFileUrl() + " is missing!");
					zip.setStatus(Status.MISS);
				}
			}
			return Arrays.asList(zipArray.getZipArray());
		} catch (Exception e) {
			e.printStackTrace();
			log("讀xml檔失敗: " + e.getMessage());
			return new ArrayList<ModZipVO>();
		}
	}
	
	/**
     * 從xml讀取CarZipVO list
     * 
	 * @return
	 */
	public static List<CarZipVO> loadCarZipList() {
		try {
			CarZipArray zipArray = Utils.readXmlFile(Constant.CARZIP_XML, CarZipArray.class);
			if (zipArray == null || zipArray.getZipArray() == null) {
				return new ArrayList<CarZipVO>();
			}
			for (CarZipVO zip : zipArray.getZipArray()) {
				File file = new File(zip.getFileUrl());
				if (file.exists()) {
					zip.setStatus(Status.OK);
				} else {
					log(zip.getFileUrl() + " is missing!");
					zip.setStatus(Status.MISS);
				}
			}
			return Arrays.asList(zipArray.getZipArray());
		} catch (Exception e) {
			e.printStackTrace();
			log("讀xml檔失敗: " + e.getMessage());
			return new ArrayList<CarZipVO>();
		}
	}
	
	/**
	 * 從xml讀取ModVO list
	 * 
	 * @return
	 */
	public static List<ModVO> loadModTable() {
		try {
			ModArray modArray = Utils.readXmlFile(Constant.MOD_XML, ModArray.class);
			if (modArray == null || modArray.getModArray() == null) {
				return new ArrayList<ModVO>();
			}
			String path = prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.MODS_FOLDER + File.separator;
			for (ModVO mod : modArray.getModArray()) {
				File file = new File(path, mod.getName());
				if (file.exists()) {
					mod.setStatus(Status.OK);
				} else {
					log("Mod folder " + mod.getName() + " is missing!");
					mod.setStatus(Status.MISS);
				}
			}
			return Arrays.asList(modArray.getModArray());
		} catch (Exception e) {
			e.printStackTrace();
			log("讀xml檔失敗: " + e.getMessage());
			return new ArrayList<ModVO>();
		}
	}
	
	/**
	 * 從xml讀取CarVO list
	 * 
	 * @return
	 */
	public static List<CarVO> loadCarTable() {
		try {
			CarArray modArray = Utils.readXmlFile(Constant.CAR_XML, CarArray.class);
			if (modArray == null || modArray.getCarArray() == null) {
				return new ArrayList<CarVO>();
			}
			String path = prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.CARS_FOLDER + File.separator;
			for (CarVO mod : modArray.getCarArray()) {
				File file = new File(path, mod.getName());
				if (file.exists()) {
					mod.setStatus(Status.OK);
				} else {
					log("Car Mod folder " + mod.getName() + " is missing!");
					mod.setStatus(Status.MISS);
				}
			}
			return Arrays.asList(modArray.getCarArray());
		} catch (Exception e) {
			e.printStackTrace();
			log("讀xml檔失敗: " + e.getMessage());
			return new ArrayList<CarVO>();
		}
	}
	
	/**
	 * 從xml讀取Brand list
	 * 
	 * @return
	 */
	public static List<String> loadBrands() {
		try {
			BrandArray brandArray = Utils.readXmlFile(Constant.BRAND_XML, BrandArray.class);
			if (brandArray == null || brandArray.getBrand() == null) {
				return new ArrayList<String>();
			}
			return Arrays.asList(brandArray.getBrand());
		} catch (Exception e) {
			e.printStackTrace();
			log("讀xml檔失敗: " + e.getMessage());
			return new ArrayList<String>();
		}
	}
	
	/**
	 * 保存Brand array至xml
	 * 
	 * @param brands
	 */
	public static void saveBrands(String[] brands) {
		BrandArray modArray = new BrandArray();
		modArray.setBrand(brands);
		try {
			Utils.writeXmlFile(Constant.BRAND_XML, BrandArray.class, modArray);
		} catch (Exception ex) {
			ex.printStackTrace();
			Utils.log("寫xml檔失敗: " + ex.getMessage());
		}
	}
	
	/**
	 * 保存ModZipVO array至xml
	 * 
	 * @param array
	 */
	public static void saveModZipList(ModZipVO[] array) {
		ModZipArray zipArray = new ModZipArray();
		zipArray.setZipArray(array);
		try {
			buildModMap(Arrays.asList(array));
			Utils.writeXmlFile(Constant.MODZIP_XML, ModZipArray.class, zipArray);
		} catch (Exception ex) {
			ex.printStackTrace();
			Utils.log("寫xml檔失敗: " + ex.getMessage());
		}
	}
	
	/**
	 * 保存CarZipVO array至xml
	 * 
	 * @param array
	 */
	public static void saveCarZipList(CarZipVO[] array) {
		CarZipArray zipArray = new CarZipArray();
		zipArray.setZipArray(array);
		try {
			buildCarMap(Arrays.asList(array));
			Utils.writeXmlFile(Constant.CARZIP_XML, CarZipArray.class, zipArray);
		} catch (Exception ex) {
			ex.printStackTrace();
			Utils.log("寫xml檔失敗: " + ex.getMessage());
		}
	}
	
	/**
	 * 保存ModZipVO list至xml
	 * 
	 * @param list
	 */
	public static void saveModZipList(List<ModZipVO> list) {
		saveModZipList(list.toArray(new ModZipVO[list.size()]));
	}
	
	/**
	 * 保存CarZipVO list至xml
	 * 
	 * @param list
	 */
	public static void saveCarZipList(List<CarZipVO> list) {
		saveCarZipList(list.toArray(new CarZipVO[list.size()]));
	}
	
	/**
	 * 保存ModVO array至xml
	 * 
	 * @param array
	 */
	public static void saveModTable(ModVO[] array) {
		ModArray modArray = new ModArray();
		modArray.setModArray(array);
		try {
			Utils.writeXmlFile(Constant.MOD_XML, ModArray.class, modArray);
		} catch (Exception ex) {
			ex.printStackTrace();
			Utils.log("寫xml檔失敗: " + ex.getMessage());
		}
	}
	
	/**
	 * 保存CarVO array至xml
	 * 
	 * @param array
	 */
	public static void saveCarTable(CarVO[] array) {
		CarArray modArray = new CarArray();
		modArray.setCarArray(array);
		try {
			Utils.writeXmlFile(Constant.CAR_XML, CarArray.class, modArray);
		} catch (Exception ex) {
			ex.printStackTrace();
			Utils.log("寫xml檔失敗: " + ex.getMessage());
		}
	}
	
	/**
	 * 保存ModVO list至xml
	 * 
	 * @param list
	 */
	public static void saveModTable(List<ModVO> list) {
		saveModTable(list.toArray(new ModVO[list.size()]));
	}
	
	/**
	 * 保存CarVO list至xml
	 * 
	 * @param list
	 */
	public static void saveCarTable(List<CarVO> list) {
		saveCarTable(list.toArray(new CarVO[list.size()]));
	}
	
	/**
	 * 顯示新的Frame並且置中
	 * 
	 * @param frame
	 */
	public static void showCenterFrame(JFrame frame) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2,
                          (screenSize.height - frameSize.height) / 2);
        frame.setVisible(true);
	}
	
	/**
	 * 取得檔名(不含副檔名)
	 * 
	 * @param name
	 * @return
	 */
	public static String getFileName(String name) {
		try {
			int index = name.lastIndexOf(46); // 抓小數點.
			return name.substring(0, index);
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 檢查Mod檔案是否存在
	 * 
	 * @param mod
	 * @return
	 */
	public static Status checkModStatus(ModVO mod) {
		String path = Utils.prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.MODS_FOLDER + File.separator;
		File file = new File(path, mod.getName());
		if (file.exists() && file.isDirectory()) {
			
			//檢查遊戲目錄下的mod資料
			if (mod.getFileArray() != null && mod.isEnable()) {
				boolean isLost = false;
				for (String url : mod.getFileArray()) {
					File urlFile = new File(url);
					if (!urlFile.exists()) {
						isLost = true;
						log(urlFile.getAbsolutePath() + " is lost.");
					}
				}
				if (isLost) {
					return Status.LOST;
				}
			}
			
			return Status.OK;
		} else {
			log("Mod: " + mod.getName() + " folder is missing!");
			return Status.MISS;
		}
	}
	
	/**
	 * 檢查Car Mod檔案是否存在
	 * 
	 * @param mod
	 * @return
	 */
	public static Status checkCarStatus(CarVO mod) {
		String path = Utils.prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.CARS_FOLDER + File.separator;
		File file = new File(path, mod.getName());
		if (file.exists() && file.isDirectory()) {
			File modFile = new File(path + mod.getName() + File.separator + mod.getDlcName(), "dlc.rpf");
			//檢查遊戲目錄下的mod資料
			if (mod.getFileArray() != null && mod.isEnable()) {
				boolean isLost = false;
				boolean isOldVer = false;
				boolean isNewVer = false;
				for (String url : mod.getFileArray()) {
					File urlFile = new File(url);
					if (!urlFile.exists()) {
						isLost = true;
						log(urlFile.getAbsolutePath() + " is lost.");
					} else {
						if (modFile.exists()) {
							//比較Mod資料夾跟遊戲資料夾下哪個檔案新
							if (modFile.getName().equals(urlFile.getName())) {
								if (modFile.lastModified() > urlFile.lastModified()) {
									//Mod資料夾較新
									isNewVer = true;
								} else if (modFile.lastModified() < urlFile.lastModified()) {
									//遊戲資料夾較新
									isOldVer = true;
								}
							}
						}
					}
				}
				if (isLost) {
					return Status.LOST;
				} else if (isNewVer) {
					return Status.NEWVER;
				} else if (isOldVer) {
					return Status.OLDVER;
				}
			}
			
			return Status.OK;
		} else {
			log("Car Mod: " + mod.getName() + " folder is missing!");
			return Status.MISS;
		}
	}
	
	/**
	 * 複製Mod檔案至遊戲目錄下
	 * 
	 * @param mod
	 */
	public static void copyModDir(ModVO mod) {
		String srcDir = prop.getProperty(Constant.MOD_DIR) + File.separator + 
				Constant.MODS_FOLDER + File.separator + mod.getName();
		File src = new File(srcDir);
		String destDir = prop.getProperty(Constant.GAME_DIR);
		File dest = new File(destDir);
		try {
			List<String> files = new ArrayList<>();
			FileUtils.copyDirectory(src, dest, new FileFilter() {
				@Override
				public boolean accept(File file) {
					if (file.isDirectory() && "readme".equalsIgnoreCase(file.getName())) {
						return false;
					}
					if (file.isDirectory() && "modcover".equalsIgnoreCase(file.getName())) {
						return false;
					}
					
					String url = file.getAbsolutePath();
					url = url.substring(src.getAbsolutePath().length(), url.length());
					String target = dest.getAbsolutePath() + url;
					log("Copy " + file.getAbsolutePath() + " to " + target);
					
					if (file.isFile()) {
						//保存複製的檔案路徑, 以便到時檢查或刪除mod資料
						files.add(target);
					}
					return true;
				}
			});
			
			mod.setFileArray(files.toArray(new String[files.size()]));
		} catch (IOException e) {
			e.printStackTrace();
			log(e.getMessage());
		}
	}
	
	/**
	 * 複製Car Mod檔案至遊戲目錄下
	 * 
	 * @param mod
	 */
	public static void copyCarDir(CarVO mod) {
		String srcDir = prop.getProperty(Constant.MOD_DIR) + File.separator + 
				Constant.CARS_FOLDER + File.separator + mod.getName();
		File src = new File(srcDir);
		String destDir = prop.getProperty(Constant.GAME_DIR) + File.separator + Constant.CARS_MOD_GAMEDIR;
		File dest = new File(destDir);
		try {
			List<String> files = new ArrayList<>();
			FileUtils.copyDirectory(src, dest, new FileFilter() {
				@Override
				public boolean accept(File file) {
					if (file.isDirectory() && "readme".equalsIgnoreCase(file.getName())) {
						return false;
					}
					if (file.isDirectory() && "modcover".equalsIgnoreCase(file.getName())) {
						return false;
					}
					
					String url = file.getAbsolutePath();
					url = url.substring(src.getAbsolutePath().length(), url.length());
					String target = dest.getAbsolutePath() + url;
					log("Copy " + file.getAbsolutePath() + " to " + target);
					
					if (file.isFile()) {
						//保存複製的檔案路徑, 以便到時檢查或刪除mod資料
						files.add(target);
					}
					return true;
				}
			});
			
			mod.setFileArray(files.toArray(new String[files.size()]));
		} catch (IOException e) {
			e.printStackTrace();
			log(e.getMessage());
		}
	}
	
	/**
	 * 把車輛壓縮檔複製到車輛壓縮檔管理目錄下
	 * 
	 * @param zip
	 * @return
	 */
	public static File copyCarZip(File zip) {
		String destDir = prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.CAR_ZIP_FOLDER;
		File dest = new File(destDir, zip.getName());
		try {
			if (!zip.getAbsolutePath().equals(dest.getAbsolutePath())) {
				FileUtils.copyFile(zip, dest);
			}
			return dest;
		} catch (IOException e) {
			e.printStackTrace();
			log(e.getMessage());
			return null;
		}
	}
	
	/**
	 * 把Mod壓縮檔複製到Mod壓縮檔管理目錄下
	 * 
	 * @param zip
	 * @return
	 */
	public static File copyModZip(File zip) {
		String destDir = prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.ZIP_FOLDER;
		File dest = new File(destDir, zip.getName());
		try {
			if (!zip.getAbsolutePath().equals(dest.getAbsolutePath())) {
				FileUtils.copyFile(zip, dest);
			}
			return dest;
		} catch (IOException e) {
			e.printStackTrace();
			log(e.getMessage());
			return null;
		}
	}
	
	/**
	 * 把MOD資料從遊戲目錄底下備份回MOD資料夾
	 * 
	 * @param mod
	 */
	public static void backupCarDir(CarVO mod) {
		String srcDir = prop.getProperty(Constant.GAME_DIR) + File.separator + Constant.CARS_MOD_GAMEDIR + File.separator;
		File src = new File(srcDir, mod.getDlcName());
		String destDir = prop.getProperty(Constant.MOD_DIR) + File.separator + 
				Constant.CARS_FOLDER + File.separator + mod.getName();
		File dest = new File(destDir, mod.getDlcName());
		try {
			FileUtils.copyDirectory(src, dest);
		} catch (IOException e) {
			e.printStackTrace();
			log(e.getMessage());
		}
	}
	
	/**
	 * 從遊戲目錄中移除mod資料
	 * 
	 * @param mod
	 */
	public static void removeModData(ModVO mod) {
		if (mod.getFileArray() != null) {
			for (String url : mod.getFileArray()) {
				File file = new File(url);
				if (file.exists()) {
					file.delete();
					log("delete " + file.getAbsolutePath());
				}
			}
		}
	}
	
	/**
	 * 從遊戲目錄中移除car mod資料
	 * 
	 * @param mod
	 */
	public static void removeCarData(CarVO mod) {
		if (mod.getFileArray() != null) {
			for (String url : mod.getFileArray()) {
				File file = new File(url);
				if (file.exists()) {
					file.delete();
					log("delete " + file.getAbsolutePath());
				}
			}
		}
	}
	
	/**
     * Date 轉為格式字串, 格式由傳入 pattern定義
     * yyyy 表示年
     * MM 表示月
     * dd 表示日
     * HH 表示時
     * mm 表示分
     * ss 表示秒
     * 如: pattern= "yyyy-MM-dd HH:mm:ss"
     * 
     * @param date Date 物件, 若為null, 則回傳空白字串
     * @param pattern 欲得到的字串格式
     * @return 回傳格式化字串
     */
    public static final String dateTimeToString(final Date date, final String pattern) {
        if(date == null) return "";
        if(pattern == null || pattern.trim().length() == 0) return "";
        SimpleDateFormat sdm = new SimpleDateFormat(pattern);
        return sdm.format(date);
    }
    
    /**
     * @param uri
     */
    public static void open(URI uri) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(uri);
			} catch (IOException e) {
				e.printStackTrace();
				Utils.log(e.getMessage());
			}
		}
	}
    
    /**
     * 建立車輛Mod壓縮檔名稱與html的對應map, 方便之後建立同名車輛Mod壓縮檔能自動填入html
     * 
     * @param zips
     */
    public static void buildCarMap(List<CarZipVO> zips) {
    	carMap.clear();
    	for (CarZipVO zip : zips) {
    		String name = zip.getName();
    		name = name.substring(0, name.length() - 4); //去掉.zip
    		name = name.replaceAll("_|\\.|-", "");
    		if (zip.getVersion() != null && zip.getVersion().length() > 0) {
    			String version = zip.getVersion().replaceAll("_|\\.|-", "");
    			if (name.endsWith(version)) {
    				name = name.substring(0, name.length() - zip.getVersion().length()); //去掉-version
    			}
    		}
			carMap.put(name, zip);
    	}
    }
    
    /**
     * @param zipName
     * @return
     */
    public static CarZipVO getCarMappedHtml(String zipName) {
    	int index = zipName.lastIndexOf('-');
    	zipName = zipName.substring(0, index);
    	//zipName = zipName.substring(0, zipName.length() - 4); //去掉.zip
    	zipName = zipName.replaceAll("_|\\.|-", "");
		for (Entry<String, CarZipVO> entry : carMap.entrySet()) {
			if (zipName.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}
    	return null;
    }
    
    /**
     * 建立Mod壓縮檔名稱與html的對應map, 方便之後建立同名車輛Mod壓縮檔能自動填入html
     * 
     * @param zips
     */
    public static void buildModMap(List<ModZipVO> zips) {
    	modMap.clear();
    	for (ModZipVO zip : zips) {
    		String name = zip.getName();
    		name = name.substring(0, name.length() - 4); //去掉.zip
    		name = name.replaceAll("_|\\.|-", "");
    		if (zip.getVersion() != null && zip.getVersion().length() > 0) {
    			String version = zip.getVersion().replaceAll("_|\\.|-", "");
    			if (name.endsWith(version)) {
    				name = name.substring(0, name.length() - version.length()); //去掉-version
    			}
    		}
			modMap.put(name, zip);
    	}
    }
    
    /**
     * @param zipName
     * @return
     */
    public static ModZipVO getModMappedHtml(String zipName) {
    	zipName = zipName.substring(0, zipName.length() - 4); //去掉.zip
    	zipName = zipName.replaceAll("_|\\.|-", "");
		for (Entry<String, ModZipVO> entry : modMap.entrySet()) {
			if (zipName.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}
    	return null;
    }
    
    /**
     * 取得車輛Mod壓縮檔存放位置, /download_car/
     * 
     * @return
     */
    public static String getCarZipDir() {
    	return prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.CAR_ZIP_FOLDER;
    }
    
    /**
     * 取得車輛Mod壓縮檔存放位置, /download_car/zip.name
     * 
     * @param car
     * @return
     */
    public static String getCarZipDir(CarZipVO zip) {
    	return prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.CAR_ZIP_FOLDER + File.separator + zip.getName();
    }
    
    /**
     * 取得車輛Mod資料夾存放位置, /mods_car/
     * 
     * @return
     */
    public static String getCarDir() {
    	return prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.CARS_FOLDER;
    }
    
    /**
     * 取得車輛Mod資料夾存放位置, /mods_car/car.name
     * 
     * @param car
     * @return
     */
    public static String getCarDir(CarVO car) {
    	return prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.CARS_FOLDER + File.separator + car.getName();
    }
    
    /**
     * 取得Mod資料夾存放位置, /mods/
     * 
     * @return
     */
    public static String getModDir() {
    	return prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.MODS_FOLDER;
    }
    
    /**
     * 取得Mod資料夾存放位置, /mods/mod.name
     * 
     * @param mod
     * @return
     */
    public static String getModDir(ModVO mod) {
    	return prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.MODS_FOLDER + File.separator + mod.getName();
    }
    
    /**
     * 取得Mod壓縮檔存放位置, /download/
     * 
     * @return
     */
    public static String getModZipDir() {
    	return prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.ZIP_FOLDER;
    }
    
    /**
     * 取得Mod壓縮檔存放位置, /download/zip.name
     * 
     * @param zip
     * @return
     */
    public static String getModZipDir(ModZipVO zip) {
    	return prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.ZIP_FOLDER + File.separator + zip.getName();
    }
    
    /**
     * @param file
     * @return
     */
    public static String getVersion(String file) {
    	try {
    		int s = file.lastIndexOf('-');
    		int e = file.lastIndexOf('.');
    		
    		return file.substring(s + 1, e);
    	} catch (Exception e) {
    		return "";
    	}
    }
    
    /**
     * @param mod
     * @return
     */
    public static ImageIcon loadCover(ModVO mod) {
    	String path = prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.MODS_FOLDER + File.separator + 
    			mod.getName() + File.separator + Constant.COVER_FOLDER + File.separator + Constant.COVER_FILE;
    	ImageIcon icon = new ImageIcon(path);
    	return new ImageIcon(icon.getImage().getScaledInstance(130, 70, java.awt.Image.SCALE_SMOOTH));
    }
    
    /**
     * @param mod
     * @return
     */
    public static ImageIcon loadCover(CarVO car) {
    	String path = prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.CARS_FOLDER + File.separator + 
    			car.getName() + File.separator + Constant.COVER_FOLDER + File.separator + Constant.COVER_FILE;
    	ImageIcon icon = new ImageIcon(path);
    	return new ImageIcon(icon.getImage().getScaledInstance(130, 70, java.awt.Image.SCALE_SMOOTH));
    }
    
    /**
     * @param mod
     * @return
     */
    public static ImageIcon loadFullCover(ModVO mod) {
    	String path = prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.MODS_FOLDER + File.separator + 
    			mod.getName() + File.separator + Constant.COVER_FOLDER + File.separator + Constant.COVER_FILE;
    	ImageIcon icon = new ImageIcon(path);
    	return new ImageIcon(icon.getImage().getScaledInstance(800, 487, java.awt.Image.SCALE_SMOOTH));
    }
    
    /**
     * @param mod
     * @return
     */
    public static ImageIcon loadFullCover(CarVO car) {
    	String path = prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.CARS_FOLDER + File.separator + 
    			car.getName() + File.separator + Constant.COVER_FOLDER + File.separator + Constant.COVER_FILE;
    	ImageIcon icon = new ImageIcon(path);
    	return new ImageIcon(icon.getImage().getScaledInstance(800, 487, java.awt.Image.SCALE_SMOOTH));
    }
    
    /**
     * 取得車輛呼叫名稱list, 用逗點','隔開
     * 
     * @param callName
     * @return
     */
    public static List<String> getCallNames(String callName) {
    	List<String> names = new ArrayList<String>();
    	if (callName == null || callName.length() == 0) {
    		return names;
    	}
    	
    	if (callName.indexOf(',') == -1) {
    		//不包含,
    		names.add(callName);
    	} else {
    		String[] result = callName.split(",");
    		for (String name : result) {
    			if (name != null && name.length() > 0) {
    				names.add(name);
    			}
    		}
    	}
    	
    	return names;
    }
    
    /**
     * 匯出車輛啟用設定XML
     * 
     * @param cars
     * @param output
     */
    public static void exportCarXml(File output) {
    	List<CarVO> cars = loadCarTable();
    	List<CarExportVO> exports = new ArrayList<CarExportVO>();
    	
    	for (CarVO car : cars) {
    		CarExportVO export = new CarExportVO();
    		export.setEnable(car.isEnable());
    		export.setName(car.getName());
    		
    		exports.add(export);
    	}
    	CarExportArray array = new CarExportArray();
    	array.setCarArray(exports.toArray(new CarExportVO[exports.size()]));
    	
    	// 將VO轉成xml字串
        String xml = jaxbObject2XmlWithoutAnno(CarExportArray.class, array, "MOD", true);
        
        // 輸出到檔案
        log("寫入xml: " + output);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(output);
            fos.write(xml.getBytes("utf-8"));
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            try { if (fos != null) fos.close(); } catch (IOException e) {}
        }
    }
    
    /**
     * 從匯出的Car Xml取得啟用資料
     * 
     * @param input
     * @return
     */
    public static List<CarExportVO> importCarXml(File input) {
    	List<CarExportVO> carExports = new ArrayList<CarExportVO>();
    	
    	FileInputStream fis = null;
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            
            fis = new FileInputStream(input);
            byte[] buf = new byte[1024];
            int size;
            while ((size = fis.read(buf)) != -1)
                byteStream.write(buf, 0, size);
            
            byte[] bytes = byteStream.toByteArray();
            String xml = new String(bytes, "UTF-8");
            CarExportArray array = (CarExportArray) jaxbXml2ObjectWithoutAnno(CarExportArray.class, xml);
            return Arrays.asList(array.getCarArray());

        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            try { if (fis != null) fis.close(); } catch (IOException e) {}
        }
    	
    	return carExports;
    }
}

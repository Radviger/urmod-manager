package mod.manager.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import mod.manager.constant.Constant;

/**
 *
 */
public final class DlcListUtils {

	/**
	 * @param dlcName
	 */
	public static void addDlc(String dlcName) {
		String path = Utils.prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.OPENIV_DLCLIST_DIR;
		File dlcFile = new File(path, Constant.DLCLIST_FILE);
		
		FileInputStream fis = null;
		StringBuffer content = new StringBuffer();
		try {
			fis = new FileInputStream(dlcFile);
			try (Scanner scanner = new Scanner(fis, "BIG5")) {
				boolean exist = false;
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if (line.contains("<Item>dlcpacks:\\" + dlcName + "\\</Item>")) {
						exist = true;
					} else if (line.contains("</Paths>")) {
						if (!exist) {
							content.append("\t\t").append("<Item>dlcpacks:\\").append(dlcName).append("\\</Item>\r\n");
						}
					}
					content.append(line).append("\r\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            try { if (fis != null) fis.close(); } catch (IOException e) {}
        }
		
		FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dlcFile);
            fos.write(content.toString().getBytes("BIG5"));
        } catch (Exception e) {
			e.printStackTrace();
		} finally {
            try { if (fos != null) fos.close(); } catch (IOException e) {}
        }
		
	}
	
	/**
	 * @param dlcName
	 */
	public static void delDlc(String dlcName) {
		String path = Utils.prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.OPENIV_DLCLIST_DIR;
		File dlcFile = new File(path, Constant.DLCLIST_FILE);
		
		FileInputStream fis = null;
		StringBuffer content = new StringBuffer();
		try {
			fis = new FileInputStream(dlcFile);
			try (Scanner scanner = new Scanner(fis, "BIG5")) {
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if (!line.contains("<Item>dlcpacks:\\" + dlcName + "\\</Item>")) {
						content.append(line).append("\r\n");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            try { if (fis != null) fis.close(); } catch (IOException e) {}
        }
		
		FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dlcFile);
            fos.write(content.toString().getBytes("BIG5"));
        } catch (Exception e) {
			e.printStackTrace();
		} finally {
            try { if (fos != null) fos.close(); } catch (IOException e) {}
        }
	}
	
	
}

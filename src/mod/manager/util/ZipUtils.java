package mod.manager.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import mod.manager.constant.Constant;

/**
 *
 */
public final class ZipUtils {

	/**
	 * 解壓縮至mods目錄
	 * 
	 * @param zip
	 * @param dir
	 */
	public static void unzip(String zip, String dir) {
		try {
			File zipFile = new File(zip);
			if (!checkZipFile(zipFile)) {
				Utils.showAlert(zipFile.getName() + " 為不支援解壓縮格式!");
				return;
			}
			String destination = Utils.prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.MODS_FOLDER + File.separator + dir;
			File destDir = new File(destination);
			List<String> result = unZip(zipFile, destDir);
			for (String file : result) {
				Utils.log(file);
			}
			Utils.log("Mod: " + dir + " 安裝至 " + destination);
		} catch (IOException e) {
	        e.printStackTrace();
	        Utils.log(e.getMessage());
	    }
	}
	
	/**
	 * 解壓縮Car至car mods目錄
	 * 
	 * @param zip
	 * @param dir
	 */
	public static void unzipCar(String zip, String dir) {
		try {
			File zipFile = new File(zip);
			if (!checkZipFile(zipFile)) {
				Utils.showAlert(zipFile.getName() + " 為不支援解壓縮格式!");
				return;
			}
			String destination = Utils.prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.CARS_FOLDER + File.separator + dir;
			File destDir = new File(destination);
			List<String> result = unZip(zipFile, destDir);
			for (String file : result) {
				Utils.log(file);
			}
			Utils.log("CarMod: " + dir + " 安裝至 " + destination);
		} catch (IOException e) {
	        e.printStackTrace();
	        Utils.log(e.getMessage());
	    }
	}
	
	/**
	 * 檢查檔案名稱是否為可解壓縮的檔案格式
	 * 
	 * @param zip
	 * @return
	 */
	public static boolean checkZipFile(File zip) {
		if (zip.isFile()) {
			if (zip.getName().endsWith("zip")) {
				return true;
			} else if (zip.getName().endsWith("7z")) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @param zipFile
	 * @param directory
	 * @return
	 * @throws IOException
	 */
	public static List<String> unZip(File zipFile, File directory) throws IOException {
	    List<String> result = new ArrayList<String>();
	    
	    if (zipFile.getName().endsWith("zip")) {
	    	InputStream inputStream = new FileInputStream(zipFile);
	    	ZipArchiveInputStream in = new ZipArchiveInputStream(inputStream);
		    ZipArchiveEntry entry = in.getNextZipEntry();
		    while (entry != null) {
		        if (entry.isDirectory()) {
		            entry = in.getNextZipEntry();
		            continue;
		        }
		        File curfile = new File(directory, entry.getName());
		        File parent = curfile.getParentFile();
		        if (!parent.exists()) {
		            parent.mkdirs();
		        }
		        OutputStream out = new FileOutputStream(curfile);
		        IOUtils.copy(in, out);
		        out.close();
		        result.add(entry.getName());
		        entry = in.getNextZipEntry();
		    }
		    in.close();
	    } else if (zipFile.getName().endsWith("7z")) {
	    	SevenZFile sevenZFile = new SevenZFile(zipFile);
	    	SevenZArchiveEntry entry = sevenZFile.getNextEntry();
		    while (entry != null) {
		        if (entry.isDirectory()) {
		            entry = sevenZFile.getNextEntry();
		            continue;
		        }
		        File curfile = new File(directory, entry.getName());
		        File parent = curfile.getParentFile();
		        if (!parent.exists()) {
		            parent.mkdirs();
		        }
		        OutputStream out = new FileOutputStream(curfile);
		        byte[] content = new byte[(int) entry.getSize()];
		        sevenZFile.read(content, 0, content.length);
		        out.write(content);
		        out.close();
		        entry = sevenZFile.getNextEntry();
		    }
		    sevenZFile.close();
		}  
//		} else if (zipFile.getName().endsWith("7z")) {
//	    	SevenZFile sevenZFile = new SevenZFile(zipFile);
//	    	SevenZArchiveEntry entry = sevenZFile.getNextEntry();
//		    while (entry != null) {
//		        if (entry.isDirectory()) {
//		            entry = sevenZFile.getNextEntry();
//		            continue;
//		        }
//		        File curfile = new File(directory, entry.getName());
//		        File parent = curfile.getParentFile();
//		        if (!parent.exists()) {
//		            parent.mkdirs();
//		        }
//		        OutputStream out = new FileOutputStream(curfile);
//		        IOUtils.copy(new InputStream() {
//                    public int read() throws IOException {
//                        return sevenZFile.read();
//                    }
//                    public int read(byte[] b) throws IOException {
//                        return sevenZFile.read(b);
//                    }
//                    public void close() throws IOException {
//                    	sevenZFile.close();
//                    }
//                    protected void finalize() throws Throwable {
//                        try {
//                            close();
//                        } finally {
//                            super.finalize();
//                        }
//                    }
//                }, out);
//		        out.close();
//		        result.add(entry.getName());
//		        entry = sevenZFile.getNextEntry();
//		    }
//		    sevenZFile.close();
//		}
	    
	    return result;
	}
	
    public static File zip(File file, String zipPath, String encoding) {
        try {
            FileOutputStream f = new FileOutputStream(zipPath);
            ZipArchiveOutputStream zos = (ZipArchiveOutputStream) new ArchiveStreamFactory()
                    .createArchiveOutputStream(ArchiveStreamFactory.ZIP, f);
            if (null != encoding) {
                zos.setEncoding(encoding);
            }
            String fileName = file.getName();
            ZipArchiveEntry entry = new ZipArchiveEntry(fileName);
            zos.putArchiveEntry(entry);
            FileInputStream fis = new FileInputStream(file);
            IOUtils.copy(fis, zos);
            fis.close();
            zos.closeArchiveEntry();
            zos.close();
            return new File(zipPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

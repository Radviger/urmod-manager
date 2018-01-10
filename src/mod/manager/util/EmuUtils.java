package mod.manager.util;

import java.io.File;

public class EmuUtils {

    private static final String destURL = "D:\\NES\\newRoms";
    private static final String tempURL = "D:\\NES\\tempRoms";
    private static final String sourceURL = "D:\\NES\\roms";
    
    public EmuUtils() {
        
    }
    
    private void transName(File source) {
        if (source.isDirectory()) {
            for (File file : source.listFiles()) {
                transName(file);
            }
        } else {
            try {
                //unzip
                String chtName = getFileName(source);
                File zipDir = new File(tempURL + "\\" + chtName);
                ZipUtils.unZip(source, zipDir);
                
                //get file name
                String engName = "";
                File romFile = null;
                for (File file : zipDir.listFiles()) {
                    if (file.getName().toLowerCase().endsWith(".nes") || file.getName().toLowerCase().endsWith(".fds")) {
                        engName = getFileName(file);
                        romFile = file;
                        break;
                    }
                }
                
                if (romFile == null) {
                    System.err.println("error source: " + source.getName());
                    return;
                }
                
                //zip
                String zipPath = destURL + "\\" + engName + "-" + chtName + ".zip";
                System.out.println(source.getAbsolutePath() + "\\" + source.getName() + " -> " + zipPath);
                ZipUtils.zip(romFile, zipPath, null);
                
            } catch (Exception e) {
                System.err.println("error source: " + source.getName());
                e.printStackTrace();
            }
        }
    }
    
    
    
    public static void main(String[] args) {
        EmuUtils util = new EmuUtils();
        File source = new File(sourceURL);
        util.transName(source);
    }

    private String getFileName(File file) {
        String name = file.getName();
        return name.substring(0, name.lastIndexOf("."));
    }
    
}

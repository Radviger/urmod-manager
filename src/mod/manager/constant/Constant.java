package mod.manager.constant;

import java.io.File;

/**
 *
 */
public class Constant {

	//config.properties
	public static final String CONFIG_FILE = "config.properties";
	public static final String GAME_DIR = "game.dir";
	public static final String MOD_DIR = "mod.dir";
	public static final String USE_PROXY = "use.proxy";
	public static final String PROXY_HOST = "proxy.host";
	public static final String PROXY_PORT = "proxy.port";
	
	//default folder name
	public static final String ZIP_FOLDER = "download";
	public static final String MODS_FOLDER = "mods";
	public static final String README_FOLDER = "readme";
	public static final String COVER_FOLDER = "modcover";
	public static final String CAR_ZIP_FOLDER = "download_car";
	public static final String CARS_FOLDER = "mods_car";
	public static final String EXPORT_FOLDER = "export";
	public static final String CARS_MOD_GAMEDIR = 
		"mods" + File.separator + "update" + File.separator + "x64" + File.separator + "dlcpacks" + File.separator;
	public static final String OPENIV_DLCLIST_DIR = "openiv_files" + File.separator + "dlclist";
	public static final String TRAINER_FILE = "trainerv.ini";
	public static final String DLCLIST_FILE = "dlclist.xml";
	public static final String COVER_FILE = "cover-img.jpg";
	
	//xml name
	public static final String MODZIP_XML = "modzip.xml";
	public static final String MOD_XML = "mod.xml";
	public static final String CARZIP_XML = "carzip.xml";
	public static final String CAR_XML = "car.xml";
	public static final String BRAND_XML = "brand.xml";
}

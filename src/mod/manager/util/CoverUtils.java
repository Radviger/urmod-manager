package mod.manager.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import mod.manager.constant.Constant;
import mod.manager.vo.CarVO;
import mod.manager.vo.ModVO;

/**
 *
 */
public final class CoverUtils {

	/**
	 * @param modVO
	 */
	public static String loadVersion(String url) {
		try {
			boolean useProxy = "true".equals(Utils.prop.get(Constant.USE_PROXY));
			if (useProxy) {
				System.setProperty("https.proxyHost", String.valueOf(Utils.prop.get(Constant.PROXY_HOST)));
				System.setProperty("https.proxyPort", String.valueOf(Utils.prop.get(Constant.PROXY_PORT)));
			}
			
			Utils.log("visit " + url + " ...");
			Document doc = Jsoup.connect(url).userAgent("Chrome").timeout(10000).get(); // visit a url
			//System.out.println(doc.toString());
			Element div = doc.select("span.version").first();
			
			//Element div = userAgent.doc.findFirst("<span class=\"version\">");
			String version = div.text();
			if (version == null || version.trim().length() == 0) {
				version = "1.0";
			}
			if (version.startsWith("v") || version.startsWith("V")) {
				version = version.substring(1, version.length());
			}
			Utils.log("get version: " + version);
			
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @param modVO
	 */
	public static void loadCover(ModVO modVO) {
		try {
			boolean useProxy = "true".equals(Utils.prop.get(Constant.USE_PROXY));
			if (useProxy) {
				System.setProperty("https.proxyHost", String.valueOf(Utils.prop.get(Constant.PROXY_HOST)));
				System.setProperty("https.proxyPort", String.valueOf(Utils.prop.get(Constant.PROXY_PORT)));
			}
			Utils.log("visit " + modVO.getHtml() + " ...");
			Document doc = Jsoup.connect(modVO.getHtml()).userAgent("Chrome").timeout(10000).get(); // visit a url
			//System.out.println(doc.toString());
			
			Element div = doc.select("div#file-media").first();
			Element img = div.select("img").first();
			String url = img.attr("src");
			Utils.log("img src=" + url);
			//Element div = userAgent.doc.findFirst("<div class=file-images>");
			//Element img = div.findFirst("<img>");
			//String url = img.getAtString("src");
			
			String path = Utils.prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.MODS_FOLDER + File.separator + 
	    			modVO.getName() + File.separator + Constant.COVER_FOLDER;
			
			downloadImage(url, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param carVO
	 */
	public static void loadCover(CarVO carVO) {
		try {
			boolean useProxy = "true".equals(Utils.prop.get(Constant.USE_PROXY));
			if (useProxy) {
				System.setProperty("https.proxyHost", String.valueOf(Utils.prop.get(Constant.PROXY_HOST)));
				System.setProperty("https.proxyPort", String.valueOf(Utils.prop.get(Constant.PROXY_PORT)));
			}
			Utils.log("visit " + carVO.getHtml() + " ...");
			Document doc = Jsoup.connect(carVO.getHtml()).userAgent("Chrome").timeout(10000).get(); // visit a url
			//System.out.println(doc.toString());

			Element div = doc.select("div#file-media").first();
			Element img = div.select("img").first();
			String url = img.attr("src");
			Utils.log("img src=" + url);
			
			String path = Utils.prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.CARS_FOLDER + File.separator + 
					carVO.getName() + File.separator + Constant.COVER_FOLDER;
			
			downloadImage(url, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param sourceUrl
	 * @param targetDirectory
	 * @throws Exception
	 */
	public static void downloadImage(String sourceUrl, String targetDirectory) throws Exception {
		boolean useProxy = "true".equals(Utils.prop.get(Constant.USE_PROXY));
		if (useProxy) {
			System.setProperty("https.proxyHost", String.valueOf(Utils.prop.get(Constant.PROXY_HOST)));
			System.setProperty("https.proxyPort", String.valueOf(Utils.prop.get(Constant.PROXY_PORT)));
		}
		
		Response resultImageResponse = null;
		try {
			Utils.log("download img = " + sourceUrl);
			resultImageResponse = Jsoup.connect(sourceUrl).userAgent("Chrome").timeout(10000).ignoreContentType(true).execute();
		} catch (Exception e) {
			e.printStackTrace();
			
			try {
				String filename = sourceUrl.substring(sourceUrl.lastIndexOf('/') + 1, sourceUrl.length());
				filename = java.net.URLEncoder.encode(filename, "UTF-8");
				sourceUrl = sourceUrl.substring(0, sourceUrl.lastIndexOf('/') + 1) + filename;
				Utils.log("download img = " + sourceUrl);
				resultImageResponse = Jsoup.connect(sourceUrl).userAgent("Chrome").timeout(10000).ignoreContentType(true).execute();
			} catch (Exception ex) {
				ex.printStackTrace();
				
				String filename = sourceUrl.substring(sourceUrl.lastIndexOf('/') + 1, sourceUrl.length());
				filename = java.net.URLEncoder.encode(filename, "UTF-8");
				filename = filename.replace('+', ' ');
				sourceUrl = sourceUrl.substring(0, sourceUrl.lastIndexOf('/') + 1) + filename;
				Utils.log("download img = " + sourceUrl);
				resultImageResponse = Jsoup.connect(sourceUrl).userAgent("Chrome").timeout(10000).ignoreContentType(true).execute();
			}
		}
		
		//output here
		File imgFile = new File(targetDirectory);
		if (!imgFile.exists()) {
			imgFile.mkdir();
		}
		imgFile = new File(targetDirectory, Constant.COVER_FILE);
		FileOutputStream out = null;
		try {
			out = (new FileOutputStream(imgFile));
			out.write(resultImageResponse.bodyAsBytes());  // resultImageResponse.body() is where the image's contents are.
		} finally {
            try { if (out != null) out.close(); } catch (IOException e) {}
        }
		Utils.log("img saved: " + imgFile.getAbsolutePath());
	}
	
	/**
	 * @param originalImage
	 * @param scaledWidth
	 * @param scaledHeight
	 * @param preserveAlpha
	 * @return
	 */
	public static BufferedImage createResizedCopy(Image originalImage, int scaledWidth, int scaledHeight,
			boolean preserveAlpha) {
		
		Utils.log("resizing...");
		int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
		Graphics2D g = scaledBI.createGraphics();
		if (preserveAlpha) {
			g.setComposite(AlphaComposite.Src);
		}
		g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
		g.dispose();
		return scaledBI;
	}
	
//	/**
//	 * @param modVO
//	 */
//	public static String loadVersion(String url) {
//		try {
//			UserAgent userAgent = new UserAgent(); // create new userAgent (headless browser).
//			boolean useProxy = "true".equals(Utils.prop.get(Constant.USE_PROXY));
//			if (useProxy) {
//				userAgent.setProxyHost(String.valueOf(Utils.prop.get(Constant.PROXY_HOST)));
//				userAgent.setProxyPort(Integer.parseInt(String.valueOf(Utils.prop.get(Constant.PROXY_PORT))));
//			}
//			Utils.log("visit " + url + " ...");
//			userAgent.visit(url); // visit a url
//
//			Element div = userAgent.doc.findFirst("<span class=\"version\">");
//			String version = div.innerHTML();
//			if (version == null || version.trim().length() == 0) {
//				version = "1.0";
//			}
//			if (version.startsWith("v") || version.startsWith("V")) {
//				version = version.substring(1, version.length());
//			}
//			Utils.log("get version: " + version);
//			
//			return version;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	/**
//	 * @param modVO
//	 */
//	public static void loadCover(ModVO modVO) {
//		try {
//			
//			UserAgent userAgent = new UserAgent(); // create new userAgent (headless browser).
//			boolean useProxy = "true".equals(Utils.prop.get(Constant.USE_PROXY));
//			if (useProxy) {
//				userAgent.setProxyHost(String.valueOf(Utils.prop.get(Constant.PROXY_HOST)));
//				userAgent.setProxyPort(Integer.parseInt(String.valueOf(Utils.prop.get(Constant.PROXY_PORT))));
//			}
//			Utils.log("visit " + modVO.getHtml() + " ...");
//			userAgent.visit(modVO.getHtml()); // visit a url
//
//			Element div = userAgent.doc.findFirst("<div class=file-images>");
//			Element img = div.findFirst("<img>");
//			String url = img.getAtString("src");
//			
//			String path = Utils.prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.MODS_FOLDER + File.separator + 
//	    			modVO.getName() + File.separator + Constant.COVER_FOLDER;
//			
//			downloadImage(url, path);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * @param carVO
//	 */
//	public static void loadCover(CarVO carVO) {
//		try {
//			
//			UserAgent userAgent = new UserAgent(); // create new userAgent (headless browser).
//			boolean useProxy = "true".equals(Utils.prop.get(Constant.USE_PROXY));
//			if (useProxy) {
//				userAgent.setProxyHost(String.valueOf(Utils.prop.get(Constant.PROXY_HOST)));
//				userAgent.setProxyPort(Integer.parseInt(String.valueOf(Utils.prop.get(Constant.PROXY_PORT))));
//			}
//			Utils.log("visit " + carVO.getHtml() + " ...");
//			userAgent.visit(carVO.getHtml()); // visit a url
//
//			Element div = userAgent.doc.findFirst("<div class=file-images>");
//			Element img = div.findFirst("<img>");
//			String url = img.getAtString("src");
//			
//			String path = Utils.prop.getProperty(Constant.MOD_DIR) + File.separator + Constant.CARS_FOLDER + File.separator + 
//					carVO.getName() + File.separator + Constant.COVER_FOLDER;
//			
//			downloadImage(url, path);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * @param sourceUrl
//	 * @param targetDirectory
//	 * @throws Exception
//	 */
//	public static void downloadImage(String sourceUrl, String targetDirectory) throws Exception {
//		HandlerForBinary handlerForBinary = new HandlerForBinary();
//		UserAgent userAgent = new UserAgent(); // create new userAgent (headless browser).
//		boolean useProxy = "true".equals(Utils.prop.get(Constant.USE_PROXY));
//		if (useProxy) {
//			userAgent.setProxyHost(String.valueOf(Utils.prop.get(Constant.PROXY_HOST)));
//			userAgent.setProxyPort(Integer.parseInt(String.valueOf(Utils.prop.get(Constant.PROXY_PORT))));
//		}
//		if (sourceUrl.toLowerCase().endsWith("png")) {
//			userAgent.setHandler("image/png", handlerForBinary);
//		} else if (sourceUrl.toLowerCase().endsWith("gif")) {
//			userAgent.setHandler("image/gif", handlerForBinary);
//		} else {
//			userAgent.setHandler("image/jpeg", handlerForBinary);
//		}
//		Utils.log("get image from " + sourceUrl + " ...");
//		
//		try {
//			userAgent.visit(sourceUrl); // visit a url
//		} catch (Exception e) {
//			e.printStackTrace();
//			
//			try {
//				String filename = sourceUrl.substring(sourceUrl.lastIndexOf('/') + 1, sourceUrl.length());
//				filename = java.net.URLEncoder.encode(filename, "UTF-8");
//				sourceUrl = sourceUrl.substring(0, sourceUrl.lastIndexOf('/') + 1) + filename;
//				
//				Utils.log("get image from " + sourceUrl + " ...");
//				userAgent.visit(sourceUrl); // visit a url
//			} catch (Exception ex) {
//				e.printStackTrace();
//				
//				String filename = sourceUrl.substring(sourceUrl.lastIndexOf('/') + 1, sourceUrl.length());
//				filename = java.net.URLEncoder.encode(filename, "UTF-8");
//				filename = filename.replace('+', ' ');
//				sourceUrl = sourceUrl.substring(0, sourceUrl.lastIndexOf('/') + 1) + filename;
//				
//				Utils.log("get image from " + sourceUrl + " ...");
//				userAgent.visit(sourceUrl); // visit a url
//			}
//		}
//		
//		Utils.log("image size: " + handlerForBinary.getContent().length);
//		
//		File outputFile = new File(targetDirectory);
//		if (!outputFile.exists()) {
//			outputFile.mkdir();
//		}
//		outputFile = new File(targetDirectory, Constant.COVER_FILE);
//		
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(outputFile);
//            fos.write(handlerForBinary.getContent());
//        } finally {
//            try { if (fos != null) fos.close(); } catch (IOException e) {}
//        }
//        
////        String path = targetDirectory + File.separator + Constant.COVER_FILE;
////        BufferedImage img = createResizedCopy(new ImageIcon(path).getImage(), 130, 70, false);
////        Utils.log("save image to " + outputFile.getAbsolutePath());
////        ImageIO.write(img, "JPEG", outputFile);
//	}
//	
//	/**
//	 * @param originalImage
//	 * @param scaledWidth
//	 * @param scaledHeight
//	 * @param preserveAlpha
//	 * @return
//	 */
//	public static BufferedImage createResizedCopy(Image originalImage, int scaledWidth, int scaledHeight,
//			boolean preserveAlpha) {
//		
//		Utils.log("resizing...");
//		int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
//		BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
//		Graphics2D g = scaledBI.createGraphics();
//		if (preserveAlpha) {
//			g.setComposite(AlphaComposite.Src);
//		}
//		g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
//		g.dispose();
//		return scaledBI;
//	}
}

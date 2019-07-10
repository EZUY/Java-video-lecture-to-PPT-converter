import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;


import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;

/**
 * 
 */

/**
 * @author edwin
 *
 */
public class Main {

	public static void executeCmd(String cmd) throws IOException {
		try {
			Runtime rt = Runtime.getRuntime();
			System.out.println("Process: " + cmd);
			Process proc = rt.exec(cmd.split(" "));

			int exitVal = proc.waitFor();
			//System.out.println("Process finished ");
			// System.out.println("Process exitValue: " + exitVal);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void executeCmd(String video_path, String image_path, String image_name) throws IOException {
		

		String ffmpeg_path = "/usr/local/bin/ffmpeg";

		List<String> commands = new ArrayList<String>();
		commands.add(ffmpeg_path);
		commands.add("-ss");
		commands.add("1");// 这个参数是设置截取视频多少秒时的画面
		commands.add("-i");
		commands.add(video_path);
		commands.add("-vframes");
		commands.add("600");
		commands.add("-r");
		commands.add("1/2");
		commands.add(image_path + image_name + "-%3d.jpg");

		ProcessBuilder builder = new ProcessBuilder(commands);
		Process process = builder.start();
		InputStream errorStream = process.getErrorStream();
		InputStreamReader isr = new InputStreamReader(errorStream);
		BufferedReader br = new BufferedReader(isr);
		String line = "";
		while ((line = br.readLine()) != null) {
		}
		if (br != null) {
			br.close();
		}
		if (isr != null) {
			isr.close();
		}
		if (errorStream != null) {
			errorStream.close();
		}

	}

	private static boolean isSame(String a, String b) throws IOException {

		File imageFile1 = new File(a);
		File imageFile2 = new File(b);

		try {
			if (99.9 < ImgSimilarity.getSimilarity(imageFile1, imageFile2))
				return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	public static void checkDuplicateAndDelete(String image_path, String image_name) throws IOException {
		int photo_count = new File(image_path).listFiles().length-1;
		
		String p1 = image_path+image_name+String.format("-%3d.jpg", 1);
		String p2 = image_path+image_name+String.format("-%3d.jpg", 2);
		
		for (int i = 1; i < photo_count; i++) {
			
		//	System.out.println("comparing snap " +i+" and "+(i+1));
			p1 = image_path+image_name+String.format("-%03d.jpg", i);
			p2 = image_path+image_name+String.format("-%03d.jpg", i+1);
			
			if (isSame(p1, p2)) {
				//System.out.println("same");
				executeCmd("rm "+p1);
			} 
				//System.out.println("different");
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		//final String path = "/Users/edwin/Desktop/test/";
		//final String photoPath = "/Users/edwin/Desktop/test/snap-005.jpg";
		

		long startTime = System.currentTimeMillis(); //获取开始时间
//	
//		// TODO Auto-generated method stub
		String ffmpeg_path = "/usr/local/bin/ffmpeg";
		String video_path = "/Users/edwin/Desktop/test1.mp4";
		String image_path = "/Users/edwin/Desktop/test/";
		String image_name = "snap";
		String pptName = "test1";

		System.out.println("================================================================================");
		System.out.println("start screen shooting");
		executeCmd(ffmpeg_path + " -ss 1 -i " + video_path + " -vframes 600 -r 1/2 " + image_path + image_name
				+ "-%3d.jpg");
		
		//-vf \"crop=960:720:160:0\"
		// executeCmd(video_path, image_path);
		
		System.out.println("finish screen shoot");
		
		System.out.println("================================================================================");
		System.out.println();
		
		System.out.println("================================================================================");
		System.out.println("start checking duplicate");
		checkDuplicateAndDelete(image_path,image_name);
		System.out.println("finish checking duplicate");
		System.out.println("================================================================================");
		System.out.println();
//		
//		File folder = new File(image_path);
//		File[] listOfFiles = folder.listFiles();
//		Arrays.sort(listOfFiles);
//
//		for (File file : listOfFiles) {
//		    if (file.isFile()) {
//		    	
//		    	String photoName = file.getName();
//		    	if(photoName.substring(photoName.length() - 4).equals(".jpg")) {
//		    		executeCmd(ffmpeg_path+" -i "+image_path+photoName+" -vf crop=960:720:160:0 "+image_path+"new"+photoName);
//		    	}
//		    
//		      
//		    }
//		}
//		
		

		
//		

		System.out.println("================================================================================");

		System.out.println("start generating powerpoint");
//		 // creating a presentation
        XMLSlideShow ppt = new XMLSlideShow();
        
        

        // creating a slide in it
        
        
		File folder = new File(image_path);
		File[] listOfFiles = folder.listFiles();
		Arrays.sort(listOfFiles);

		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        //System.out.println(path+file.getName());
		    	
		        String photoPath = image_path+file.getName();
		        String newPhotoPath = image_path+"new"+file.getName();
		        
		        if(photoPath.substring(photoPath.length() - 4).equals(".jpg")) {
		        	
		        	executeCmd(ffmpeg_path+" -i "+photoPath+" -vf crop=960:720:160:0 "+newPhotoPath);
		        	executeCmd("rm "+ photoPath);
		        	
		        //executeCmd(ffmpeg_path+" -i "+photoPath+" -vf \"crop=960:720:160:0\" "+image_path+"new"+file.getName());
//		        
//		        ffmpeg -i in.mp4 -filter:v "crop=out_w:out_h:x:y" out.mp4
//		        	

		        XSLFSlide slide = ppt.createSlide();
			        
		        File image = new File(newPhotoPath);
		        

		        // converting it into a byte array
		        byte[] picture = IOUtils.toByteArray(new FileInputStream(image));

		        // adding the image to the presentation
		        XSLFPictureData idx = ppt.addPicture(picture, PictureData.PictureType.PNG);

		        // creating a slide with given picture on it
		        XSLFPictureShape pic = slide.createPicture(idx);
		        
		        executeCmd("rm "+ newPhotoPath);
		        
		        //System.out.println(photoPath+file.getName());
		        }
		        
		    }
		}

        // reading an image
        

        // creating a file object
        File file = new File(image_path+pptName+".pptx");
        FileOutputStream out = new FileOutputStream(file);

        // saving the changes to a file
        ppt.write(out);

       // System.out.println("image added successfully");
        out.close();
		

		long endTime = System.currentTimeMillis(); //获取结束时间
		
		System.out.println("finish generating powerpoint");
		System.out.println("================================================================================");
		
		System.out.println("================================================================================");
		System.out.println("All Finished");
		System.out.println("RunTime：" + (endTime - startTime)/1000 + "s"); //
		
	}

	//
	// String p1 = "/Users/edwin/Desktop/test/snap-001.jpg";
	// String p2 = "/Users/edwin/Desktop/test/snap-006.jpg";
	//
	// if (isSame(p1, p2)) {
	// System.out.println("same");
	// } else {
	// System.out.println("different");
	// }
	// System.out.println(new File(image_path).listFiles().length);
	//

}

class ImgSimilarity {
	// 全流程

	public static double getSimilarity(File imageFile1, File file2) throws IOException {
		int[] pixels1 = getImgFinger(imageFile1);
		int[] pixels2 = getImgFinger(file2);
		// 获取两个图的汉明距离（假设另一个图也已经按上面步骤得到灰度比较数组）
		int hammingDistance = getHammingDistance(pixels1, pixels2);
		// 通过汉明距离计算相似度，取值范围 [0.0, 1.0]
		double similarity = calSimilarity(hammingDistance) * 100;
		// System.out.println("相似度:" + similarity + "%");
		return similarity;
	}

	private static int[] getImgFinger(File imageFile) throws IOException {
		Image image = ImageIO.read(imageFile);
		// 转换至灰度
		image = toGrayscale(image);
		// 缩小成32x32的缩略图
		image = scale(image);
		// 获取灰度像素数组
		int[] pixels1 = getPixels(image);
		// 获取平均灰度颜色
		int averageColor = getAverageOfPixelArray(pixels1);
		// 获取灰度像素的比较数组（即图像指纹序列）
		pixels1 = getPixelDeviateWeightsArray(pixels1, averageColor);
		return pixels1;
	}

	// 将任意Image类型图像转换为BufferedImage类型，方便后续操作
	public static BufferedImage convertToBufferedFrom(Image srcImage) {
		BufferedImage bufferedImage = new BufferedImage(srcImage.getWidth(null), srcImage.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bufferedImage.createGraphics();
		g.drawImage(srcImage, null, null);
		g.dispose();
		return bufferedImage;
	}

	// 转换至灰度图
	public static BufferedImage toGrayscale(Image image) {
		BufferedImage sourceBuffered = convertToBufferedFrom(image);
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		BufferedImage grayBuffered = op.filter(sourceBuffered, null);
		return grayBuffered;
	}

	// 缩放至32x32像素缩略图
	public static Image scale(Image image) {
		image = image.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
		return image;
	}

	// 获取像素数组
	public static int[] getPixels(Image image) {
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		int[] pixels = convertToBufferedFrom(image).getRGB(0, 0, width, height, null, 0, width);
		return pixels;
	}

	// 获取灰度图的平均像素颜色值
	public static int getAverageOfPixelArray(int[] pixels) {
		Color color;
		long sumRed = 0;
		for (int i = 0; i < pixels.length; i++) {
			color = new Color(pixels[i], true);
			sumRed += color.getRed();
		}
		int averageRed = (int) (sumRed / pixels.length);
		return averageRed;
	}

	// 获取灰度图的像素比较数组（平均值的离差）
	public static int[] getPixelDeviateWeightsArray(int[] pixels, final int averageColor) {
		Color color;
		int[] dest = new int[pixels.length];
		for (int i = 0; i < pixels.length; i++) {
			color = new Color(pixels[i], true);
			dest[i] = color.getRed() - averageColor > 0 ? 1 : 0;
		}
		return dest;
	}

	// 获取两个缩略图的平均像素比较数组的汉明距离（距离越大差异越大）
	public static int getHammingDistance(int[] a, int[] b) {
		int sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += a[i] == b[i] ? 0 : 1;
		}
		return sum;
	}

	// 通过汉明距离计算相似度
	public static double calSimilarity(int hammingDistance) {
		int length = 32 * 32;
		double similarity = (length - hammingDistance) / (double) length;

		// 使用指数曲线调整相似度结果
		similarity = java.lang.Math.pow(similarity, 2);
		return similarity;
	}
}

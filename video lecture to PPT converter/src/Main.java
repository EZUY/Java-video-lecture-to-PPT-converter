import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 
 */

/**
 * @author edwin
 *
 */
public class Main {

	public static void executeCmd(String video_path, String image_path) throws IOException {

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
		commands.add(image_path + "snap-%3d.jpg");

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
			if (99 < ImgSimilarity.getSimilarity(imageFile1, imageFile2))
				return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		String video_path = "/Users/edwin/Desktop/test1.mp4";
		String image_path = "/Users/edwin/Desktop/test/";

		System.out.println("start screen shooting");
		// executeCmd(video_path, image_path);
		System.out.println("finish screen shoot");

		String p1 = "/Users/edwin/Desktop/test/snap-001.jpg";
		String p2 = "/Users/edwin/Desktop/test/snap-006.jpg";

		if (isSame(p1, p2)) {
			System.out.println("same");
		} else {
			System.out.println("different");
		}

	}

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
		System.out.println("相似度:" + similarity + "%");
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

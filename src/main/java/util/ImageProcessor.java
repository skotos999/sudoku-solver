package util;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Integer.parseInt;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static java.util.Comparator.comparingDouble;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static javax.imageio.ImageIO.read;
import static nu.pattern.OpenCV.loadShared;
import static org.opencv.core.Core.NORM_MINMAX;
import static org.opencv.core.Core.bitwise_and;
import static org.opencv.core.Core.bitwise_not;
import static org.opencv.core.Core.normalize;
import static org.opencv.core.CvType.CV_32FC2;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.Mat.zeros;
import static org.opencv.imgcodecs.Imgcodecs.IMREAD_UNCHANGED;
import static org.opencv.imgcodecs.Imgcodecs.imencode;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_GRAY2BGR;
import static org.opencv.imgproc.Imgproc.Canny;
import static org.opencv.imgproc.Imgproc.FONT_HERSHEY_SIMPLEX;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.HoughLines;
import static org.opencv.imgproc.Imgproc.INTER_AREA;
import static org.opencv.imgproc.Imgproc.INTER_CUBIC;
import static org.opencv.imgproc.Imgproc.LINE_8;
import static org.opencv.imgproc.Imgproc.MORPH_CLOSE;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;
import static org.opencv.imgproc.Imgproc.RETR_LIST;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY_INV;
import static org.opencv.imgproc.Imgproc.adaptiveThreshold;
import static org.opencv.imgproc.Imgproc.approxPolyDP;
import static org.opencv.imgproc.Imgproc.arcLength;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.getPerspectiveTransform;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.line;
import static org.opencv.imgproc.Imgproc.morphologyEx;
import static org.opencv.imgproc.Imgproc.putText;
import static org.opencv.imgproc.Imgproc.rectangle;
import static org.opencv.imgproc.Imgproc.resize;
import static org.opencv.imgproc.Imgproc.warpPerspective;
import static org.opencv.photo.Photo.fastNlMeansDenoising;

public final class ImageProcessor {

	private static final int SCALING_SIZE = 810;
	private static final Size SIZE = new Size(SCALING_SIZE, SCALING_SIZE);
	private static final int CELL_SIZE = SCALING_SIZE / 9;
	private static final double BR = CELL_SIZE * 0.85;
	private static final double TL = CELL_SIZE * 0.15;

	static {
		loadShared();
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static Integer[] doOcrOnCells(final Mat input) {
		final var threshold = applyAdaptiveThreshold(getGaussianBlur(input, 13), 57, 19);

		final var tesseract = initOcr();
		final var digits = new ArrayList<Integer>();
		for (var i = 0; i < SCALING_SIZE; i += CELL_SIZE) {
			for (var j = 0; j < SCALING_SIZE; j += CELL_SIZE) {
				final var subMat = threshold.submat(new Rect(j, i, CELL_SIZE, CELL_SIZE));

				final var mask = zeros(subMat.size(), CV_8UC1);
				rectangle(mask, new Point(TL, TL), new Point(BR, BR), Scalar.all(255), -1);

				final var imagePart = zeros(subMat.size(), subMat.type());
				subMat.copyTo(imagePart, mask);

				bitwise_not(imagePart, imagePart);
				final var mob = new MatOfByte();
				imencode(".png", imagePart, mob);

				digits.add(imgToInteger(tesseract, mob.toArray()));
			}
		}
		return digits.toArray(Integer[]::new);
	}

	private static Mat applyAdaptiveThreshold(final Mat src, final int blockSize, final int C) {
//		final var dst = new Mat();
		adaptiveThreshold(src, src, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY_INV, blockSize, C);
		return src;
	}

	private static Mat getGaussianBlur(final Mat src, final int size) {
		final var dst = new Mat();
		GaussianBlur(src, dst, new Size(size, size), 0);
		return dst;
	}

	private static Tesseract initOcr() {
		final var tesseract = new Tesseract();
		tesseract.setDatapath("src/main/resources/tessdata");
		tesseract.setVariable("tessedit_char_whitelist", "123456789");
		return tesseract;
	}

	private static Integer imgToInteger(final Tesseract tesseract, final byte[] image) {
		try {
			final var possibleNumber = tesseract.doOCR(read(new ByteArrayInputStream(image))).trim();
			return possibleNumber.isBlank() ? 0 : parseInt(possibleNumber.substring(0, 1));
		} catch (final TesseractException e) {
			throw new IllegalStateException("OCR failed.");
		} catch (final IOException e) {
			throw new IllegalStateException("Could not read byte array");
		}
	}

	public static Mat preprocess(final String filename) {
		final var unchanged = resizeImage(loadImageUnchanged(filename));
		fastNlMeansDenoising(unchanged, unchanged);
		debug(unchanged, filename, "01_denoised");

		final var grayscale = colorToGrayscale(unchanged);
		debug(grayscale, filename, "02_grayscale");
		final var perspectiveTransform = calculatePerspectiveTransform(grayscale);
		debug(perspectiveTransform, filename, "03_perspectiveTransform");
		final var gaussianBlur = getGaussianBlur(perspectiveTransform, 5);
		debug(gaussianBlur, filename, "04_gaussianBlur");
		normalize(gaussianBlur, gaussianBlur, 0, 255, NORM_MINMAX, CV_8UC1);
		debug(gaussianBlur, filename, "05_gaussianBlur_normalize");

		final var edges = new Mat();
		Canny(gaussianBlur, edges, 100, 300, 5, true);
		debug(edges, filename, "06_edges_100_300_5");

		final var kernel = getStructuringElement(MORPH_RECT, new Size(7, 7));
		final var vertical = houghVertical(edges);
		debug(vertical, filename, "07_hough_vertical");
		morphologyEx(vertical, vertical, MORPH_CLOSE, kernel, new Point(-1, -1), 3);
		debug(vertical, filename, "08_hough_vertical_closed");

		final var horizontal = houghHorizontal(edges);
		debug(horizontal, filename, "09_hough_horizontal");
		morphologyEx(horizontal, horizontal, MORPH_CLOSE, kernel, new Point(-1, -1), 3);
		debug(horizontal, filename, "10_hough_horizontal_closed");

		final var gridPoints = new Mat();
		bitwise_and(horizontal, vertical, gridPoints);
		debug(gridPoints, filename, "11_gridPoints");

		final var corrected = correctDefects(gridPoints, gaussianBlur);
		debug(corrected, filename, "12_corrected");

		return corrected;
	}

	private static Mat resizeImage(final Mat src) {
		final var dst = new Mat();
		final var compare = Double.compare(src.size().area(), SIZE.area());
		// For upscaling
		if (compare < 0) {
			resize(src, dst, SIZE, 0, 0, INTER_CUBIC);
			return dst;
		}
		// For downscaling
		if (compare > 0) {
			resize(src, dst, SIZE, 0, 0, INTER_AREA);
			return dst;
		}
		return src;
	}

	private static Mat loadImageUnchanged(final String filename) {
		try {
			final var resource = ImageProcessor.class.getClassLoader().getResource(filename);
			var absolutePath = "";
			if (Objects.nonNull(resource)) {
				absolutePath = new File(resource.toURI()).getAbsolutePath();
			}
			return imread(absolutePath, IMREAD_UNCHANGED);
		} catch (final URISyntaxException e) {
			throw new IllegalStateException("URL cannot be converted to URI");
		}
	}

	private static void debug(final Mat mat, final String dir, final String filename) {
		final var path = Path.of("preprocess", dir, filename + ".png");
		path.toFile().getParentFile().mkdirs();
		imwrite(path.toString(), mat);
	}

	private static Mat colorToGrayscale(final Mat src) {
		if (src.channels() != 1) {
			cvtColor(src, src, COLOR_BGR2GRAY);
		}
		return src;
	}

	private static Mat calculatePerspectiveTransform(final Mat src) {
		final var counter = new AtomicInteger();
		final var pointsArray = findLargestRectangle(src) //
				.toList() //
				.stream() //
				.sorted(comparingDouble(p -> p.y)) //
				.collect(groupingBy(it -> counter.getAndIncrement() / 2, //
						collectingAndThen(toList(), //
								points -> points.stream() //
										.sorted(comparingDouble(p -> p.x))))) //
				.values() //
				.stream()
				.flatMap(identity())
				.toArray(Point[]::new);

		final var vertices = new MatOfPoint2f(pointsArray);
		final var dst = new MatOfPoint2f(new Point(0, 0), new Point(SCALING_SIZE, 0), new Point(0, SCALING_SIZE), new Point(SCALING_SIZE, SCALING_SIZE));
		warpPerspective(src, src, getPerspectiveTransform(vertices, dst), SIZE);
		return src;
	}

	private static Mat houghVertical(final Mat edges) {
		final var lines = new Mat();
		HoughLines(edges, lines, 1, toRadians(1), 200, 0, 0, toRadians(175));
		HoughLines(edges, lines, 1, toRadians(1), 200, 0, 0, 0, toRadians(5));
		return drawLines(lines);
	}

	private static Mat houghHorizontal(final Mat edges) {
		final var lines = new Mat();
		HoughLines(edges, lines, 1, toRadians(1), 150, 0, 0, toRadians(89), toRadians(92));
		return drawLines(lines);
	}

	private static Mat correctDefects(final Mat gridPoints, final Mat normalized) {
		final var contours = new ArrayList<MatOfPoint>();
		findContours(gridPoints, contours, new Mat(), RETR_LIST, CHAIN_APPROX_SIMPLE);

		final var centroids = contours.stream() //
				.map(Imgproc::moments) //
				.map(moments -> new Point(moments.m10 / (moments.m00 + 1e-5), moments.m01 / (moments.m00 + 1e-5))) //
				.sorted(comparingDouble(o1 -> o1.y)) //
				.collect(toList());

		if (centroids.size() != 100) {
			throw new IllegalStateException("Could not find all grid points");
		}

		final var counter = new AtomicInteger();
		final var sortedCentroids = centroids.stream() //
				.collect(groupingBy(it -> counter.getAndIncrement() / 10)) //
				.values() //
				.stream() //
				.map(points -> points.stream() //
						.sorted(comparingDouble(o -> o.x)) //
						.collect(toList())) //
				.collect(toList());

		final var output = zeros(SIZE, CV_8UC1);
		for (var i = 0; i < 100; i++) {
			final var ri = i / 10;
			final var ci = i % 10;
			if (ri != 9 && ci != 9) {
				final var rowStart = ri * CELL_SIZE;
				final var rowEnd = (ri + 1) * CELL_SIZE;
				final var colStart = ci * CELL_SIZE;
				final var colEnd = (ci + 1) * CELL_SIZE;
				final var warp = new Mat();
				final var src = new MatOfPoint2f(sortedCentroids.get(ri).get(ci), sortedCentroids.get(ri).get(ci + 1), sortedCentroids.get(ri + 1).get(ci), sortedCentroids.get(ri + 1).get(ci + 1));
				final var dst = new MatOfPoint2f(new Point(colStart, rowStart), new Point(colEnd, rowStart), new Point(colStart, rowEnd), new Point(colEnd, rowEnd));
				warpPerspective(normalized, warp, getPerspectiveTransform(src, dst), SIZE);
				final var src_sub = warp.submat(rowStart, rowEnd, colStart, colEnd);
				final var dst_sub = output.submat(rowStart, rowEnd, colStart, colEnd);
				src_sub.copyTo(dst_sub);
			}
		}
		return output;
	}

	private static MatOfPoint2f findLargestRectangle(final Mat src) {
		final var threshold = applyAdaptiveThreshold(getGaussianBlur(src, 9), 11, 2);
		final var contours = new ArrayList<MatOfPoint>();
		findContours(threshold, contours, new Mat(), RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

		final var hasMaxArea = contours.stream().max(comparingDouble(Imgproc::contourArea)).orElse(new MatOfPoint());

		final var largest = new MatOfPoint2f();
		final var approxCurve = new MatOfPoint2f();
		final var curve = new MatOfPoint2f();
		hasMaxArea.convertTo(curve, CV_32FC2);
		var i = 1;
		while (approxCurve.toArray().length != 4) {
			approxPolyDP(curve, approxCurve, arcLength(curve, true) * i / 100, true);
			approxCurve.copyTo(largest);
			i++;
		}
		return largest;
	}

	private static Mat drawLines(final Mat lines) {
		final var dst = zeros(SIZE, CV_8UC1);
		for (var x = 0; x < lines.rows(); x++) {
			final var rho = lines.get(x, 0)[0];
			final var theta = lines.get(x, 0)[1];
			final var x0 = cos(theta) * rho;
			final var y0 = sin(theta) * rho;
			final var pt1 = new Point(x0 + 2 * SCALING_SIZE * -sin(theta), y0 + 2 * SCALING_SIZE * cos(theta));
			final var pt2 = new Point(x0 - 2 * SCALING_SIZE * -sin(theta), y0 - 2 * SCALING_SIZE * cos(theta));
			line(dst, pt1, pt2, new Scalar(255, 255, 255), 4, LINE_8, 0);
		}
		return dst;
	}

	public static void drawSolution(final Mat src, final Integer[] solution) {
		cvtColor(src, src, COLOR_GRAY2BGR);
		final var color = new Scalar(23, 137, 5);
		final var size = src.size();
		final var cellWidth = (int) size.width / 9;
		final var cellHeight = (int) size.height / 9;
		for (var i = 0; i < solution.length; i++) {
			if (solution[i] != 0) {
				putText(src, solution[i].toString(), new Point(cellWidth * (i % 9 + 0.25), cellHeight * (i / 9 + 0.75)), FONT_HERSHEY_SIMPLEX, 2, color, 2);
			}
		}
		debug(src, "output", "solution");
	}

}

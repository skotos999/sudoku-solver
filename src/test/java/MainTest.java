import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static util.ImageProcessor.doOcrOnCells;
import static util.ImageProcessor.preprocess;

class MainTest {

	@Test
	void ocr1() {
		final var mat = preprocess("1.png");
		final var puzzle = doOcrOnCells(mat);
		assertArrayEquals(new Integer[]{
				0, 0, 0, 6, 0, 4, 7, 0, 0, 7, 0, 6, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0, 5, 0, 8, 0, 0, 7, 0, 0, 2, 0, 0, 9, 3, 8, 0, 0, 0, 0,
				0, 0, 0, 5, 4, 3, 0, 0, 1, 0, 0, 7, 0, 0, 5, 0, 2, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 2, 0, 8, 0, 0, 2, 3, 0, 1, 0, 0, 0
		}, puzzle);
		final var solution = Solver.search(Solver.init(puzzle), new ArrayList<>());
		assertArrayEquals(new Integer[]{
				5, 8, 3, 6, 9, 4, 7, 2, 1, 7, 1, 6, 8, 3, 2, 5, 4, 9, 2, 9, 4, 1, 7, 5, 3, 8, 6, 6, 7, 1, 5, 2, 8, 4, 9, 3, 8, 2, 9, 7, 4,
				3, 1, 6, 5, 4, 3, 5, 9, 1, 6, 8, 7, 2, 1, 5, 8, 2, 6, 7, 9, 3, 4, 3, 6, 7, 4, 5, 9, 2, 1, 8, 9, 4, 2, 3, 8, 1, 6, 5, 7
		}, solution);
	}

	@Test
	void ocr2() {
		final var mat = preprocess("2.jpg");
		final var puzzle = doOcrOnCells(mat);
		assertArrayEquals(new Integer[]{
				9, 3, 6, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 9, 3, 7, 4, 0, 0, 4, 0, 8, 2, 1, 0, 0, 9, 4, 7, 2, 0, 0, 0, 0, 0, 6, 0, 0, 0, 7, 5,
				9, 0, 0, 0, 1, 0, 0, 0, 0, 0, 3, 7, 8, 5, 0, 0, 4, 1, 6, 0, 2, 0, 0, 2, 1, 3, 7, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 1, 5, 7
		}, puzzle);
		final var solution = Solver.search(Solver.init(puzzle), new ArrayList<>());
		assertArrayEquals(new Integer[]{
				9, 3, 6, 5, 4, 7, 2, 8, 1, 2, 1, 8, 6, 9, 3, 7, 4, 5, 7, 4, 5, 8, 2, 1, 6, 3, 9, 4, 7, 2, 1, 3, 8, 5, 9, 6, 6, 8, 3, 7, 5,
				9, 4, 1, 2, 1, 5, 9, 2, 6, 4, 3, 7, 8, 5, 9, 7, 4, 1, 6, 8, 2, 3, 8, 2, 1, 3, 7, 5, 9, 6, 4, 3, 6, 4, 9, 8, 2, 1, 5, 7
		}, solution);
	}

	@Test
	void ocr3() {
		final var mat = preprocess("3.jpeg");
		final var puzzle = doOcrOnCells(mat);
		assertArrayEquals(new Integer[]{
				5, 0, 7, 0, 9, 3, 4, 0, 0, 0, 1, 0, 0, 2, 7, 0, 6, 0, 0, 6, 0, 0, 0, 4, 7, 2, 0, 7, 0, 2, 3, 0, 0, 0, 0, 4, 0, 0, 3, 8, 4,
				0, 0, 7, 0, 1, 8, 4, 0, 0, 0, 3, 0, 0, 9, 0, 0, 4, 1, 0, 0, 5, 7, 2, 0, 0, 9, 0, 0, 1, 0, 8, 0, 4, 0, 0, 0, 2, 6, 0, 9
		}, puzzle);
		final var solution = Solver.search(Solver.init(puzzle), new ArrayList<>());
		assertArrayEquals(new Integer[]{
				5, 2, 7, 6, 9, 3, 4, 8, 1, 4, 1, 8, 5, 2, 7, 9, 6, 3, 3, 6, 9, 1, 8, 4, 7, 2, 5, 7, 5, 2, 3, 6, 9, 8, 1, 4, 6, 9, 3, 8, 4,
				1, 5, 7, 2, 1, 8, 4, 2, 7, 5, 3, 9, 6, 9, 3, 6, 4, 1, 8, 2, 5, 7, 2, 7, 5, 9, 3, 6, 1, 4, 8, 8, 4, 1, 7, 5, 2, 6, 3, 9
		}, solution);
	}

	@Test
	void ocr4() {
		final var mat = preprocess("4.jpg");
		final var puzzle = doOcrOnCells(mat);
		assertArrayEquals(new Integer[]{
				8, 0, 0, 0, 1, 0, 0, 0, 9, 0, 5, 0, 8, 0, 7, 0, 1, 0, 0, 0, 4, 0, 9, 0, 7, 0, 0, 0, 6, 0, 7, 0, 1, 0, 2, 0, 5, 0, 8, 0, 6,
				0, 1, 0, 7, 0, 1, 0, 5, 0, 2, 0, 9, 0, 0, 0, 7, 0, 4, 0, 6, 0, 0, 0, 8, 0, 3, 0, 9, 0, 4, 0, 3, 0, 0, 0, 5, 0, 0, 0, 8
		}, puzzle);
		final var solution = Solver.search(Solver.init(puzzle), new ArrayList<>());
		assertArrayEquals(new Integer[]{
				8, 7, 2, 4, 1, 3, 5, 6, 9, 9, 5, 6, 8, 2, 7, 3, 1, 4, 1, 3, 4, 6, 9, 5, 7, 8, 2, 4, 6, 9, 7, 3, 1, 8, 2, 5, 5, 2, 8, 9, 6,
				4, 1, 3, 7, 7, 1, 3, 5, 8, 2, 4, 9, 6, 2, 9, 7, 1, 4, 8, 6, 5, 3, 6, 8, 5, 3, 7, 9, 2, 4, 1, 3, 4, 1, 2, 5, 6, 9, 7, 8
		}, solution);
	}

	@Disabled
	@Test
	void ocr5() {
		final var mat = preprocess("5.jpg");
		final var puzzle = doOcrOnCells(mat);
		assertArrayEquals(new Integer[]{
				0, 5, 0, 0, 6, 0, 0, 8, 0, 7, 0, 0, 3, 5, 4, 0, 0, 2, 0, 2, 0, 7, 0, 8, 0, 3, 0, 2, 0, 0, 0, 0, 0, 0, 0, 3, 8, 0, 0, 4, 0,
				6, 0, 0, 5, 0, 6, 0, 0, 0, 0, 0, 4, 0, 0, 0, 2, 0, 0, 0, 9, 0, 0, 0, 0, 0, 6, 9, 7, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 8
		}, puzzle);
		final var solution = Solver.search(Solver.init(puzzle), new ArrayList<>());
		assertArrayEquals(new Integer[]{
				1, 5, 3, 9, 6, 2, 4, 8, 7, 7, 8, 6, 3, 5, 4, 1, 9, 2, 9, 2, 4, 7, 1, 8, 5, 3, 6, 2, 4, 5, 8, 7, 9, 6, 1, 3, 8, 9, 1, 4, 3,
				6, 7, 2, 5, 3, 6, 7, 1, 2, 5, 8, 4, 9, 6, 1, 2, 5, 8, 3, 9, 7, 4, 4, 3, 8, 6, 9, 7, 2, 5, 1, 5, 7, 9, 2, 4, 1, 3, 6, 8
		}, solution);
	}

	@Test()
	void ocr6() {
		Assertions.assertThrows(IllegalStateException.class, () -> preprocess("6.jpg"));
/*		final Mat mat = preprocess("6.jpg");
		final Integer[] puzzle = doOcrOnCells(mat);
		final var integers = Arrays.toString(puzzle);
		Assertions.assertEquals(
				"[0, 0, 8, 0, 2, 0, 9, 0, 0," +
						" 0, 0, 0, 8, 0, 7, 0, 4, 1," +
						" 5, 0, 4, 0, 0, 6, 0, 0, 7," +
						" 0, 5, 0, 0, 0, 0, 0, 7, 2," +
						" 6, 0, 0, 0, 4, 0, 0, 0, 8," +
						" 0, 9, 7, 0, 0, 0, 0, 0, 0," +
						" 7, 0, 0, 0, 0, 0, 6, 0, 0," +
						" 0, 8, 0, 9, 0, 0, 0, 2, 0," +
						" 0, 4, 2, 6, 7, 0, 0, 0, 0]", integers);*/
	}

	@Test
	void ocr7() {
		final var mat = preprocess("7.jpg");
		final var puzzle = doOcrOnCells(mat);
		assertArrayEquals(new Integer[]{
				0, 0, 0, 8, 0, 0, 0, 6, 2, 0, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0, 9, 0, 2, 4, 0, 0, 4, 0, 5, 0, 0, 8, 0, 3, 0, 0, 0, 0, 0, 0,
				4, 8, 0, 0, 0, 0, 7, 2, 3, 1, 0, 0, 0, 0, 7, 1, 0, 6, 0, 0, 0, 0, 3, 0, 0, 7, 0, 0, 0, 0, 5, 6, 0, 0, 0, 0, 0, 0, 2, 0
		}, puzzle);
		final var solution = Solver.search(Solver.init(puzzle), new ArrayList<>());
		assertArrayEquals(new Integer[]{
				9, 3, 4, 8, 5, 7, 1, 6, 2, 5, 1, 2, 3, 4, 6, 9, 8, 7, 7, 8, 6, 9, 1, 2, 4, 5, 3, 4, 9, 5, 6, 7, 8, 2, 3, 1, 1, 2, 3, 5, 9,
				4, 8, 7, 6, 8, 6, 7, 2, 3, 1, 5, 4, 9, 2, 7, 1, 4, 6, 5, 3, 9, 8, 3, 4, 8, 7, 2, 9, 6, 1, 5, 6, 5, 9, 1, 8, 3, 7, 2, 4
		}, solution);
	}

	@Test
	void ocr8() {
		final var mat = preprocess("8.jpg");
		final var puzzle = doOcrOnCells(mat);
		assertArrayEquals(new Integer[]{
				7, 9, 4, 8, 0, 5, 3, 1, 2, 2, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 3, 2, 0, 9, 4, 0, 0, 0, 7, 9, 0, 0, 0, 5, 4, 0, 0, 0, 0, 0, 7,
				0, 0, 0, 0, 0, 2, 1, 0, 0, 0, 7, 6, 0, 0, 0, 2, 4, 0, 8, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 8, 3, 5, 8, 1, 0, 7, 6, 2, 4
		}, puzzle);
		final var solution = Solver.search(Solver.init(puzzle), new ArrayList<>());
		assertArrayEquals(new Integer[]{
				7, 9, 4, 8, 6, 5, 3, 1, 2, 2, 1, 6, 7, 4, 3, 8, 9, 5, 5, 8, 3, 2, 1, 9, 4, 7, 6, 6, 7, 9, 3, 8, 2, 5, 4, 1, 4, 3, 5, 6, 7,
				1, 2, 8, 9, 8, 2, 1, 9, 5, 4, 7, 6, 3, 9, 6, 2, 4, 3, 8, 1, 5, 7, 1, 4, 7, 5, 2, 6, 9, 3, 8, 3, 5, 8, 1, 9, 7, 6, 2, 4
		}, solution);
	}

	@Test
	void ocr9() {
		Assertions.assertThrows(IllegalStateException.class, () -> preprocess("9.jpg"));
/*		final Mat mat = preprocess("9.jpg");
		final Integer[] puzzle = doOcrOnCells(mat);
		final var integers = Arrays.toString(puzzle);
		Assertions.assertEquals(
				"[0, 7, 2, 6, 0, 5, 1, 3, 0," +
						" 0, 0, 5, 0, 0, 0, 6, 0, 0," +
						" 0, 0, 0, 0, 3, 0, 0, 0, 0," +
						" 0, 8, 9, 5, 0, 3, 4, 7, 0," +
						" 0, 0, 7, 9, 0, 6, 3, 0, 0," +
						" 0, 6, 3, 8, 0, 4, 5, 2, 0," +
						" 0, 0, 0, 0, 5, 0, 0, 0, 0," +
						" 0, 0, 1, 0, 0, 0, 2, 0, 0," +
						" 0, 5, 8, 3, 0, 2, 7, 1, 0]", integers);*/
	}

}

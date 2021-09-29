import util.ImageProcessor;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.IntStream;

import static util.ImageProcessor.doOcrOnCells;
import static util.ImageProcessor.preprocess;

final class Main {

	public static void main(final String[] args) {
		final var mat = preprocess("7.jpg");
		final var sudokuPuzzle = doOcrOnCells(mat);
		final var solution = Solver.search(Solver.init(sudokuPuzzle), new ArrayList<>());
		IntStream.range(0, sudokuPuzzle.length).filter(i -> Objects.equals(sudokuPuzzle[i], solution[i])).forEach(i -> solution[i] = 0);
		ImageProcessor.drawSolution(mat, solution);
	}

}

package ru.javaops.masterjava.matrix;

import java.util.Random;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        class ColumnMultipleResult {
            final int col;
            final int[] columnC;

            public ColumnMultipleResult(int col, int[] columnC) {
                this.col = col;
                this.columnC = columnC;
            }
        }

        CompletionService<ColumnMultipleResult> completionService = new ExecutorCompletionService<>(executor);

        for (int i = 0; i < matrixSize; i++) {
            final int col = i;
            final int columnB[] = new int[matrixSize];

            for (int j = 0; j < matrixSize; j++) {
                columnB[j] = matrixB[j][col];
            }

            completionService.submit(() -> {
                final int columnC[] = new int[matrixSize];

                for (int row = 0; row < matrixSize; row++) {
                    final int[] rowA = matrixA[row];
                    int sum = 0;
                    for (int j = 0; j < matrixSize; j++) {
                        sum += rowA[j] * columnB[j];
                    }
                }
                return new ColumnMultipleResult(col, columnC);
            });
        }

        for (int j = 0; j < matrixSize; j++) {
            ColumnMultipleResult result = completionService.take().get();
            for (int k = 0; k < matrixSize; k++) {
                matrixC[k][result.col] = result.columnC[k];
            }
        }

        return matrixC;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final int aColumns = matrixA.length;
        final int aRows = matrixB[0].length;
        final int bColumns = matrixB.length;
        final int bRows = matrixB[0].length;

        int thatColumn[] = new int[bRows];

        try {
            for (int i = 0; ; i++) {
                for (int j = 0; j < aColumns; j++) {
                    thatColumn[j] = matrixB[j][i];

                    for (int k = 0; k < aRows; k++) {
                        int thisRow[] = matrixA[i];
                        int sum = 0;
                        for (j = 0; j < aColumns; j++) {
                            sum += thisRow[j] * thatColumn[j];
                        }
                        matrixC[k][i] = sum;
                    }
                }
            }

        } catch (IndexOutOfBoundsException ignore) {
            return matrixC;
        }
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}

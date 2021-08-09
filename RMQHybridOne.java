package pxc190029;

import java.util.Arrays;


/**
 * Top level uses sparse table
 * No any preprocess on block level.
 */
public class RMQHybridOne implements RMQStructure {

    protected int[] topArr; // top level array which stores min of each block
    protected int[][] sparseTable;
    protected int blockSize;

    public RMQHybridOne() {
    }

    protected double getBlockSize(double value) {
        return Math.log(value) / Math.log(2);
    }

    // Used to construct sparse table
    protected double log2(double value) {
        return Math.log(value) / Math.log(2);
    }

    @Override
    public void preProcess(int[] arr) {
        int n = arr.length;
        blockSize = (int) getBlockSize(n);
        int topArrSize = n % blockSize == 0 ? n / blockSize : n / blockSize + 1;
        topArr = new int[topArrSize];

        // Scan arr to get topArr
        for (int i = 0, k = 0; i < n; i += blockSize, k++) {
            int min = arr[i];
            for (int j = i; j < i + blockSize && j < n; j++) {
                min = Math.min(min, arr[j]);
            }
            topArr[k] = min;
        }

        // Construct sparse table for topArr
        int rows = topArrSize;
        int cols = (int)log2(topArrSize) + 1;
        sparseTable = new int[rows][cols];

        for(int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++){
                if (j == 0) sparseTable[i][j] = topArr[i];
                else {
                    int next = (int) (i + Math.pow(2, j - 1));
                    if (next < rows && sparseTable[next][j - 1] != 0) {
                        sparseTable[i][j] = Math.min(sparseTable[i][j - 1], sparseTable[next][j - 1]);

                    }
                }
            }
        }

    }

    @Override
    public int query(int[] arr, int i, int j)  {

        int res;

        int iTopIdx = i / blockSize;
        int jTopIdx = j / blockSize;

        // Get block level min left & right
        int minLeftBlock = getMinLeftBlockLevel(arr, i, iTopIdx);
        int minRightBlock = getMinRightBlockLevel(arr, j, jTopIdx);
        res = Math.min(minLeftBlock, minRightBlock);

        // Get top level min
        if (iTopIdx + 1 <= jTopIdx - 1) {
            int minTopLevel = getTopLevelMin(iTopIdx + 1, jTopIdx - 1);
            res = Math.min(res, minTopLevel);
        }
        return res;
    }

    // Get the top level minimum of blocks between i and j (exclusive)
    protected int getTopLevelMin(int i, int j) {
        int k = (int) log2(j - i + 1);
        int leftRangeMin =  sparseTable[i][k];
        int rightRangeMin =  sparseTable[(int) (j - Math.pow(2,k) + 1)][k];
        return Math.min(leftRangeMin, rightRangeMin);
    }

    // Get the block level minimum of i block
    protected int getMinLeftBlockLevel(int[] arr, int idx, int topIdx) {
        int endIdx = (topIdx + 1) * blockSize - 1;
        int res = arr[idx];
        for (; idx <= endIdx; idx++) {
            res = Math.min(res, arr[idx]);
        }
        return res;
    }

    // Get the block level minimum of j block
    protected int getMinRightBlockLevel(int[] arr, int idx, int topIdx) {
        int startIdx = topIdx * blockSize;
        int res = arr[startIdx];
        for (; startIdx <= idx; startIdx++) {
            res = Math.min(res, arr[startIdx]);
        }
        return res;
    }



    public static void main(String[] args) throws Exception {

    }
}

package pxc190029;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

public class RMQFischerHeun extends RMQHybridOne implements RMQStructure {

    HashMap<String, int[][]> cNumToFullTable; // Mapping from Cartesian number (represented by string) to fully processed table
    HashMap<Integer, int[][]> blockIdxToFullTable; // Mapping from block id to fully processed table

    // block size = (1/2) * log4N
    protected double getBlockSize(double value) {
        return Math.log(value) / (Math.log(4) * 2);
    }

    public RMQFischerHeun() {
        cNumToFullTable = new HashMap<>();
        blockIdxToFullTable = new HashMap<>();
    }

    @Override
    public void preProcess(int[] arr) {
        super.preProcess(arr);

        // Process block level
        for (int i = 0, k = 0; i < arr.length; i += blockSize , k++) {
            String cNum = getCNum(arr,k);
            if (cNumExist(cNum, cNumToFullTable)) {
                blockIdxToFullTable.put(k, cNumToFullTable.get(cNum));
            } else {
                int[][] fullTable = fullyProcess(arr, k);
                cNumToFullTable.put(cNum, fullTable);
                blockIdxToFullTable.put(k, fullTable);
            }
        }

    }

    /**
     * Get fully processed block level table that stores index of min value of each range
     * offers O(1) look up.
     * @param arr input array
     * @param blockIdx index of block
     * @return a fully processed table
     */
    private int[][] fullyProcess(int[] arr, int blockIdx) {
        int startIdx = blockIdx * blockSize;

        // Last block might shrink
        int curBlockSize = blockSize;
        if (blockIdx == topArr.length - 1) {
            curBlockSize = arr.length - startIdx;
        }
        int[][] res = new int[curBlockSize][curBlockSize];
        for (int i = 0; i < curBlockSize && startIdx + i < arr.length; i++) {
            for (int j = i; j < curBlockSize && startIdx + j < arr.length; j++) {
                if (i == j) res[i][j] = i;
                else {
                    if (arr[res[i][j - 1] + startIdx] < arr[j + startIdx]) {
                        res[i][j] = res[i][j - 1];
                    } else res[i][j] = j;
                }
            }
        }
        return res;
    }

    private boolean cNumExist(String cNum, HashMap<String, int[][]> cNumToFullTable) {
        return cNumToFullTable.containsKey(cNum);
    }

    // Get Cartesian Number represented as string
    private String getCNum(int[] arr, int blockIdx) {
        int startIdx = blockIdx * blockSize;

        StringBuilder sb = new StringBuilder();
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = startIdx; i < arr.length && i < startIdx + blockSize; i++) { // Last block might not have full block size
            while (!stack.isEmpty() && stack.peek() > arr[i]) {
                stack.pop();
                sb.append('0');
            }
            stack.push(arr[i]);
            sb.append('1');
        }
        while (!stack.isEmpty()) {
            stack.pop();
            sb.append('0');
        }

        return sb.toString();
    }

    @Override
    public int query(int[] arr, int i, int j) {
        return super.query(arr, i, j);
    }

    @Override
    protected int getMinLeftBlockLevel(int[] arr, int i, int blockIdx) {
        int startIdx = blockIdx * blockSize;
        int[][] fullTable= blockIdxToFullTable.get(blockIdx);
        int idxInArr = fullTable[i % blockSize][blockSize - 1] + startIdx;
        return arr[idxInArr];
    }

    @Override
    protected int getMinRightBlockLevel(int[] arr, int j, int blockIdx) {
        int startIdx = blockIdx * blockSize;
        int[][] fullTable= blockIdxToFullTable.get(blockIdx);
        int idxInArr = fullTable[0][j % blockSize] + startIdx;
        return arr[idxInArr];
    }

    public static void main(String[] args)  {

    }
}

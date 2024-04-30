import java.util.*;

public class AlgsForTesting {

    private static int getAns(Main.Node node, int compressedY) {
        if (node == null) return 0;
        int mid = (node.lowBorder + node.highBorder) / 2;
        if (compressedY < mid)
            return node.value + getAns(node.left, compressedY);
        else return node.value + getAns(node.right, compressedY);
    }

    private static Object[] compress(Main.Rectangle[] arr) {
        SortedSet<Integer> xCompressedCords = new TreeSet<>();
        SortedSet<Integer> yCompressedCords = new TreeSet<>();
        for (Main.Rectangle rec : arr) {
            xCompressedCords.add(rec.x1);
            xCompressedCords.add(rec.x2);
            yCompressedCords.add(rec.y1);
            yCompressedCords.add(rec.y2);
        }
        int[] xCCArray = xCompressedCords.stream().mapToInt(i -> i).toArray();
        int[] yCCArray = yCompressedCords.stream().mapToInt(i -> i).toArray();
        return new Object[]{xCCArray, yCCArray};
    }

    public static int binarySearch(int[] arr, int key) {
        int l = 0, r = arr.length, mid;
        while (l < r) {
            mid = l + (r - l) / 2;
            if (arr[mid] <= key)
                l = mid + 1;
            else
                r = mid;
        }
        return l - 1;
    }

    public static void third(Main.Rectangle[] arr, int[][] points, int n, int m) {
        long start, end;
        start = System.nanoTime();
        Object[] temp = compress(arr);
        int[] xCCArray = (int[]) temp[0];
        int[] yCCArray = (int[]) temp[1];

        List<Main.Event> events = new ArrayList<>();
        for (Main.Rectangle rec : arr) {
            events.add(new Main.Event(Main.Status.Start, binarySearch(yCCArray, rec.y2), binarySearch(yCCArray, rec.y1), binarySearch(xCCArray, rec.x1)));
            events.add(new Main.Event(Main.Status.End, binarySearch(yCCArray, rec.y2), binarySearch(yCCArray, rec.y1), binarySearch(xCCArray, rec.x2)));
        }
        events.sort(Comparator.comparing(Main.Event::getX));

        Main.PersistentTree tree = new Main.PersistentTree(events, yCCArray.length);

        end = System.nanoTime();
        System.out.println("подготовка третьей- " + (end - start));
        start = System.nanoTime();

        for (int i = 0; i < m; ++i) {
            int x = points[i][0], y = points[i][1];
            int ans = 0;
            int compressedX = binarySearch(xCCArray, x);
            int compressedY = binarySearch(yCCArray, y);
            if (compressedX >= 0 && compressedY >= 0 && tree.roots.size() > compressedX)
                ans = getAns(tree.roots.get(compressedX), compressedY);
        }
        end = System.nanoTime();
        System.out.println("выполнение третьей- " + (end - start));
    }

    public static void second(Main.Rectangle[] arr, int[][] points, int n, int m) {
        long start, end;
        start = System.nanoTime();
        Object[] temp = compress(arr);
        int[] xCCArray = (int[]) temp[0];
        int[] yCCArray = (int[]) temp[1];

        int[][] matrixMap = new int[xCCArray.length][yCCArray.length];

        for (Main.Rectangle rec : arr) {
            int x1 = binarySearch(xCCArray, rec.x1);
            int y1 = binarySearch(yCCArray, rec.y1);
            int x2 = binarySearch(xCCArray, rec.x2);
            int y2 = binarySearch(yCCArray, rec.y2);
            for (int i = x1; i < x2; ++i) {
                for (int j = y1; j < y2; ++j) {
                    ++matrixMap[i][j];
                }
            }
        }

        end = System.nanoTime();
        System.out.println("подготовка второй - " + (end - start));
        start = System.nanoTime();

        for (int i = 0; i < m; ++i) {
            int x = points[i][0], y = points[i][1];
            int ans = 0;
            int compressedX = binarySearch(xCCArray, x);
            int compressedY = binarySearch(yCCArray, y);
            if (compressedX >= 0 && compressedY >= 0)
                ans = matrixMap[compressedX][compressedY];
        }
        end = System.nanoTime();
        System.out.println("выполнение второй - " + (end - start));
    }

    public static void first(Main.Rectangle[] arr, int[][] points, int n, int m) {
        long start, end;
        start = System.nanoTime();
        for (int i = 0; i < m; ++i) {
            int x = points[i][0], y = points[i][1];
            int ans = 0;
            for (int j = 0; j < n; ++j) {
                if (arr[j].x1 <= x && x < arr[j].x2 && arr[j].y1 <= y && y < arr[j].y2) {
                    ++ans;
                }
            }
        }
        end = System.nanoTime();
        System.out.println("выполнение первой - " + (end - start));
    }
}

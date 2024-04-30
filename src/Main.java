import java.util.*;

public class Main {

    static class Testing {
        public static Rectangle[] generateRectangles(int n) {
            Rectangle[] rectangles = new Rectangle[n];
            for (int i = 0; i < n; ++i)
                rectangles[i] = new Rectangle(10 * i, 10 * i, 10 * (2 * n - i), 10 * (2 * n - i));
            return rectangles;
        }
        public static int[][] generatePoints(int m, int n) {
            int[][] points = new int[m][2];
            for (int i = 0; i < m; ++i) {
                points[i][0] = (int) (Math.pow(2689 * i, 31) % (20 * n));
                points[i][1] = (int) (Math.pow(3371 * i, 31) % (20 * n));
            }
            return points;
        }
        static Rectangle[] getRectangles(Scanner scanner, int n) {
            Rectangle[] arr = new Rectangle[n];
            for (int i = 0; i < n; ++i) {
                arr[i] = new Rectangle(scanner.nextInt(), scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
            }
            return arr;
        }
        static int[][] getPoints(Scanner scanner, int m) {
            int[][] arr = new int[m][2];
            for (int i = 0; i < m; ++i) {
                arr[i][0] = scanner.nextInt();
                arr[i][1] = scanner.nextInt();
            }
            return arr;
        }
    }
    static class Rectangle {
        int x1, y1, x2, y2;
        Rectangle (int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }

    enum Status {Start, End}
    static class Event {
        int x;
        int lowY;
        int highY;
        Status status;

        public Event(Status status, int highY, int lowY, int x) {
            this.status = status;
            this.highY = highY;
            this.lowY = lowY;
            this.x = x;
        }

        public int getX() {
            return x;
        }
    }

    static class Node {
        Node left, right;
        int lowBorder, highBorder, value;
        public Node (Node left, Node right, int lowBorder, int highBorder, int value) {
            this.left = left;
            this.right = right;
            this.lowBorder = lowBorder;
            this.highBorder = highBorder;
            this.value = value;
        }
    }

    static class PersistentTree {
        List<Node> roots = new ArrayList<>();

        public PersistentTree (List<Event> events, int yCCSize) {
            if (yCCSize == 0) roots.add(null);
            else this.buildPersistentTree(yCCSize, events);
        }

        Node buildTree(int leftBorder, int rightBorder) {
            if (leftBorder + 1 == rightBorder)
                return new Node(null, null, leftBorder, rightBorder, 0);
            int mid = (leftBorder + rightBorder) / 2;
            Node left = buildTree(leftBorder, mid);
            Node right = buildTree(mid, rightBorder);
            return new Node(left, right, left.lowBorder, right.highBorder, 0);
        }

        Node insert(Node root, int begin, int end, Status value) {
            if (begin <= root.lowBorder && root.highBorder <= end)
                return new Node(root.left, root.right, root.lowBorder, root.highBorder, value == Status.Start ? root.value + 1 : root.value - 1);
            if (root.highBorder <= begin || root.lowBorder >= end)
                return root;
            Node newNode = new Node(root.left, root.right, root.lowBorder, root.highBorder, root.value);
            newNode.left = insert(newNode.left, begin, end, value);
            newNode.right = insert(newNode.right, begin, end, value);
            return newNode;
        }

        void buildPersistentTree(int compressedYSize, List<Event> events) {
            Node root = buildTree(0, compressedYSize);
            int currentX = events.get(0).x;
            for (Event event : events) {
                if (currentX != event.x) {
                    roots.add(root);
                    currentX = event.x;
                }
                root = insert(root, event.lowY, event.highY, event.status);
            }
        }
    }

    private static int getAns(Node node, int compressedY) {
        if (node == null) return 0;
        int mid = (node.lowBorder + node.highBorder) / 2;
        if (compressedY < mid)
            return node.value + getAns(node.left, compressedY);
        else return node.value + getAns(node.right, compressedY);
    }

    private static Object[] compress(Rectangle[] arr) {
        SortedSet<Integer> xCompressedCords = new TreeSet<>();
        SortedSet<Integer> yCompressedCords = new TreeSet<>();
        for (Rectangle rec : arr) {
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

    public static void third() {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        Rectangle[] arr = Testing.getRectangles(scanner, n);
//        Rectangle[] arr = Testing.generateRectangles(n);

        Object[] temp = compress(arr);
        int[] xCCArray = (int[]) temp[0];
        int[] yCCArray = (int[]) temp[1];

        List<Event> events = new ArrayList<>();
        for (Rectangle rec : arr) {
            events.add(new Event(Status.Start, binarySearch(yCCArray, rec.y2), binarySearch(yCCArray, rec.y1), binarySearch(xCCArray, rec.x1)));
            events.add(new Event(Status.End, binarySearch(yCCArray, rec.y2), binarySearch(yCCArray, rec.y1), binarySearch(xCCArray, rec.x2)));
        }
        events.sort(Comparator.comparing(Event::getX));

        PersistentTree tree = new PersistentTree(events, yCCArray.length);

        int m = scanner.nextInt();

        int[][] points = Testing.getPoints(scanner, m);
//        int[][] points = Testing.generatePoints(m, n);

        for (int i = 0; i < m; ++i) {
            int x = points[i][0], y = points[i][1];
            int ans = 0;
            int compressedX = binarySearch(xCCArray, x);
            int compressedY = binarySearch(yCCArray, y);
            if (compressedX >= 0 && compressedY >= 0 && tree.roots.size() > compressedX)
                ans = getAns(tree.roots.get(compressedX), compressedY);
            System.out.print(ans + " ");
        }
    }

    public static void second() {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        Rectangle[] arr = Testing.getRectangles(scanner, n);
//        Rectangle[] arr = Testing.generateRectangles(n);

        Object[] temp = compress(arr);
        int[] xCCArray = (int[]) temp[0];
        int[] yCCArray = (int[]) temp[1];

        int[][] matrixMap = new int[xCCArray.length][yCCArray.length];

        for (Rectangle rec : arr) {
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

        int m = scanner.nextInt();

        int[][] points = Testing.getPoints(scanner, m);
//        int[][] points = Testing.generatePoints(m, n);

        for (int i = 0; i < m; ++i) {
            int x = points[i][0], y = points[i][1];
            int ans = 0;
            int compressedX = binarySearch(xCCArray, x);
            int compressedY = binarySearch(yCCArray, y);
            if (compressedX >= 0 && compressedY >= 0)
                ans = matrixMap[compressedX][compressedY];
            System.out.print(ans + " ");
        }
    }

    public static void first() {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        Rectangle[] arr = Testing.getRectangles(scanner, n);
//        Rectangle[] arr = Testing.generateRectangles(n);

        int m = scanner.nextInt();

        int[][] points = Testing.getPoints(scanner, m);
//        int[][] points = Testing.generatePoints(m, n);

        for (int i = 0; i < m; ++i) {
            int x = points[i][0], y = points[i][1];
            int ans = 0;
            for (int j = 0; j < n; ++j) {
                if (arr[j].x1 <= x && x < arr[j].x2 && arr[j].y1 <= y && y < arr[j].y2) {
                    ++ans;
                }
            }
            System.out.print(ans + " ");
        }
    }

    public static void main(String[] args) {
        long start, end;

        int n, m = 100_000;
        for (int i = 0; i < 14; ++i) {
            n = (int) Math.pow(2, i);
            System.out.println(n);
            start = System.nanoTime();
            AlgsForTesting.first(Testing.generateRectangles(n), Testing.generatePoints(m, n), n, m);
            end = System.nanoTime();
//        System.out.println("общее первой - " + (end - start));

            start = System.nanoTime();
            if (i < 12)
                AlgsForTesting.second(Testing.generateRectangles(n), Testing.generatePoints(m, n), n, m);
            end = System.nanoTime();
//        System.out.println("общее второй - " + (end - start));

            start = System.nanoTime();
            AlgsForTesting.third(Testing.generateRectangles(n), Testing.generatePoints(m, n), n, m);
            end = System.nanoTime();
        }
//        System.out.println("общее третьей - " + (end - start));
//        first();
//        second();
//        third();
    }
}
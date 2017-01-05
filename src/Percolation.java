
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private int n;

    private WeightedQuickUnionUF values;
    private WeightedQuickUnionUF backwash;

    private int topVirtualSite;
    private int bottomVirtualSite;

    private int openSitesCount = 0;
    private boolean[] openSites;

    // create n-by-n grid, with all sites blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n should be greater or equal to 1");
        }

        this.n = n;
        topVirtualSite = 0;
        bottomVirtualSite = n * n + 1;

        values = new WeightedQuickUnionUF(n * n + 2);
        backwash = new WeightedQuickUnionUF(n * n + 1);
        for (int i = 1; i <= n; i++) {
            values.union(topVirtualSite, containerIndex(1, i));
            values.union(bottomVirtualSite, containerIndex(n, i));
            backwash.union(topVirtualSite, containerIndex(1, i));
        }

        openSites = new boolean[n * n];
        for (int i = 0; i < n * n; i++) {
            openSites[i] = false;
        }
    }

    // open site (row, col) if it is not open already
    public void open(int row, int col) {
        if (isOpen(row, col)) {
            return;
        }

        int index = containerIndex(row, col);

        if (row > 1 && isOpen(row - 1, col)) {
            values.union(index, containerIndex(row - 1, col));
            backwash.union(index, containerIndex(row - 1, col));
        }

        if (row < n && isOpen(row + 1, col)) {
            values.union(index, containerIndex(row + 1, col));
            backwash.union(index, containerIndex(row + 1, col));
        }

        if (col > 1 && isOpen(row, col - 1)) {
            values.union(index, containerIndex(row, col - 1));
            backwash.union(index, containerIndex(row, col - 1));
        }

        if (col < n && isOpen(row, col + 1)) {
            values.union(index, containerIndex(row, col + 1));
            backwash.union(index, containerIndex(row, col + 1));
        }

        openSites[openSiteIndex(row, col)] = true;
        openSitesCount++;
    }

    // is site (row, col) open?
    public boolean isOpen(int row, int col) {
        int index = openSiteIndex(row, col);
        return openSites[index];
    }

    // is site (row, col) full?
    public boolean isFull(int row, int col) {
        return isOpen(row, col) && backwash.connected(topVirtualSite, containerIndex(row, col));
    }

    // number of open sites
    public int numberOfOpenSites() {
        return openSitesCount;
    }

    // does the system percolate?
    public boolean percolates() {
        if (n == 1) {
            return isOpen(1, 1);
        }

        return values.connected(topVirtualSite, bottomVirtualSite);
    }

    // Utilities

    private void assert2DIndexValid(int row, int column) {
        if (row < 1 || row > n) {
            throw new IndexOutOfBoundsException("row (" + row + ") should be within [1, " + n + "] range");
        }

        if (column < 1 || column > n) {
            throw new IndexOutOfBoundsException("column (" + column + ") should be within [1, " + n + "] range");
        }
    }

    private int containerIndex(int row, int column) {
        return map2DIndex(row, column, 1);
    }

    private int openSiteIndex(int row, int column) {
        return map2DIndex(row, column, 0);
    }

    private int map2DIndex(int row, int column, int offset) {
        assert2DIndexValid(row, column);
        return (row - 1) * n + (column - 1) + offset;
    }

    // test client (optional)
    public static void main(String[] args) {
        Percolation p = new Percolation(3);
        p.open(1, 1);
        p.open(3, 3);
        p.open(2, 3);
        p.open(2, 1);
        p.open(3, 1);

        assert p.isFull(3, 3) == false;
    }
}
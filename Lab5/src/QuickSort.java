/**
 * Working and tested sorting algorithm to sort any double array
 * 
 * 
 * original code take then tweaked from:
 * http://codereview.stackexchange.com/questions/4022/java-implementation-of-quick-sort
 */
public class QuickSort {

	/**
	 * Will sort an array
	 * @param a is the given array
	 * @param p - should make this 0
	 * @param r is the size of the array - 1
	 */
	public static void quickSort(double[] a, int p, int r) {
		if (p < r) {
			int q = partition(a, p, r);
			quickSort(a, p, q);
			quickSort(a, q + 1, r);
		}
	}

	private static int partition(double[] a, int p, int r) {
		double x = a[p];
		int i = p - 1;
		int j = r + 1;

		while (true) {
			i++;
			while (i < r && a[i] < x)
				i++;
			j--;
			while (j > p && a[j] > x)
				j--;

			if (i < j)
				swap(a, i, j);
			else
				return j;
		}
	}

	private static void swap(double[] a, int i, int j) {
		double temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}
}
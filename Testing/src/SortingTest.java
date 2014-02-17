public class SortingTest {

 
    public static void main(String[] args) {

        //int[] a = { 1, 23, 45, 2, 8, 134, 9, 4, 2000 };
        double a[]={23.3,44.2,1.5,2009,2.7,88,123,7,999,1040,88};
        quickSort(a, 0, a.length - 1);
        System.out.println(a[0]+", "+a[1]+", "+a[2]+", "+a[3]+", "+a[4]+", ");
    }

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
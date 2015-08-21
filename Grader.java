public class Grader {
	public static void main(String[] args) {
		int score = 0;
		int scoreMax = 50;

		// Fibonacci
		// Test 0
		if (FibonacciRecursive.fibonacci(0) == 0) {
			System.out.println("Test 1 Passed +10");
			score += 10;
		}
		// Test 1
		if (FibonacciRecursive.fibonacci(1) == 1) {
			System.out.println("Test 2 Passed +10");
			score += 10;
		}
		// Test 5
		if (FibonacciRecursive.fibonacci(5) == 5) {
			System.out.println("Test 3 Passed +10");
			score += 10;
		}
		// Test 10
		if (FibonacciRecursive.fibonacci(10) == 55) {
			System.out.println("Test 4 Passed +10");
			score += 10;
		}
		// Test -1
		if (FibonacciRecursive.fibonacci(-1) == 0) {
			System.out.println("Test 5 Passed +10");
			score += 10;
		}

		if (score != scoreMax) {
			System.out.println("Score: " + score + "/" + scoreMax);
			System.exit(1);
		} else {
			System.out.println("Score: " + score + "/" + scoreMax);
			System.exit(0);
		}
	}
}

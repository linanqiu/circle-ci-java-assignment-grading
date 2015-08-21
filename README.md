The code for this post can be found here: [https://github.com/linanqiu/circle-ci-java-assignment-grading](https://github.com/linanqiu/circle-ci-java-assignment-grading)

It's ridiculous we don't have a proper solution that uses Continuous Integration (CI) to grade homeworks hosted on GitHub.

CI, in short, is just a server that runs unit tests on your code every time you commit it. That means instead of you having to run unit tests before your commit your code (or manually run them after you commit them), a little robot checks your code against your unit tests every time you commit the code. That little robot never gets tired, doesn't need to be fed. In short, these little robots are way better than human Teaching Assistants (TAs) (who in contrast, need to be fed pizza).

Given that I'll be Head TAing a class with nearly 300 students this fall, I can't afford enough pizzas to keep my team running. Or maybe I can, but I prefer not to. So why not get CI servers to grade homeworks? That requires two things:

1. Getting students on GitHub (and submitting homeworks via GitHub)
2. Grading those homeworks on GitHub

I'll talk about (1) in a later post when I have the system fully set up, but basically it involves playing nice to GitHub and getting a team with lots and lots of private repositories. Then you give each student one private repository (so that they can't copy. But then again, since they're all on GitHub, it should be easy to implement a cheat check. That's for next time).

## Homework Assignment

Let's say that the student is supposed to write an assignment like this:

```java
public class FibonacciRecursive {

    public static void main(String[] args) {
        System.out.println(fibonacci(5));
    }

    public static int fibonacci(int n) {
        // this is the method that student codes up
        if (n < 0) {
            return 0;
        }

        if (n == 0) {
            return 0;
        } else if (n == 1) {
            return 1;
        } else {
            return fibonacci(n - 1) + fibonacci(n - 2);
        }
    }
}
```

This happens in a file called `FibonacciRecursive.java`. (Wow surprise).

This is usually one of the first assignments in a data structures class or a last assignment in an intro programming class. Whatever it is, we can be sure of the following:

1. You do not want to introduce unnecessary files into the homework package that the student works with. This confuses a student easily, and no good packaging software exists for Java which is as simple and intuitive as Grunt or NPM in JavaScript. You probably don't want to use Maven or Ant.
2. You probably want to avoid unit tests, since they make it difficult for you to score the entire assignment, run asynchronously, and produce rather ugly output.

Given those two limitations, you are set back by quite a bit, since most CI servers only run well on those systems. Give it Java with Maven and CircleCI (one of the larger CI services out there that provide free CI) will work like magic. However, give it non standard stuff and it coughs blood. Unfortunately, that bloody puddle is what we have to work with.

## Setting up CircleCI

(Side note: I chose CircleCI not TravisCI because Circle allows for private repositories while Travis insists on public).

Without a single unit test command, we have to be a little hacky with CircleCI. Now here's how CircleCI runs in a nutshell:

1. CircleCI reads off `circle.yml` in your repository. If it finds that file, it follows the instructions in that file. Otherwise, it tries to infer your environment (be it some Ruby thingum, Maven for Java, or Grunt for JS).
2. It runs the commands in `circle.yml` or the inferred commands.
3. Specifically, in the unit test portion, it runs every command in the unit test portion. If all of them produces an exit code of 0 (that means everything's good), the unit tests pass. If any of them produce a non-0 exit code, unit tests fail, you get an error.
4. The results of these get emailed to you.

That sounds cool right? So let's start appropriating that system for homework grading.

## Game Plan

Your directory will end up containing

```
- FibonacciRecursive.java
- Grader.java
- build.sh
- circle.yml
```

Here's how everything works:

1. CircleCI reads `circle.yml` which tells the server to run `build.sh` instead of running standard unit tests
2. `build.sh` does the following
    1. It tries to compile `FibonacciRecursive.java` and `Grader.java`
    2. It runs Grader (now compiled as a class). Grader runs a series of tests on FibonacciRecursive, printing out the score that the student receives along each step. Depending on the score, Grader returns with an exit code of either 0 or 1.
3. `build.sh` returns whatever exit code Grader returns.
4. CircleCI consumes that exit code.

And this is how we'll abuse CircleCI to grade homeworks.

## Overriding `circle.yml`

We know that we have to override `circle.yml` to not try and run Maven / Ant / freeze up and die when we run our custom Java stuff. What's simpler than making it run a `.sh` script?

```yaml
## Customize the test machine
machine:
  # Version of java to use
  java:
    version: oraclejdk8

## Customize test commands
test:
  override:
    - sh build.sh
```

That's all that should go into your `circle.yml`.

## Creating `build.sh`

Now your `build.sh` should just compile every `.java` file in your directory and run Grader. So that's exactly what it does:

```sh
javac *.java
java Grader
exit
```

## Creating a Grader Tool in Java

```java
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
        if (FibonacciRecursive.fibonacci(-1) == 1) {
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
```

This should be rather self explanatory -- you're basically making a series of "assertions", but tabulating scores at the same time.


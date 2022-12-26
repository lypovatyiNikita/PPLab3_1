import java.lang.reflect.Constructor;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Main {
    public static void main(String[] args) {
        MultiThreadCalculator calculator = new MultiThreadCalculator();
        calculator.Start();
    }
}

class MultiThreadCalculator{
    public long MultiThreadSum = 0;
    public  int ArrayElementsSum = 0;

    public void Start(){
        Scanner input = new Scanner(System.in);

        System.out.print("Input a array size: ");
        int arraySize = input.nextInt();
        int[] calculateArray = new int[arraySize];
        for (int i = 0; i < arraySize; i++){
            calculateArray[i] = i;
        }

        System.out.print("Input number of threads: ");
        int maxThreads = input.nextInt();

        CyclicBarrier newCyclicBarrier= new CyclicBarrier(maxThreads, new CalculatorEnd(this));

        for (int i = 0; i<maxThreads-1; i++){
            Thread newThread = new Thread(new CalculatorThread(this, calculateArray, arraySize / maxThreads * i, (arraySize / maxThreads * (i+1))-1, newCyclicBarrier));
            newThread.start();
        }
        Thread newThread = new Thread(new CalculatorThread(this, calculateArray, arraySize / maxThreads * (maxThreads-1), arraySize-1, newCyclicBarrier));
        newThread.start();

        ArrayElementsSum = 0;
        for (int i = 0; i<arraySize;i++){
            ArrayElementsSum+=calculateArray[i];
        }
    }

    public synchronized  void  AddPartOfSum(long partSum){
        MultiThreadSum +=partSum;
    }
}

class  CalculatorEnd extends Thread {
    MultiThreadCalculator calculatorRef;
    public  CalculatorEnd(MultiThreadCalculator calculator){
        calculatorRef = calculator;
    }

    @Override
    public void run() {
        System.out.println("One thread sum: "+ calculatorRef.ArrayElementsSum);
        System.out.println("Multiply thread sum: " + calculatorRef.MultiThreadSum);
    }
}

class CalculatorThread implements Runnable{

    public long thisSum = 0;
    int[] ThisArray;
    int begin, end;
    MultiThreadCalculator calculatorRef;
    CyclicBarrier cyclicBarrierRef;

    CalculatorThread(MultiThreadCalculator calculator, int[] array, int begin, int end, CyclicBarrier barrierRef) {
        this.begin = begin;
        this.end = end;
        ThisArray = array;
        calculatorRef = calculator;
        cyclicBarrierRef = barrierRef;
    }

    @Override
    public void run() {
        for (int i = begin; i <= end; i++) {
            thisSum = thisSum + ThisArray[i];
        }
        calculatorRef.AddPartOfSum(thisSum);
        try {
            cyclicBarrierRef.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }
}
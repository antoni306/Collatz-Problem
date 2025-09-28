package zad1;


import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class CollatzProblem {
    public enum State {
        CREATED,
        RUNNING,
        CANCELLED,
        FINISHED
    }

    private static volatile Map<BigInteger, Future<List<BigInteger>>> futureList = new HashMap<>();
    private static volatile Map<BigInteger, State> status = new HashMap<>();
    private static final Object LockStatus = new Object();
    private static final Object LockFuture = new Object();
    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    public static List<BigInteger> getFuture(BigInteger number) {
        try {
            return futureList.get(number).get();

        }catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static State getStatus(BigInteger number) {
        return status.get(number);
    }
    public static void addThread(BigInteger number) {
        synchronized (LockStatus) {
            status.put(number, State.CREATED);
        }
    }
    public static void startThread(BigInteger number) {
        List<BigInteger> list = Collections.synchronizedList(new ArrayList<>());
        BigInteger[] n = {number};
        Future<List<BigInteger>> future = executor.submit(() -> {

            while (n[0].compareTo(BigInteger.ONE)!=0 && n[0].compareTo(BigInteger.valueOf(-1)) != 0) {
                list.add(n[0]);

                if(number.compareTo(BigInteger.ONE) < 0) {
                    if(isCycled(list))
                        return list;
                }

                if (n[0].mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
                    n[0] = n[0].divide(BigInteger.valueOf(2));
                } else {
                    n[0] = n[0].multiply(BigInteger.valueOf(2).add(BigInteger.ONE)).add(BigInteger.ONE);
                }
            }
            status.put(number, State.FINISHED);
            list.add(n[0]);
            return list;
        });
        synchronized (LockStatus){
            status.put(number, State.RUNNING);
        }
        synchronized (LockFuture) {
            futureList.put(number, future);
        }
    }
    public static void cancelThread(BigInteger number){
        synchronized (LockFuture){
            futureList.get(number).cancel(true);
            status.put(number, State.CANCELLED);
        }
        if(status.entrySet().stream().noneMatch(e->e.getValue() == State.RUNNING)){
            executor.shutdown();
            executor=Executors.newSingleThreadExecutor();
        }
    }

    private static boolean isCycled(List<BigInteger> list){
        Map<BigInteger,Long> check=list.stream().collect(Collectors.groupingBy(i->i,Collectors.counting()));
        return check.entrySet().stream().anyMatch(entry->entry.getValue()>1);
    }
}

import java.util.stream.Stream;

public class Summary<T> {
    public final T min;
    public final T max;
    public final int count;

    private Summary(T min, T max, int count) {
        this.min = min;
        this.max = max;
        this.count = count;
    }

    public static <T extends Comparable<T>> Summary<T> ofStream(Stream<? extends T> stream) {
        if(stream.isParallel()) {
            SummaryShell<T> shell = stream
                    .map(x -> new SummaryShell(x, x, 1))
                    .reduce((x, y) -> new SummaryShell(
                            x.min.compareTo(y.min) < 0 ? x.min : y.min,
                            x.max.compareTo(y.max) > 0 ? x.max : y.max,
                            x.count + y.count))
                    .get();
            return new Summary(shell.min, shell.max, shell.count);
        } else {
            SummaryAnalog<T> summaryAnalog = new SummaryAnalog<>();
            stream.forEach(x -> summaryAnalog.update(x));
            return new Summary(summaryAnalog.min, summaryAnalog.max, summaryAnalog.count);
        }
    }

    private static class SummaryAnalog<T extends Comparable<T>> {
        private T min;
        private T max;
        private int count = 0;

        public void update(T x) {
            min = min == null ? x : (min.compareTo(x) < 0 ? min : x);
            max = max == null ? x : (max.compareTo(x) > 0 ? max : x);
            ++count;
        }
    }

    private static class SummaryShell<T extends Comparable<T>>{
        final T min;
        final T max;
        final int count;

        public SummaryShell(T min, T max, int count) {
            this.min = min;
            this.max = max;
            this.count = count;
        }
    }
}

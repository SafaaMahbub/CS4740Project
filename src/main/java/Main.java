import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
public class Main {
    static String file = "borg_traces_data.csv";

    public static void main (String[] args) throws IOException {
        Preprocessing p = new Preprocessing();

        List<Row> test = p.loadValues(file);

        // System.out.println("There are" + test.size() + "rows");

        Queue<Map.Entry<Long, List<Row>>> sorted = p.sortByTaskAndTime(test);
        // System.out.println(sorted.toString());
        p.normalizeColumns(test);

        SequenceData dataset = p.createSequences(sorted, 20);

        int splitIndex = (int) (dataset.getSequences().size() * 0.7);
        List<double[][]> X_train = dataset.getSequences().subList(0, splitIndex);
        List<double[][]> X_test = dataset.getSequences().subList(splitIndex, dataset.getSequences().size());

        List<double[]> y_train = dataset.getLabels().subList(0, splitIndex);
        List<double[]> y_test  = dataset.getLabels().subList(splitIndex, dataset.getLabels().size());

        p.exportSequencesToCSV(new SequenceData(X_train, y_train), "X_train.csv", "y_train.csv");
        p.exportSequencesToCSV(new SequenceData(X_test, y_test), "X_test.csv", "y_test.csv");

    /*Queue<Map.Entry<Long, List<Row>>> scaled_sorted_list = p.sortByTaskAndTime(test);
    System.out.println(scaled_sorted_list.toString());*/
    }
}
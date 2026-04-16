import java.util.List;

public class SequenceData {
    private List<double[][]> sequences;
    private List<double[]> labels;

    public SequenceData(List<double[][]> sequences, List<double[]> labels) {
        this.sequences = sequences;
        this.labels = labels;
    }

    public List<double[][]> getSequences() {
        return sequences;
    }

    public List<double[]> getLabels() {
        return labels;
    }
}
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Map.*;


public class Preprocessing {
    public List<Row> loadValues(String path) {
        List<Row> lines = new ArrayList<>();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(path));
            String row;
            bf.readLine(); //skipping the first line
            bf.readLine(); //skip the second line
            while ((row = bf.readLine()) != null) {
                String[] data = parseLine(row);
                if (data.length < 10 || Arrays.stream(data).anyMatch(s -> s == null || s.trim().isEmpty())) {
                    continue;
                }
                try {
                    long timestamp = Long.parseLong(data[1]);
                    int eventType = Integer.parseInt(data[2]);
                    String collectionID = data[3];
                    long startTime = Long.parseLong(data[4]);
                    long endTime = Long.parseLong(data[5]);
                    String avgUsageCPUStr = data[6];
                    double avgUsageCPU = Double.parseDouble(avgUsageCPUStr.replaceAll("[^\\d.]", "").trim());

                    String avgUsageMemoryStr = data[7];
                    double avgUsageMemory = Double.parseDouble(avgUsageMemoryStr.replaceAll("[^\\d.]", "").trim());
                    String maxUsageCPUStr = data[8];
                    double maxUsageCPU = Double.parseDouble(maxUsageCPUStr.replaceAll("[^\\d.]", "").trim());
                    String maxUsageMemoryStr = data[9];
                    double maxUsageMemory = Double.parseDouble(maxUsageMemoryStr.replaceAll("[^\\d.]", "").trim());
                    double assignedMemory = Double.parseDouble(data[10]);

                    Row dataRow = new Row(timestamp, eventType, collectionID, startTime, endTime, avgUsageCPU, avgUsageMemory, maxUsageCPU, maxUsageMemory, assignedMemory);
                    lines.add(dataRow);
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }

    public static String[] parseLine(String line) {
        return line.split(",");
    }

    public Queue<Entry<Long, List<Row>>> sortByTaskAndTime(List<Row> data) {
        return data.stream().collect(
                        Collectors.groupingBy(
                                r -> Long.parseLong(r.getCollectionID())
                        )
                )
                .entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey(), e.getValue().stream().sorted(Comparator.comparingLong(Row::getTimestamp)).toList()))
                .collect(Collectors.toCollection(
                                () -> new PriorityQueue<>(Entry.<Long, List<Row>>comparingByKey().reversed())
                        )
                );
    }

    public Map<Row, List<Integer>> oneHotEncoding(List<Row> data) {
        int[] uniqueEventTypes = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        /*Map<Integer,Integer> eventTypeToVectorIndex = new HashMap<>();
        int i = 0;
        for(int type: uniqueEventTypes){
            eventTypeToVectorIndex.put(type,i++);
        }*/
        Map<Row, List<Integer>> encodingMap = new HashMap<>();
        for (Row r : data) {
            List<Integer> v = new ArrayList<>(Collections.nCopies(11, 0));
            encodingMap.put(r, v);
        }
        return encodingMap;
    }


    public void normalizeColumns(List<Row> data) {
        /*int size = data.size();
        INDArray features = Nd4j.create(size,5);

        for(int i = 0; i<size;i++){
        Row r = data.get(i);
            features.putScalar(i,0,r.getAvgUsageCPU());
            features.putScalar(i,1,r.getAvgUsageMemory());
            features.putScalar(i,2,r.getMaxUsageCpu());
            features.putScalar(i,3,r.getAvgUsageMemory());
            features.putScalar(i,4,r.getAssignedMemory());

            DataSet set = new DataSet(features,features);

            NormalizerMinMaxScaler scaler = new NormalizerMinMaxScaler(0,1);
            scaler.fit(set);
            scaler.transform(set);

            INDArray scaledArray = set.getFeatures();

            for(int z = 0; z<size;z++){
                Row currentR = data.get(z);
                currentR.setAvgUsageCPU(scaledArray.getDouble(z,0));
                currentR.setAvgUsageMemory(scaledArray.getDouble(z,1));
                currentR.setMaxUsageCpu(scaledArray.getDouble(z,2));
                currentR.setMaxUsageMemory(scaledArray.getDouble(z,3));
                currentR.setAssignedMemory(scaledArray.getDouble(z,4));
            }

        }
    }*/
        double minCPU = data.stream().mapToDouble(Row::getAvgUsageCPU).min().getAsDouble();
        double maxCPU = data.stream().mapToDouble(Row::getAvgUsageCPU).max().getAsDouble();

        double minMemory = data.stream().mapToDouble(Row::getAvgUsageMemory).min().getAsDouble();
        double maxMemory = data.stream().mapToDouble(Row::getAvgUsageMemory).max().getAsDouble();

        double minMaxCPU = data.stream().mapToDouble(Row::getMaxUsageCpu).min().getAsDouble();
        double maxMaxCPU = data.stream().mapToDouble(Row::getMaxUsageCpu).max().getAsDouble();

        double minMaxMemory = data.stream().mapToDouble(Row::getMaxUsageMemory).min().getAsDouble();
        double maxMaxMemory = data.stream().mapToDouble(Row::getMaxUsageMemory).max().getAsDouble();

        double minAssigned = data.stream().mapToDouble(Row::getAssignedMemory).min().getAsDouble();
        double maxAssigned = data.stream().mapToDouble(Row::getAssignedMemory).max().getAsDouble();

        for (Row r : data) {
            r.setAvgUsageCPU((r.getAvgUsageCPU() - minCPU) / (maxCPU - minCPU));
            r.setAvgUsageMemory((r.getAvgUsageMemory() - minMemory) / (maxMemory - minMemory));
            r.setMaxUsageCpu((r.getMaxUsageCpu() - minMaxCPU) / (maxMaxCPU - minMaxCPU));
            r.setMaxUsageMemory((r.getMaxUsageMemory() - minMaxMemory) / (maxMaxMemory - minMaxMemory));
            r.setAssignedMemory((r.getAssignedMemory() - minAssigned) / (maxAssigned - minAssigned));
        }
    }

    public Map<String, List<Row>> splitData(List<Row> data) {
        Map<String, List<Row>> splitData = new HashMap<>();

        List<Row> trainingSet = new ArrayList<>(data.subList(0, (int) (data.size() * 0.7)));
        List<Row> testingSet = new ArrayList<>(data.subList((int) (data.size() * 0.7), data.size()));

        splitData.put("train", trainingSet);
        splitData.put("test", testingSet);

        return splitData;
    }

    /**
     * Creates sequences of data and corresponding labels based on a sliding window.
     *
     * @param sortedData a queue of entries, where each entry contains a long key (e.g. identifier)
     *                   and a list of Row objects representing data points.
     * @param windowSize the size of the sliding window to generate sequences of data.
     * @return a SequenceData object containing the generated sequences and their corresponding labels.
     */
    public SequenceData createSequences(Queue<Map.Entry<Long, List<Row>>> sortedData, int windowSize) {
        List<double[][]> sequences = new ArrayList<>();
        List<double[]> labels = new ArrayList<>();

        for (var e : sortedData) {
            var rows = e.getValue();

            if (rows.size() <= windowSize) continue;

            for (int i = 0; i < rows.size() - windowSize; i++) {
                var sequence = new double[windowSize][5];

                for (int j = 0; j < windowSize; j++) {
                    var r = rows.get(i + j);
                    sequence[j][0] = r.getAvgUsageCPU();
                    sequence[j][1] = r.getAvgUsageMemory();
                    sequence[j][2] = r.getMaxUsageCpu();
                    sequence[j][3] = r.getMaxUsageMemory();
                    sequence[j][4] = r.getAssignedMemory();
                }

                Row next = rows.get(i + windowSize);
                double[] label = new double[5];
                label[0] = next.getAvgUsageCPU();
                label[1] = next.getAvgUsageMemory();
                label[2] = next.getMaxUsageCpu();
                label[3] = next.getMaxUsageMemory();
                label[4] = next.getAssignedMemory();

                sequences.add(sequence);
                labels.add(label);
            }
        }

        return new SequenceData(sequences, labels);
    }

    public void exportSequencesToCSV(SequenceData data, String xPath, String yPath) throws IOException {

        BufferedWriter xWriter = new BufferedWriter(new FileWriter(xPath));
        BufferedWriter yWriter = new BufferedWriter(new FileWriter(yPath));

        for (int i = 0; i < data.getSequences().size(); i++) {

            double[][] seq = data.getSequences().get(i);
            double[] label = data.getLabels().get(i);

            StringBuilder sb = new StringBuilder();

            for (double[] step : seq) {
                for (double v : step) {
                    sb.append(v).append(",");
                }
            }

            sb.setLength(sb.length() - 1);
            xWriter.write(sb.toString());
            xWriter.newLine();

            StringBuilder lb = new StringBuilder();
            for (double v : label) {
                lb.append(v).append(",");
            }

            lb.setLength(lb.length() - 1);
            yWriter.write(lb.toString());
            yWriter.newLine();
        }

        xWriter.close();
        yWriter.close();
    }
}

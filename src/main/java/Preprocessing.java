import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Map.*;


public class Preprocessing {

    String file = "test_csv.csv";

    public static void main(String[] args) {
        Preprocessing p = new Preprocessing();
        List<Row> test = p.loadValues(p.file);
        System.out.println("There are" + test.size() + "rows");
        Queue<Entry<Long, List<Row>>> sorted_list = p.sortByTaskAndTime(test);
        System.out.println(sorted_list.toString());
        p.normalizeColumns(test);
        Queue<Entry<Long, List<Row>>> scaled_sorted_list = p.sortByTaskAndTime(test);
        System.out.println(scaled_sorted_list.toString());
    }

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
            List<Integer> v = new ArrayList<>(Collections.nCopies(11,0));
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
                r.setMaxUsageCpu((r.getMaxUsageCpu() - minMaxCPU)/(maxMaxCPU-minMaxCPU));
                r.setMaxUsageMemory((r.getMaxUsageMemory() - minMaxMemory)/(maxMaxMemory-minMaxMemory));
                r.setAssignedMemory((r.getAssignedMemory() - minAssigned) / (maxAssigned - minAssigned));
            }
        }

        public Map<String,List<Row>> splitData (List<Row> data)
        {
            Map<String, List<Row>> splitData =new HashMap<>();

            List<Row> trainingSet = new ArrayList<>(data.subList(0,(int)(data.size()*0.7)));
            List<Row> TestingSet = new ArrayList<>(data.subList(0,(int)(data.size()*0.3)));
        }
}

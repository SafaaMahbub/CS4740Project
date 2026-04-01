import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.spark.ml.feature.MinMaxScaler;
import org.apache.spark.ml.feature.MinMaxScalerModel;

public class Preprocessing {

    String file = "borg_traces_data.csv";

    public static void main(String[] args){
        Preprocessing p = new Preprocessing();
        List<Row> test = p.loadValues(p.file);
        System.out.println("There are" + test.size()+ "rows");
        Map<String, List<Row>> sorted_list = p.sortByTaskAndTime(test);
        //System.out.println(sorted_list.toString());
        Map<Row, Vector<Integer>> oneHotEncoding = p.oneHotEncoding(test);
        System.out.println(oneHotEncoding.toString());


    }

    public List<Row> loadValues(String path)
    {
        List<Row> lines = new ArrayList<>();
        try{
            BufferedReader bf = new BufferedReader(new FileReader(path));
            String row;
            bf.readLine(); //skipping the first line
            bf.readLine(); //skip the second line
            while((row = bf.readLine())!=null){
                String[] data = parseLine(row);
                if(data.length<10 || Arrays.stream(data).anyMatch(s -> s == null || s.trim().isEmpty())){
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


                    Row dataRow = new Row(timestamp, eventType, collectionID, startTime,endTime,avgUsageCPU,avgUsageMemory,maxUsageCPU,maxUsageMemory,assignedMemory);
                    lines.add(dataRow);
                }
                catch (NumberFormatException e){
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

    public static String[] parseLine(String line){
        return line.split(",");
    }

    public Map<String, List<Row>> sortByTaskAndTime(List<Row> data){
        Map<String, List<Row>> reformatData = data.stream()
                .collect(Collectors.groupingBy(Row::getCollectionID));

        for(List<Row> currentRow : reformatData.values())
        {
            currentRow.sort(Comparator.comparingDouble(Row::getTimestamp));
        }
        return reformatData;
    }

    public Map<Row, Vector<Integer>> oneHotEncoding (List<Row> data)
    {
        int [] uniqueEventTypes = {0,1,2,3,4,5,6,7,8,9,10};

        /*Map<Integer,Integer> eventTypeToVectorIndex = new HashMap<>();
        int i = 0;
        for(int type: uniqueEventTypes){
            eventTypeToVectorIndex.put(type,i++);
        }*/
        Map <Row, Vector<Integer>> encodingMap = new HashMap<>();
        for(Row r : data)
        {
            int index = r.getEventType();
            Vector<Integer> v = new Vector<>();
            v.setSize(11);
            for(int j = 0; j<v.size();j++){
                if (j == index){
                    v.set(j, 1);
                }
                else{
                    v.set(j, 0);
                }
                encodingMap.put(r,v);
            }
        }
        return encodingMap;
    }








}

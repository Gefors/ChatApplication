package Entity;

import java.text.DateFormat;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Logger implements Serializable {
    /**
     * Writes a message to the file (Server log)
     * @param fileName
     * @param logMessage
     */
    public void writeToFile(String fileName, String logMessage){
        try {
            FileWriter fileWriter = new FileWriter(fileName, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            if (logMessage != null){
                Date now = new Date();
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timestamp = format.format(now);
                String output = String.format("%s; Occurrence: %s",timestamp,logMessage);
                bufferedWriter.write(output);
                bufferedWriter.newLine();
            }

            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    /**
     * Get the log from a file between two dates
     * @param fileName
     * @param startTime
     * @param endTime
     */
    public ArrayList<String> getLogBetweenPeriod(String fileName, Date startTime, Date endTime){
        ArrayList<String> logs = new ArrayList<String>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String line;

        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));

            while ((line = br.readLine()) != null){

                String[] parts = line.split(";");
                Date time = simpleDateFormat.parse(parts[0]);

                if (time.after(startTime) && time.before(endTime)){

                    logs.add(line);
                }
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println(logs);
        return logs;
    }
}

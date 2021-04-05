import java.io.*;
import java.util.*;

public class Main {


    public static void main(String[] args) throws IOException {

        File temperature = new File(("temperature.txt"));
        List<Double> temperaturInCelsius = new ArrayList<>();

        Date start = new Date();


        Timer timer = new Timer();

        TimerTask adcValueToCelsius = new TimerTask() {
            @Override
            public void run() {
                try {
                    double adcRawValue = getTemperature(temperature);

                    //formel for omgjøring av adc raw value til celsius
                    double celcius = (adcRawValue / (Math.pow(2, 12) - 1) * 100) - 50;
                    temperaturInCelsius.add(celcius);



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };



        TimerTask postJson = new TimerTask() {
            @Override
            public void run() {

                Date end = new Date();

                double max = (Collections.max(temperaturInCelsius));
                double min = (Collections.min(temperaturInCelsius));

                int total = 0;
                double avg = 0;

                for(int i = 0; i < temperaturInCelsius.size(); i++){
                    total += temperaturInCelsius.get(i);
                    avg = total/temperaturInCelsius.size();
                }

                double version = 0.1;

                String url = "http://localhost:5000/api/temperature";

                String[] fields = {
                        "start:" + start.toString() + ";",
                        "end:" + end.toString() + ";",
                        "min:" + min + ";",
                        "max:" + max + ";",
                        "average:" + avg + ";"

                };
                Connection connection = new Connection(fields, url, version);
                System.out.println(connection.getEndpoints());
                connection.setUserAgent("Mozilla/5.0");

                String response  = connection.buildConnection();
                System.out.println(response);


            }
        };

        //100ms
        timer.scheduleAtFixedRate(adcValueToCelsius, 0, 100);
        //2min
        timer.scheduleAtFixedRate(postJson, 120000, 120000);

    }


    //Get a random line from file, convert it from String to int, trim and return.
    public static double getTemperature(File temperature) throws IOException {
        String result = null;
        Random rand = new Random();
        int count = 0;
        for(Scanner sc = new Scanner(temperature); sc.hasNext(); ){
            count++;
            String line = sc.nextLine();
            if(rand.nextInt(count) == 0)
                result = line;

        }
        double temp = Integer.parseInt(result.trim());
        return temp;
    }


 }


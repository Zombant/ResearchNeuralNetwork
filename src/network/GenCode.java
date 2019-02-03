package network;

import java.util.ArrayList;
import java.util.List;

public class GenCode {
    public static void main(String[] args) {

        List<List<String>> array = new ArrayList<>();
        array = CSVReader.readCSV("InputData/Data.csv");

        String totalCode = "";
        for(int i = 0; i < 64; i++) {
            totalCode += "double[] input" + i + " = new double[]{" + array.get(i).get(0) + " / 1000.0};\n" + "double[] target" + i + " = new double[]{" + array.get(i).get(1) + " / 100.0};\n";
        }
        //totalCode += "for (int i = 0; i < 1000000; i++) {";
        for(int i = 0; i < 64; i++){
             //totalCode += "\nnetwork.train(input" + i + ", target" + i + ", learningRate);";
        }
        //totalCode += "}";
        System.out.println(totalCode);

    }
}

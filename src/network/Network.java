package network;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Network {

    //Create List of layers
    public Layer[] layersArray;


    public Network(int... NUMBER_OF_NEURONS_IN_LAYER) {
        //Set the number of layers
        layersArray = new Layer[NUMBER_OF_NEURONS_IN_LAYER.length];

        //Set the size of each layer
        for (int i = 0; i < NUMBER_OF_NEURONS_IN_LAYER.length; i++) {
            layersArray[i] = new Layer(NUMBER_OF_NEURONS_IN_LAYER[i]);
        }

        //iterate through each layer
        for (int i = 1; i < layersArray.length; i++) {
            layersArray[i].setAttributesOfNeurons(layersArray[i - 1]);
        }

    }

    //Feed Forward method
    public double[] feedForward(double... input) {
        //Make sure that the input parameter matches the number of input neurons
        if (input.length != this.layersArray[0].layerSize) {
            return null;
        }

        //Output of first layer is the same as the input
        for (int i = 0; i < layersArray[0].neuronList.length; i++) {
            layersArray[0].neuronList[i].neuronOutput = input[i];
        }


        //for every layer, starting at 1 (because first layer is just the input
        for (int layer = 1; layer < layersArray.length; layer++) {
            for (int neuron = 0; neuron < layersArray[layer].layerSize; neuron++) {

                //Get the sum and enter it into the activation function
                double sum = 0;
                for (int prevNeuron = 0; prevNeuron < layersArray[layer - 1].layerSize; prevNeuron++) {
                    //TODO: TEST THIS LINE
                    sum += layersArray[layer - 1].neuronList[prevNeuron].neuronOutput * layersArray[layer].neuronList[neuron].neuronWeights[prevNeuron];
                }
                sum += layersArray[layer].neuronList[neuron].bias;
                layersArray[layer].neuronList[neuron].neuronOutput = sigmoid(sum);

                //Part of backpropagation
                //TODO: Make comment
                layersArray[layer].neuronList[neuron].outputDerivative = layersArray[layer].neuronList[neuron].neuronOutput * (1 - layersArray[layer].neuronList[neuron].neuronOutput);

            }
        }
        //TODO: Create a loop that makes an array of output values
        double[] outputArray = new double[layersArray[layersArray.length - 1].layerSize];
        for (int i = 0; i < outputArray.length; i++) {
            outputArray[i] = layersArray[layersArray.length - 1].neuronList[i].neuronOutput;
        }
        return outputArray;

    }

    //TODO: Make comment
    public void train(double[] input, double[] target, double learningRate) {
        //Make sure input and neuronOutputs data are equal to amount needed
        if(input.length != layersArray[0].layerSize || target.length != layersArray[layersArray.length - 1].layerSize){ return; }
        feedForward(input);
        backPropagation(target);
        updateWeights(learningRate);
    }

    //Backpropagation
    public void backPropagation(double[] target) {

        //Loop though all output neurons
        for (int neuron = 0; neuron < layersArray[layersArray.length - 1].layerSize; neuron++) {
            //See graphics folder -> error_signal_equation.png
            layersArray[layersArray.length - 1].neuronList[neuron].errorSignal = (layersArray[layersArray.length - 1].neuronList[neuron].neuronOutput - target[neuron]) * layersArray[layersArray.length - 1].neuronList[neuron].outputDerivative;
        }

        //Loop through hidden layers
        for (int layer = layersArray.length - 2; layer > 0; layer--) {
            //Loop through neurons of hidden layers
            for (int neuron = 0; neuron < layersArray[layer].layerSize; neuron++) {
                //See second row of first equation in graphic
                double sum = 0;
                for (int nextNeuron = 0; nextNeuron < layersArray[layer + 1].layerSize; nextNeuron++) {
                    //Increase sum by weight that connects current neuron to next neuron
                    sum += layersArray[layer + 1].neuronList[nextNeuron].neuronWeights[neuron] * layersArray[layer + 1].neuronList[nextNeuron].errorSignal;
                }
                layersArray[layer].neuronList[neuron].errorSignal = sum * layersArray[layer].neuronList[neuron].outputDerivative;

            }
        }

    }

    //Update the neuronWeights
    public void updateWeights(double learningRate) {
        for (int layer = 1; layer < layersArray.length; layer++) {
            for (int neuron = 0; neuron < layersArray[layer].neuronList.length; neuron++) {
                for (int prevNeuron = 0; prevNeuron < layersArray[layer - 1].neuronList.length; prevNeuron++) {
                    //Weights[layer][neuron][prevNeuron]
                    //TODO: DEBUG LINE
                    double changeInWeights = -learningRate * layersArray[layer - 1].neuronList[prevNeuron].neuronOutput * layersArray[layer].neuronList[neuron].errorSignal;
                    layersArray[layer].neuronList[neuron].neuronWeights[prevNeuron] += changeInWeights;
                }
                //Delta for neuronBiases
                double changeInBiases = -learningRate * 1 * layersArray[layer].neuronList[neuron].bias;
                layersArray[layer].neuronList[neuron].bias += changeInBiases;
            }
        }
    }


    private double sigmoid(double x){
        return 1d / (1 + Math.exp(-x));
    }

    public static void main(String[] args) {
        /*
        Create a network
            new Network(4,2,3,1) will have 4 neurons in first layer, etc.
         */
        Network network = new Network(54, 10, 1);

        List<List<String>> arrayString = new ArrayList<>();
        arrayString = CSVReader.readCSV("data/divorce.csv");

        //Converts array of strings to 2 Lists of Doubles
        List<List<Double>> inputList = new ArrayList<>();
        for(int i = 0; i < arrayString.size(); i++){
            List<Double> tempList = new ArrayList<>();
            for(int j = 0; j < arrayString.get(i).size() -1; j++){
                tempList.add(Double.valueOf(arrayString.get(i).get(j)));
            }
            inputList.add(tempList);
        }
        List<List<Double>> targetList = new ArrayList<>();
        for(int i = 0; i < arrayString.size(); i++){
            List<Double> tempList = new ArrayList<>();
            tempList.add(Double.valueOf(arrayString.get(i).get(arrayString.get(i).size() - 1)));
            targetList.add(tempList);
        }
        //System.out.println(targetList.size());


        for(int p = 0; p < 10; p++) {
            for (int i = 0; i < 1000000; i++) {
                for (int j = 1; j < inputList.size(); j++) {
                    Double[] inputOneDimension = inputList.get(j).toArray(new Double[inputList.get(j).size()]);
                    Double[] targetOneDimension = targetList.get(j).toArray(new Double[targetList.get(j).size()]);
                    double[] input = new double[inputOneDimension.length];
                    double[] target = new double[targetOneDimension.length];
                    for(int r = 0; r < inputOneDimension.length; r++){
                        input[r] = inputOneDimension[r];
                    }
                    for(int r = 0; r < targetOneDimension.length; r++){
                        target[r] = targetOneDimension[r];
                    }
                    //System.out.println(Arrays.toString(target));

                    //TODO: Make Network a class that will be used by other classes' main methods, for example put learning rate in the constructor and remove main method
                    network.train(input, target, .3);

                    System.out.println(Arrays.toString(input) + ",   " + Arrays.toString(target));
                    System.out.println(network.layersArray[network.layersArray.length - 1].neuronList[0].neuronOutput);

                }
                System.out.println("---------------------NEXT ITERATION (p= " + p +")(i= " + i + ")---------------------");
            }


        }


        //For Research Project
//        Network network = new Network(1, 10, 1);
        //Load the CSV
//        List<List<String>> array = new ArrayList<>();
//        array = CSVReader.readCSV("data/Data.csv");
//        for(int p = 0; p < 10; p++) {
//            for (int i = 0; i < 1000000; i++) {
//                for (int j = 1; j < array.size(); j++) {
//                    double[] input = new double[]{Double.valueOf(array.get(j).get(1)) / 1.0};
//                    double[] target = new double[]{Double.valueOf(array.get(j).get(0)) / 1000.0};
//                    //TODO: Make Network a class that will be used by other classes' main methods, for example put learning rate in the constructor and remove main method
//                    network.train(input, target, .3);
//
//                    System.out.println(Arrays.toString(input) + ",   " + Arrays.toString(target));
//                    System.out.println(network.layersArray[network.layersArray.length - 1].neuronList[0].neuronOutput);
//
//                }
//                System.out.println("---------------------NEXT ITERATION (p= " + p +")(i= " + i + ")---------------------");
//            }
//
//
//        }

    }
}
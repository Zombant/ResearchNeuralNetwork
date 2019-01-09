package network;

import java.util.Arrays;

public class Network {

    //NEURONS ARE STORED AS ARRAYS
    //first index is layer, second is neuron
    private double[][] output;
    //first index is layer, second is neuron, third is neuron from previous layer
    private double[][][] weights;
    //first index is layer, second is neuron
    private double[][] biases;

    public final int[] NETWORK_LAYER_SIZES; //how many neurons on each layer

    public final int INPUT_SIZE; //number of neurons in input layer
    public final int OUTPUT_SIZE; //number of neurons in output layer
    public final int NETWORK_SIZE; //number of layers

    public Network(int... NETWORK_LAYER_SIZES) {
        this.NETWORK_LAYER_SIZES = NETWORK_LAYER_SIZES;
        this.INPUT_SIZE = NETWORK_LAYER_SIZES[0];
        this.NETWORK_SIZE = NETWORK_LAYER_SIZES.length;
        this.OUTPUT_SIZE = NETWORK_LAYER_SIZES[NETWORK_SIZE - 1];


        ///////SET UP NEURONS///////

        //Set up using number of layers but not the size
        this.output = new double[NETWORK_SIZE][];
        this.weights = new double[NETWORK_SIZE][][];
        this.biases = new double[NETWORK_SIZE][];


        //Set the size of each layer
        //iterate through each layer
        for(int i = 0; i < NETWORK_SIZE; i++){
            this.output[i] = new double[NETWORK_LAYER_SIZES[i]];
            this.biases[i] = new double[NETWORK_LAYER_SIZES[i]];

            //Create weights array for each layer except the first
            if(i > 0){
                weights[i] = new double[NETWORK_LAYER_SIZES[i]][NETWORK_LAYER_SIZES[i-1]];

            }
        }

        ////////////////////////////
    }

    //Feed Forward method
    public double[] calculate(double... input){
        //Make sure that the input parameter matches the number of input neurons
        if(input.length != this.INPUT_SIZE){ return null; }

        //Output of first layer is the same as the input
        this.output[0] = input;

        //for every layer, starting at 1 (because first layer is just the input
        for(int layer = 1; layer < NETWORK_SIZE; layer++){
            for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++){

                //Get the sum and enter it into the activation function
                double sum = 0;
                for(int prevNeuron = 0; prevNeuron < NETWORK_LAYER_SIZES[layer - 1]; prevNeuron++){
                    sum += output[layer - 1][prevNeuron] * weights[layer][neuron][prevNeuron];
                }
                sum += biases[layer][neuron];
                output[layer][neuron] = sigmoid(sum);

            }
        }
        return output[NETWORK_SIZE - 1];

    }

    private double sigmoid(double x){
        return 1d / (1 + Math.exp(-x));
    }


    public static void main(String[] args){
        /*
        Create a network
            new Network(4,2,3,1) will have 4 neurons in first layer, etc.
         */
        Network network = new Network(4,2,3,4);
        double[] output = network.calculate(0.2, 0.9, 0.3, 0.4);
        System.out.println(Arrays.toString(output));
    }
}

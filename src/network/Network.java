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

    //TODO: Make comment
    private double[][] error_signal;
    private double[][] output_derivative;

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

        //TODO: Make comment
        this.error_signal = new double[NETWORK_SIZE][];
        this.output_derivative = new double[NETWORK_SIZE][];

        //Set the size of each layer
        //iterate through each layer
        for(int i = 0; i < NETWORK_SIZE; i++){
            this.output[i] = new double[NETWORK_LAYER_SIZES[i]];
            //TODO: Make comment
            this.error_signal[i] = new double[NETWORK_LAYER_SIZES[i]];
            this.output_derivative[i] = new double[NETWORK_LAYER_SIZES[i]];

            this.biases[i] = NetworkTools.createRandomArray(NETWORK_LAYER_SIZES[i], 0.3, 0.7);

            //Create weights array for each layer except the first
            if(i > 0){
                weights[i] = new double[NETWORK_LAYER_SIZES[i]][NETWORK_LAYER_SIZES[i-1]];
                weights[i] = NetworkTools.createRandomArray(NETWORK_LAYER_SIZES[i], NETWORK_LAYER_SIZES[i - 1], 0.3, 0.7);
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


                //Part of backpropagation
                //TODO: Make comment
                output_derivative[layer][neuron] = output[layer][neuron] * (1 - output[layer][neuron]);

            }
        }
        return output[NETWORK_SIZE - 1];

    }

    //TODO: Refactor eta into learningRate
    //TODO: Make comment
    public void  train(double[] input, double[] target, double eta){
        //Make sure input and output data are equal to amount needed
        if(input.length != INPUT_SIZE || target.length != OUTPUT_SIZE){ return; }
        calculate(input);
        backPropError(target);
        updateWeights(eta);
    }

    //Backpropagation
    public void backPropError(double [] target){

        //Loop though all output neurons
        for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[NETWORK_SIZE - 1]; neuron++){
            //See graphics folder -> error_signal_equation.png
            error_signal[NETWORK_SIZE - 1][neuron] = (output[NETWORK_SIZE - 1][neuron] - target[neuron])
                    * output_derivative[NETWORK_SIZE - 1][neuron];
        }

         //Loop through hidden layers
        for(int layer = NETWORK_SIZE - 2; layer > 0; layer--){
            //Loop through neurons of hidden layers
            for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++){
                //See second row of first equation in graphic
                double sum = 0;
                for(int nextNeuron = 0; nextNeuron < NETWORK_LAYER_SIZES[layer + 1]; nextNeuron++){
                    //Increase sum by weight that connects current neuron to next neuron
                    sum += weights[layer + 1][nextNeuron][neuron] * error_signal[layer + 1][nextNeuron];
                }
                this.error_signal[layer][neuron] = sum * output_derivative[layer][neuron];
            }
        }

    }

    //Update the weights
    public void updateWeights(double eta){
        for(int layer = 1; layer < NETWORK_SIZE; layer++){
            for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++){
                for(int prevNeuron = 0; prevNeuron < NETWORK_LAYER_SIZES[layer - 1]; prevNeuron++){
                    //Weights[layer][neuron][prevNeuron]
                    //TODO: Refactor delta to changeInWeights
                    double delta = -eta * output[layer - 1][prevNeuron] * error_signal[layer][neuron];
                    weights[layer][neuron][prevNeuron] += delta;
                }
                //Delta for biases
                double delta = -eta * 1 * error_signal[layer][neuron];
                biases[layer][neuron] += delta;
            }
        }
    }


    private double sigmoid(double x){
        return 1d / (1 + Math.exp(-x));
    }


    public static void main(String[] args){
        /*
        Create a network
            new Network(4,2,3,1) will have 4 neurons in first layer, etc.
         */
        Network network = new Network(4,1,3,4);

        double[] input = new double[]{0.1, 0.5, 0.2, 0.9};
        double[] target = new double[]{0, 1, 0, 0};

        for(int i = 0; i < 100000; i++){
            network.train(input, target, .3);
        }
        double[] output = network.calculate(input);
        System.out.println(Arrays.toString(output));
    }
}

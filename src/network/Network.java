package network;

import java.util.Arrays;

public class Network {

    //NEURONS ARE STORED AS ARRAYS
    //first index is layer, second is neuron
    private double[][] neuronOutputs;
    //first index is layer, second is neuron, third is neuron from previous layer
    private double[][][] neuronWeights;
    //first index is layer, second is neuron
    private double[][] neuronBiases;

    //TODO: Make comment
    private double[][] errorSignal;
    private double[][] outputDerivative;

    public final int[] NUMBER_OF_NEURONS_IN_LAYER; //how many neurons on each layer

    public final int INPUT_LAYER_SIZE; //number of neurons in input layer
    public final int OUTPUT_LAYER_SIZE; //number of neurons in neuronOutputs layer
    public final int NUMBER_OF_LAYERS; //number of layers

    public Network(int... NUMBER_OF_NEURONS_IN_LAYER) {
        this.NUMBER_OF_NEURONS_IN_LAYER = NUMBER_OF_NEURONS_IN_LAYER;
        this.INPUT_LAYER_SIZE = NUMBER_OF_NEURONS_IN_LAYER[0];
        this.NUMBER_OF_LAYERS = NUMBER_OF_NEURONS_IN_LAYER.length;
        this.OUTPUT_LAYER_SIZE = NUMBER_OF_NEURONS_IN_LAYER[NUMBER_OF_LAYERS - 1];


        ///////SET UP NEURONS///////

        //Set up using number of layers but not the size
        this.neuronOutputs = new double[NUMBER_OF_LAYERS][];
        this.neuronWeights = new double[NUMBER_OF_LAYERS][][];
        this.neuronBiases = new double[NUMBER_OF_LAYERS][];

        //TODO: Make comment
        this.errorSignal = new double[NUMBER_OF_LAYERS][];
        this.outputDerivative = new double[NUMBER_OF_LAYERS][];

        //Set the size of each layer
        //iterate through each layer
        for(int i = 0; i < NUMBER_OF_LAYERS; i++){
            this.neuronOutputs[i] = new double[NUMBER_OF_NEURONS_IN_LAYER[i]];
            //TODO: Make comment
            this.errorSignal[i] = new double[NUMBER_OF_NEURONS_IN_LAYER[i]];
            this.outputDerivative[i] = new double[NUMBER_OF_NEURONS_IN_LAYER[i]];

            this.neuronBiases[i] = NetworkTools.createRandomArray(NUMBER_OF_NEURONS_IN_LAYER[i], 0.3, 0.7);

            //Create neuronWeights array for each layer except the first
            if(i > 0){
                neuronWeights[i] = new double[NUMBER_OF_NEURONS_IN_LAYER[i]][NUMBER_OF_NEURONS_IN_LAYER[i-1]];
                neuronWeights[i] = NetworkTools.createRandomArray(NUMBER_OF_NEURONS_IN_LAYER[i], NUMBER_OF_NEURONS_IN_LAYER[i - 1], 0.3, 0.7);
            }
        }

        ////////////////////////////
    }

    //Feed Forward method
    public double[] feedForward(double... input){
        //Make sure that the input parameter matches the number of input neurons
        if(input.length != this.INPUT_LAYER_SIZE){ return null; }

        //Output of first layer is the same as the input
        this.neuronOutputs[0] = input;

        //for every layer, starting at 1 (because first layer is just the input
        for(int layer = 1; layer < NUMBER_OF_LAYERS; layer++){
            for(int neuron = 0; neuron < NUMBER_OF_NEURONS_IN_LAYER[layer]; neuron++){

                //Get the sum and enter it into the activation function
                double sum = 0;
                for(int prevNeuron = 0; prevNeuron < NUMBER_OF_NEURONS_IN_LAYER[layer - 1]; prevNeuron++){
                    sum += neuronOutputs[layer - 1][prevNeuron] * neuronWeights[layer][neuron][prevNeuron];
                }
                sum += neuronBiases[layer][neuron];
                neuronOutputs[layer][neuron] = sigmoid(sum);


                //Part of backpropagation
                //TODO: Make comment
                outputDerivative[layer][neuron] = neuronOutputs[layer][neuron] * (1 - neuronOutputs[layer][neuron]);

            }
        }
        return neuronOutputs[NUMBER_OF_LAYERS - 1];

    }

    //TODO: Make comment
    public void  train(double[] input, double[] target, double learningRate){
        //Make sure input and neuronOutputs data are equal to amount needed
        if(input.length != INPUT_LAYER_SIZE || target.length != OUTPUT_LAYER_SIZE){ return; }
        feedForward(input);
        backPropagation(target);
        updateWeights(learningRate);
    }

    //Backpropagation
    public void backPropagation(double [] target){

        //Loop though all output neurons
        for(int neuron = 0; neuron < NUMBER_OF_NEURONS_IN_LAYER[NUMBER_OF_LAYERS - 1]; neuron++){
            //See graphics folder -> error_signal_equation.png
            errorSignal[NUMBER_OF_LAYERS - 1][neuron] = (neuronOutputs[NUMBER_OF_LAYERS - 1][neuron] - target[neuron]) * outputDerivative[NUMBER_OF_LAYERS - 1][neuron];
        }

         //Loop through hidden layers
        for(int layer = NUMBER_OF_LAYERS - 2; layer > 0; layer--){
            //Loop through neurons of hidden layers
            for(int neuron = 0; neuron < NUMBER_OF_NEURONS_IN_LAYER[layer]; neuron++){
                //See second row of first equation in graphic
                double sum = 0;
                for(int nextNeuron = 0; nextNeuron < NUMBER_OF_NEURONS_IN_LAYER[layer + 1]; nextNeuron++){
                    //Increase sum by weight that connects current neuron to next neuron
                    sum += neuronWeights[layer + 1][nextNeuron][neuron] * errorSignal[layer + 1][nextNeuron];
                }
                this.errorSignal[layer][neuron] = sum * outputDerivative[layer][neuron];
            }
        }

    }

    //Update the neuronWeights
    public void updateWeights(double learningRate){
        for(int layer = 1; layer < NUMBER_OF_LAYERS; layer++){
            for(int neuron = 0; neuron < NUMBER_OF_NEURONS_IN_LAYER[layer]; neuron++){
                for(int prevNeuron = 0; prevNeuron < NUMBER_OF_NEURONS_IN_LAYER[layer - 1]; prevNeuron++){
                    //Weights[layer][neuron][prevNeuron]
                    double changeInWeights = -learningRate * neuronOutputs[layer - 1][prevNeuron] * errorSignal[layer][neuron];
                    neuronWeights[layer][neuron][prevNeuron] += changeInWeights;
                }
                //Delta for neuronBiases
                double changeInBiases = -learningRate * 1 * errorSignal[layer][neuron];
                neuronBiases[layer][neuron] += changeInBiases;
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
        double[] output = network.feedForward(input);
        System.out.println(Arrays.toString(output));
    }
}

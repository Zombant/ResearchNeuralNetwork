package network;

import java.util.ArrayList;
import java.util.Arrays;

public class NetworkClassTest {

    //Create List of layers
    public Layer[] layersArray;


    public NetworkClassTest(int... NUMBER_OF_NEURONS_IN_LAYER) {
        //Set the number of layers
        layersArray = new Layer[NUMBER_OF_NEURONS_IN_LAYER.length];

        //Set the size of each layer
        for(int i = 0; i < NUMBER_OF_NEURONS_IN_LAYER.length; i++){
            layersArray[i] = new Layer(NUMBER_OF_NEURONS_IN_LAYER[i]);
        }

        //iterate through each layer
        for(int i = 1; i < layersArray.length; i++){
            layersArray[i].setAttributesOfNeurons(layersArray[i - 1]);
        }

    }

    //Feed Forward method
    public double[] feedForward(double... input){
        //Make sure that the input parameter matches the number of input neurons
        if(input.length != this.layersArray[0].layerSize){ return null; }

        //Output of first layer is the same as the input
        for(int i = 0; i < layersArray[0].neuronList.length; i++){
            layersArray[0].neuronList[i].neuronOutput = input[i];
        }


        //for every layer, starting at 1 (because first layer is just the input
        for(int layer = 1; layer < layersArray.length; layer++){
            for(int neuron = 0; neuron < layersArray[layer].layerSize; neuron++){

                //Get the sum and enter it into the activation function
                double sum = 0;
                for(int prevNeuron = 0; prevNeuron < layersArray[layer - 1].layerSize; prevNeuron++){
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
        for(int i = 0; i < outputArray.length; i++){
            outputArray[i] = layersArray[layersArray.length - 1].neuronList[i].neuronOutput;
        }
        return outputArray;

    }

    //TODO: Make comment
    public void  train(double[] input, double[] target, double learningRate){
        //Make sure input and neuronOutputs data are equal to amount needed
        if(input.length != layersArray[0].layerSize || target.length != layersArray[layersArray.length - 1].layerSize){ return; }
        feedForward(input);
        backPropagation(target);
        updateWeights(learningRate);
    }

    //Backpropagation
    public void backPropagation(double [] target){

        //Loop though all output neurons
        for(int neuron = 0; neuron < layersArray[layersArray.length - 1].layerSize; neuron++){
            //See graphics folder -> error_signal_equation.png
            layersArray[layersArray.length - 1].neuronList[neuron].errorSignal = (layersArray[layersArray.length - 1].neuronList[neuron].neuronOutput - target[neuron]) * layersArray[layersArray.length - 1].neuronList[neuron].outputDerivative;
        }

         //Loop through hidden layers
        for(int layer = layersArray.length - 2; layer > 0; layer--){
            //Loop through neurons of hidden layers
            for(int neuron = 0; neuron < layersArray[layer].layerSize; neuron++){
                //See second row of first equation in graphic
                double sum = 0;
                for(int nextNeuron = 0; nextNeuron < layersArray[layer + 1].layerSize; nextNeuron++){
                    //Increase sum by weight that connects current neuron to next neuron
                    sum += layersArray[layer + 1].neuronList[nextNeuron].neuronWeights[neuron] * layersArray[layer + 1].neuronList[nextNeuron].errorSignal;
                }
                layersArray[layer].neuronList[neuron].errorSignal = sum * layersArray[layer].neuronList[neuron].outputDerivative;

            }
        }

    }

    //Update the neuronWeights
    public void updateWeights(double learningRate){
        for(int layer = 1; layer < layersArray.length; layer++){
            for(int neuron = 0; neuron < layersArray[layer].neuronList.length; neuron++){
                for(int prevNeuron = 0; prevNeuron < layersArray[layer - 1].neuronList.length; prevNeuron++){
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


//    public static void main(String[] args){
//        /*
//        Create a network
//            new Network(4,2,3,1) will have 4 neurons in first layer, etc.
//         */
//        NetworkClassTest network = new NetworkClassTest(4,1,3,4);
//        //System.out.println(Arrays.toString(network.layersArray[0].neuronList[0].neuronWeights));
//        double[] input = new double[]{0.1, 0.5, 0.2, 0.9};
//        double[] target = new double[]{0, 1, 0, 0};
//        double[] input2 = new double[]{0.6, 0.2, 0.7, 0.4};
//        double[] target2 = new double[]{0, 0, 1, 0};
//
//        for(int i = 0; i < 100000; i++){
//            network.train(input, target, .3);
//            network.train(input2, target2, .3);
//        }
//        System.out.println(Arrays.toString(network.feedForward(input)));
//        System.out.println(Arrays.toString(network.feedForward(input2)));
//    }
}

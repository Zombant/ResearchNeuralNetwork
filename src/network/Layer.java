package network;

import java.util.Random;

public class Layer {

    public int layerSize;

    public Neuron[] neuronList;

    public Layer(int layerSize) {
        this.layerSize = layerSize;
        //Initialize neuronList with empty neurons
        neuronList = new Neuron[layerSize];
        for(int i = 0; i < layerSize; i++){
            neuronList[i] = new Neuron();
        }
    }

    public void setAttributesOfNeurons(Layer previousLayer){
        for(int i = 0; i < neuronList.length; i++){
            neuronList[i].neuronOutput = 0.0;
            neuronList[i].bias = (new Random()).nextDouble();
            neuronList[i].errorSignal = 0.0;
            neuronList[i].outputDerivative = 0;
            //TODO: May have to make it i > 0
            if(i >= 0){
                neuronList[i].neuronWeights = new double[previousLayer.layerSize];
                for(int j = 0; i < neuronList[i].neuronWeights[j]; j++){
                    neuronList[i].neuronWeights[j] = (new Random()).nextDouble();
                }
            }
        }
    }


}

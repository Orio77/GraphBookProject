import os
import sys

from flask import Flask
from flask import request
from flask import jsonify

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))
from hf_llm_Handler import HF_LLMHandler
from llms.bert import BERT



app = Flask(__name__)

import json

@app.route('/similarity', methods =['POST'])
def calculate_similarity():
    data = request.json
    print("Received JSON: " + json.dumps(data))  # Print received JSON

    text1 = data['text1']
    text2 = data['text2']
    handler = HF_LLMHandler(BERT(model_name='bert-base-uncased')) # TODO change to phi3 of Ollama's and check the String response, then figure out how to extract a double out of that | Get Phi3 model with admin instruction (Ollama save)
    score = handler.get_Similarity_Score(text1, text2)

    response = {'similarity': score}
    print("Sent JSON: " + json.dumps(response))  # Print sent JSON

    return jsonify(response)

def test_handler():
    handler = HF_LLMHandler(BERT(model_name='bert-base-uncased'))

    text1 = "google-bert/bert-large-uncased - Similar to the base model but larger, which typically means it can perform better at the cost of requiring more computational resources. It's also uncased, so it treats text in a case-insensitive manner."
    text2 = "All animals are composed of cells, surrounded by a characteristic extracellular matrix composed of collagen and elastic glycoproteins.[22] During development, the animal extracellular matrix forms a relatively flexible framework upon which cells can move about and be reorganised, making the formation of complex structures possible. This may be calcified, forming structures such as shells, bones, and spicules.[23] In contrast, the cells of other multicellular organisms (primarily algae, plants, and fungi) are held in place by cell walls, and so develop by progressive growth.[24] Animal cells uniquely possess the cell junctions called tight junctions, gap junctions, and desmosomes.[25]"
    

    score = handler.get_Similarity_Score(text1, text2)

    print(score)

def getPath():
    print("Current directory:", os.getcwd)
    print("\nPython path:")
    for path in sys.path:
        print(path)

if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=5000)
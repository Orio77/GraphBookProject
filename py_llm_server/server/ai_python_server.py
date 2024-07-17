import os
import sys
import atexit

from flask import Flask
from flask import request
from flask import jsonify

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))
from hf_llm_Handler import HF_LLMHandler
from llms.mistral import Mistral

app = Flask(__name__)

llm = Mistral()

# curl test: curl -X POST -H "Content-Type: application/json" -d '{"texts": ["text1", "text2", "text3"], "label": "some_label"}' http://192.168.1.46:5000/similarity_batch
@app.route('/similarity_batch', methods=['POST'])
def calculate_similarity_batch():
    data = request.json

    try:
        texts = data['texts']
        label = data['label']
    except Exception as e:
        return jsonify({"error": f"Missing key {e}"}), 400

    handler = HF_LLMHandler(llm)
    scores = handler.get_similarity_Scores(texts=texts, label=label) # type used: results[int(i)].append({"el1": int(j),"el2": float(score)})

    response = {'similarity_batch': scores}

    return jsonify(response)

# TODO @app.route('continue_calculations', methods=['?'])
# Curl test: curl -X POST -H "Content-Type: application/json" -d '{"texts": ["The cat sat on the mat.", "The quick brown fox jumps over the lazy dog.", "Hello, world!"], "concept": "animal", "label": "test_label"}' http://localhost:5000/concept
@app.route('/concept', methods=['POST'])
def calculate_Concept_Scores():
    data = request.json

    try:
        texts = data['texts']
        concept = data['concept']
        label = data['label']
    except Exception as e:
        return jsonify({"error": f"Missing key {e}"}), 400
    
    handler = HF_LLMHandler(llm)
    scores = handler.get_Concept_Scores(texts=texts, concept=concept, label=label)
    response = {'concept': scores}

    return jsonify(response)

def closing_Message():
    print("Server is shutting down...")

atexit.register(closing_Message)

if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=5000)
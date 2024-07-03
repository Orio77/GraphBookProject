import os
import sys
import atexit

from flask import Flask
from flask import request
from flask import jsonify
import json

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))
from hf_llm_Handler import HF_LLMHandler
from llms.mistral import Mistral



app = Flask(__name__)

@app.route('/similarity', methods =['POST'])
def calculate_similarity():
    data = request.json

    try:
        text1 = data['text1']
        text2 = data['text2']
    except Exception as e:
        return jsonify({"error": f"Missing key: {e}"}), 400

    handler = HF_LLMHandler(Mistral())
    score = handler.get_Similarity_Score(text1, text2)

    response = {'similarity': score}
    print("Sent JSON: " + json.dumps(response))  # Print sent JSON

    return jsonify(response)

# curl test: curl -X POST -H "Content-Type: application/json" -d '{"texts": ["text1", "text2", "text3"], "label": "some_label"}' http://192.168.1.46:5000/similarity_batch
@app.route('/similarity_batch', methods=['POST'])
def calculate_similarity_batch():
    data = request.json

    try:
        texts = data['texts']
        label = data['label']
    except Exception as e:
        return jsonify({"error": f"Missing key {e}"}), 400
    
    handler = HF_LLMHandler(Mistral())
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
    
    handler = HF_LLMHandler(Mistral())
    scores = handler.get_Concept_Scores(texts=texts, concept=concept, label=label)
    response = {'concept': scores}

    return jsonify(response)

def closing_Message():
    print("Server is shutting down...")

atexit.register(closing_Message)

def test_similarity_scores_mistral():
    texts = [
    "Machine learning is a method of data analysis that automates analytical model building. It is a branch of artificial intelligence based on the idea that systems can learn from data, identify patterns and make decisions with minimal human intervention.",
    "Because of new computing technologies, machine learning today is not like machine learning of the past. It was born from pattern recognition and the theory that computers can learn without being programmed to perform specific tasks.",
    "Researchers interested in artificial intelligence wanted to see if computers could learn from data. The iterative aspect of machine learning is important because as models are exposed to new data, they are able to independently adapt.",
    "They learn from previous computations to produce reliable, repeatable decisions and results. It’s a science that’s not new – but one that has gained fresh momentum.",
    "While many machine learning algorithms have been around for a long time, the ability to automatically apply complex mathematical calculations to big data – over and over, faster and faster – is a recent development.",
    "Machine learning algorithms are often categorized as supervised or unsupervised. Supervised algorithms require a data scientist or data analyst with machine learning skills to provide both input and desired output, in addition to furnishing feedback about the accuracy of predictions during training.",
    "Unsupervised algorithms, on the other hand, use an approach called deep learning to review data and arrive at conclusions. Unsupervised learning algorithms are used when the information used to train is neither classified nor labeled."
    ]
    label = 'tst'
    mist = Mistral()
    HF_LLMHandler(mist).get_similarity_Scores(texts=texts, label=label)
    mist.print_saved_results()


if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=5000)
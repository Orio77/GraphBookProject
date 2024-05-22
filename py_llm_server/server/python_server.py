import os
import sys
import re
import traceback
import atexit
from dotenv import load_dotenv
load_dotenv("C:/Users/macie/iCloudDrive/MyProjects/graph-book-core/py_llm_server/environment/.env")

LOG_DIR = os.getenv('ERROR_LOG_DIR')

from flask import Flask
from flask import request
from flask import jsonify
import json

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))
from hf_llm_Handler import HF_LLMHandler
from llms.bert import BERT
from llms.phi import Phi
from llms.mistral import Mistral



app = Flask(__name__)

@app.route('/similarity', methods =['POST'])
def calculate_similarity():
    data = request.json

    try:
        text1 = data['text1']
        text2 = data['text2']
    except Exception as e:
        logReceivedData
        logErrorCause(exception=e)
        return jsonify({"error": f"Missing key: {e}"}), 400

    handler = HF_LLMHandler(Mistral())
    score = handler.get_Similarity_Score(text1, text2)

    response = {'similarity': score}
    print("Sent JSON: " + json.dumps(response))  # Print sent JSON

    return jsonify(response)

@app.route('/similarity_batch', methods=['POST'])
def calculate_similarity_batch():
    data = request.json

    try:
        texts = data['texts']
    except Exception as e:
        return jsonify({"error": f"Missing key {e}"}), 400
    
    handler = HF_LLMHandler(Mistral())
    scores = handler.get_similarity_Scores(texts=texts) # Map.of(Integer=page_number, int[]{Integer=other_page_number, Double=score})

    response = {'similarity_batch': scores}

    return jsonify(response)

# Transparent and Debug Version
@app.route('/debug_similarity', methods =['POST'])
def _debug_calculate_similarity():
    print("_debug_calculate_similarity called")
    try:
        print("Trying to parse JSON data")
        print("Raw data: ", request.data)  # Print raw data
        data = request.json
        print("JSON data parsed successfully")

        print("Logging received data")
        # logReceivedData(data=request.data, headers=request.headers, received_json=json.dumps(request.json))
        print("Received data logged successfully")

        print("Trying to extract text1 and text2 from data")
        text1 = data['text1']
        text2 = data['text2']
        print("Extracted text1 and text2 successfully")
    except KeyError as e:
        print("Caught KeyError")
        logErrorCause(exception=e)
        return jsonify({"error": f"Missing key: {e}"}), 400
    except Exception as e:
        print("caught an exception")
        print("Exception traceback: " + traceback.format_exc())
        return jsonify({"error": "An error occured while processing the request"}), 400

    print("Creating handler and calculating similarity score")
    handler = HF_LLMHandler(Phi())
    score = handler.get_Similarity_Score(text1, text2)
    print("Calculated similarity score successfully")

    response = {'similarity': score}
    print("Sent JSON: " + json.dumps(response))  # Print sent JSON

    return jsonify(response)

def logErrorCause(exception):
    with open(getPreciseLogPath(), 'a') as f:
        f.write(f"Missing key in received JSON: {exception}\n")

def logReceivedData(data, headers, received_json):
    with open(getPreciseLogPath(), 'a') as f:
        f.write("PYTHON:\n" +
                "Received data: " + str(data) + "\n" +
                "Received headers: " + str(headers) + "\n" +
                "Received json: " + str(received_json) + "\n")

def getPreciseLogPath():
    folders = os.listdir(LOG_DIR)

    def get_number(folder_name):
        match = re.search(r'\d+$', folder_name)
        return int(match.group()) if match else 0
    
    folders.sort(key=get_number)

    LOG_PATH = os.path.join(LOG_DIR, folders[-1])
    print(folders[-1])
    return LOG_PATH

def getPath():
    print("Current directory:", os.getcwd)
    print("\nPython path:")
    for path in sys.path:
        print(path)


def closing_Message():
    print("Server is shutting down...")

atexit.register(closing_Message)


if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=5000)
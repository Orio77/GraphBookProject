import os
import sys
import re
import traceback
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

# Transparent and Debug Version
# @app.route('/similarity', methods =['POST'])
# def calculate_similarity():
#     print("calculate similarity called")
#     try:
#         print("Trying to parse JSON data")
#         print("Raw data: ", request.data)  # Print raw data
#         data = request.json
#         print("JSON data parsed successfully")

#         print("Logging received data")
#         logReceivedData(data=request.data, headers=request.headers, received_json=json.dumps(request.json))
#         print("Received data logged successfully")

#         print("Trying to extract text1 and text2 from data")
#         text1 = data['text1']
#         text2 = data['text2']
#         print("Extracted text1 and text2 successfully")
#     except KeyError as e:
#         print("Caught KeyError")
#         logErrorCause(exception=e)
#         return jsonify({"error": f"Missing key: {e}"}), 400
#     except Exception as e:
#         print("caught an exception")
#         print("Exception traceback: " + traceback.format_exc())
#         return jsonify({"error": "An error occured while processing the request"}), 400

#     print("Creating handler and calculating similarity score")
#     handler = HF_LLMHandler(Phi())
#     score = handler.get_Similarity_Score(text1, text2)
#     print("Calculated similarity score successfully")

#     response = {'similarity': score}
#     print("Sent JSON: " + json.dumps(response))  # Print sent JSON

#     return jsonify(response)

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


def test_handler():
    handler = HF_LLMHandler(Phi())

    text1 = "google-bert/bert-large-uncased - Similar to the base model but larger, which typically means it can perform better at the cost of requiring more computational resources. It's also uncased, so it treats text in a case-insensitive manner."
    text2 = "All animals are composed of cells, surrounded by a characteristic extracellular matrix composed of collagen and elastic glycoproteins.[22] During development, the animal extracellular matrix forms a relatively flexible framework upon which cells can move about and be reorganised, making the formation of complex structures possible. This may be calcified, forming structures such as shells, bones, and spicules.[23] In contrast, the cells of other multicellular organisms (primarily algae, plants, and fungi) are held in place by cell walls, and so develop by progressive growth.[24] Animal cells uniquely possess the cell junctions called tight junctions, gap junctions, and desmosomes.[25]"
    text3 = """Butterflies are winged insects from the lepidopteran suborder Rhopalocera, characterized by large, often brightly coloured wings that often fold together when at rest, and a conspicuous, fluttering flight. The group comprises the superfamilies Hedyloidea (moth-butterflies in the Americas) and Papilionoidea (all others). The oldest butterfly fossils have been dated to the Paleocene, about 56 million years ago, though they likely originated in the Late Cretaceous, about 101 million years ago.[1]
    Butterflies have a four-stage life cycle, and like other holometabolous insects they undergo complete metamorphosis. Winged adults lay eggs on the food plant on which their larvae, known as caterpillars, will feed. The caterpillars grow, sometimes very rapidly, and when fully developed, pupate in a chrysalis. When metamorphosis is complete, the pupal skin splits, the adult insect climbs out, expands its wings to dry, and flies off.
    Some butterflies, especially in the tropics, have several generations in a year, while others have a single generation, and a few in cold locations may take several years to pass through their entire life cycle."""
    text4 = "The earliest Lepidoptera fossils date to the Triassic-Jurassic boundary, around 200 million years ago.[6] Butterflies evolved from moths, so while the butterflies are monophyletic (forming a single clade), the moths are not. The oldest known butterfly is Protocoeliades kristenseni from the Palaeocene aged Fur Formation of Denmark, approximately 55 million years old, which belongs to the family Hesperiidae (skippers).[7] Molecular clock estimates suggest that butterflies originated sometime in the Late Cretaceous, but only significantly diversified during the Cenozoic,[8][1] with one study suggesting a North American origin for the group.[1] The oldest American butterfly is the Late Eocene Prodryas persephone from the Florissant Fossil Beds,[9][10] approximately 34 million years old.[11]"

    score = handler.get_Similarity_Score(text3, text4)

    print(score)


if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=5000)
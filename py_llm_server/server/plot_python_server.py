from flask import Flask, request, jsonify
import re
from matplotlib import pyplot as plt
import numpy as np

app = Flask(__name__)

@app.route('/generatePlot', methods=['POST'])
def create_3d_plot():
    data = request.json
    concepts = list(data.keys())
    
    # Extract page IDs as strings
    page_ids = sorted({pair['el1'] for pairs in data.values() for pair in pairs})
    num_pages = len(page_ids)
    
    # Create a dictionary to map page IDs to indices
    page_id_to_index = {page_id: i for i, page_id in enumerate(page_ids)}
    
    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')
    
    for concept_index, concept in enumerate(concepts):
        pairs = data[concept]
        x = []
        y = []
        z = []
        for pair in pairs:
            page_id = pair['el1']
            print(page_id)
            score = pair['el2']
            # Check if page_id exists in the dictionary before using it
            if page_id in page_id_to_index:
                x.append(page_id_to_index[page_id])
                y.append(concept_index)
                z.append(score)
            else:
                # Handle missing page_id as needed, e.g., log a warning or skip
                print(f"Warning: page_id '{page_id}' not found in page_id_to_index dictionary.")
        
        if x:  # Check if x is not empty
            ax.scatter(np.array(x), np.array(y), zs=np.array(z), zdir='z', label=f'Concept {concept}')
    
    ax.set_xlabel('Pages')
    ax.set_ylabel('Graph Index')
    ax.set_zlabel('Scores')
    
    ax.set_xticks(range(num_pages))
    ax.set_xticklabels(page_ids, rotation=90)
    
    plt.legend()
    plt.show()

    return jsonify({"status": "Plot generated successfully"})

if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=5001)
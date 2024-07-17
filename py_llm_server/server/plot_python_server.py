from flask import Flask, request, jsonify
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
    
    fig = plt.figure(figsize=(14, 10))
    fig.set_dpi(100)
    plt.style.use('ggplot')
    
    ax = fig.add_subplot(111, projection='3d')
    
    # Define a colormap
    cmap = plt.get_cmap('viridis')
    
    for concept_index, concept in enumerate(concepts):
        pairs = data[concept]
        x = []
        y = []
        z = []
        for pair in pairs:
            page_id = pair['el1']
            score = pair['el2']
            # Check if page_id exists in the dictionary before using it
            if page_id in page_id_to_index:
                x.append(page_id_to_index[page_id])
                y.append(concept_index)
                z.append(score)
            else:
                print(f"Warning: page_id '{page_id}' not found in page_id_to_index dictionary.")
        
        if x:  # Check if x is not empty
            ax.scatter(
                np.array(x), np.array(y), zs=np.array(z), zdir='z', 
                label=f'Concept {concept}', 
                color=cmap(concept_index % 10), 
                alpha=0.7, edgecolors='w', s=100
            )
    
    ax.set_xlabel('Pages', fontsize=14, labelpad=15)
    ax.set_ylabel('Concept Index', fontsize=14, labelpad=15)
    ax.set_zlabel('Scores', fontsize=14, labelpad=15)
    
    ax.set_xticks(range(num_pages))
    ax.set_xticklabels(page_ids, rotation=90, fontsize=10)
    ax.yaxis.set_major_formatter(plt.FuncFormatter(lambda val, loc: concepts[int(val)] if int(val) < len(concepts) else ''))

    plt.legend(loc='upper left', bbox_to_anchor=(1.05, 1), fontsize=12)
    plt.tight_layout()
    plt.show()

    return jsonify({"status": "Plot generated successfully"})

if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=5001)

from pprint import pprint
from interfaces.llm import LLM
import ollama

import re
import os
import shutil
import json

class Mistral(LLM):  
    def __init__(self):
        with open('C:/Users/macie/Desktop/GBP/graph-book-core/src/main/resources/config.json') as f:
            data = json.load(f)
        self.save_path = data['GraphBookProject']['Scores']
        if self.save_path is None:
            raise ValueError("save_path is both null. Please provide a valid path")

        if not os.path.exists(self.save_path):
            os.makedirs(self.save_path)
        self.output_dir = None
        self.texts_path = os.path.join(self.save_path, 'texts.json')
        self.label_path = os.path.join(self.save_path, 'label.json')
        self.results_path = os.path.join(self.save_path, 'results.json').replace('\\', '/')
    
    def calculate_similarity_batch(self, texts, label):
        # Serialize texts and label for checkpoint
        with open(self.texts_path, 'w') as f:
            json.dump(texts, f)
        with open(self.label_path, 'w') as f:
            json.dump(label, f)

        # Load existing results if available
        if os.path.exists(self.results_path):
            with open(self.results_path, 'r') as f:
                results = json.load(f)
        else:
            results = {}
            with open(self.results_path, 'w') as f:
                json.dump(label, f)

        length = len(texts)
        start_index = len(results)  # Determine where to resume
        self.output_dir = os.path.join(self.save_path, label)
        # Ensure the output directory exists
        if not os.path.exists(self.output_dir):
            os.makedirs(self.output_dir)
        
        for i in range(start_index, length):
            for j in range(i+1, length):
                # Get the Similarity Score from AI
                response = ollama.chat(model='mistral', messages=[
                    {
                    'role': 'system',
                    'content': 'Given two texts, return their similarity score as double in range (0.0-100.0). End your message with: "Score: YOUR_SIMILARITY_SCORE"'
                },
                {
                    'role': 'system',
                    'content': 'Make sure to include "Score:" in your answer.'
                },
                {
                    'role': 'system',
                    'content': 'Make sure to respond with score as a double.'
                },
                {
                    'role': 'user',
                    'content': f"""
                    TEXT1:
                    {texts[i]}
                    
                    TEXT2:
                    {texts[j]}

                    YOUR SIMILARITY SCORE (0-100):
                    """
                },
                {
                    'role': 'assistant',
                    'content': 'Score: '
                },
                ],
                options={'temperature': 0.7, 'num_predict': 5}, keep_alive=-1)
                ai_response = response['message']['content']
                # Extract score out of the response
                score_match = re.match(r'^(\d+\.\d+)', ai_response)
                if score_match:
                    score = float(score_match.group())
                else:
                    score = -1

                # Store results in dictionary
                if i not in results:
                    results[i] = []
                results[int(i)].append({"el1": int(j),"el2": float(score)})

            # Save periodically after every page iteration
            with open(self.results_path, 'w') as f:
                        json.dump(results, f)
                    

        # Final save before moving the file
        with open(self.results_path, 'w') as f:
            json.dump(results, f)

        if not os.path.exists(self.output_dir):
            os.makedirs(self.output_dir)

        # Move the final file to the dedicated directory
        shutil.move(self.results_path, os.path.join(self.output_dir, os.path.basename(self.results_path)))

        os.remove(self.texts_path)
        os.remove(self.label_path)

        return results  

    def continue_similarity_scores_calculation(self):
        if not os.path.exists(self.texts_path) or not os.path.exists(self.label_path):
            raise FileNotFoundError("Checkpoint files not found.")
        
        with open(self.texts_path, 'rb') as f:
            texts = json.load(f)
        with open(self.label_path, 'rb') as f:
            label = json.load(f)

        # TODO What if there are no such files (throw an exception)

        return self.calculate_similarity_batch(texts=texts, label=label)

    def print_saved_results(self):
        path = os.path.join("C:\\Users\\macie\\GraphBookDirTest\\scores", 'results.json')
        if os.path.exists(path=path):
                with open(path, 'rb') as f:
                    results = json.load(f)
                    pprint(results)
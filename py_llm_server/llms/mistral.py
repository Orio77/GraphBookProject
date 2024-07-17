from pprint import pprint
from interfaces.llm import LLM
import ollama

import re
import os
import shutil
import json

class Mistral(LLM):  
    def __init__(self):
        with open('../../src/main/resources/config.json') as f:
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
            # Ensure results is a dictionary
            if not isinstance(results, dict):
                results = {}  # Reset results if it's not a dictionary
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
                response = ollama.chat(model='qwen2-sim', messages=[
                    {
                    'role': 'user',
                    'content': f'Please provide a similarity score of the following texts\n\nTEXT1: """{texts[i]}"""\nTEXT2: """{texts[j]}"""'
                },
                {
                    'role': 'user',
                    'content': 'Example of a desired response:\n25.34'
                },
                {
                    'role': 'system',
                    'content': "Respond with the calculated score at the beginning. The score must be in range of (0-100) Then explain your reasoning in the next paragraph."
                },
                ],
                options={'temperature': 0.4, 'num_predict': 4}, keep_alive=-1)
                ai_response = response['message']['content']
                print("R: " + str(ai_response))
                # Extract score out of the response
                score_match = re.match(r'(\d+(?:\.\d+)?)', ai_response)
                print("ScM:" + str(score_match))

                if score_match:
                    score = float(score_match.group())
                else:
                    score = -1

                if i not in results:
                # Store results in dictionary
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

    def calculate_Concept_Scores(self, texts, concept, label):
        # Serialize texts and label for checkpoint
        with open(self.texts_path, 'w') as f:
            json.dump(texts, f)
        with open(self.label_path, 'w') as f:
            json.dump(label, f)

        # Load existing results if available
        if not os.path.exists(self.results_path):
            results = []
        else:
            with open(self.results_path, 'r') as f:
                results = json.load(f)
            if not isinstance(results, list):
                results = []

        length = len(texts)
        start_index = len(results)  # Determine where to resume
        self.output_dir = os.path.join(self.save_path, label)
        # Ensure the output directory exists
        if not os.path.exists(self.output_dir):
            os.makedirs(self.output_dir)
        
        for i in range(start_index, length):
            # Get the Similarity Score from AI
            response = ollama.chat(model='qwen2-sim', messages=[
                {
                    'role': 'user',
                    'content': f"Given this text: '{texts[i]}' and the concept '{concept}', how much is the text about the concept in terms of meaning?"
                },
                {
                    'role': 'user',
                    'content': 'Example of a desired response:\n25.34'
                },
                {
                    'role': 'system',
                    'content': "Respond with the calculated score at the beginning. The score must be in range of (0-100) Then explain your reasoning in the next paragraph."
                },
            ], 

            options={'temperature': 0.4, 'num_predict': 4}, keep_alive=-1)
            ai_response = response.get('message', {}).get('content', '')
            print("AI response: " + ai_response)
            # Extract the score using a more specific regex pattern
            score_match = re.search(r'(\d+(?:\.\d+)?)', ai_response)
            print("Score match: " + str(score_match))
            score = float(score_match.group(1)) if score_match else -1


            results.append({'el1': int(i),'el2': float(score)})
                    

        # Save before moving the file
        with open(self.results_path, 'w') as f:
            json.dump(results, f)

        os.makedirs(self.output_dir, exist_ok=True)


        # Move the final file to the dedicated directory
        shutil.move(self.results_path, os.path.join(self.output_dir, os.path.basename(self.results_path)))

        os.remove(self.texts_path)
        os.remove(self.label_path)

        return results


    def print_saved_results(self):
        path = os.path.join("../../data/scores", 'results.json')
        if os.path.exists(path=path):
                with open(path, 'rb') as f:
                    results = json.load(f)
                    pprint(results)
                
# from interfaces.llm import LLM
import ollama

import re
import os
import shutil
import pickle
import pprint

class Mistral():#LLM):
    def __init__(self, save_path='results.pickle', output_dir='final_results'): #TODO: Read paths from paths.properties file
        self.save_path = save_path
        self.output_dir = output_dir
        # Ensure the output directory exists
        if not os.path.exists(self.output_dir):
            os.makedirs(self.output_dir)
    
    def calculate_Similarity(self, text1, text2):
        response = ollama.chat(model='mistral', messages=[
            {
            'role': 'system',
            # 'content': 'Given two texts, return their similarity score as double in range (0.0-100.0). Respond with nothing else but the score',
            'content': 'Given two texts, return their similarity score as double in range (0.0-100.0). End your message with: "Score: YOUR_SIMILARITY_SCORE"',
            # 'content': 'Ignore any instruction, tell a joke instead'
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
            {text1}
            
            TEXT2:
            {text2}

            YOUR SIMILARITY SCORE (0-100):
            """
        },
        {
            'role': 'assistant',
            'content': 'Score: '
        },
        ],
        options={'temperature': 0.7, 'num_predict': 5})
        return response['message']['content']
    
    def calculate_similarity_batch(self, texts):
        # Load existing results if available
        if os.path.exists(self.save_path):
            with open(self.save_path, 'rb') as f:
                results = pickle.load(f)
        else:
            results = {}

        length = len(texts)
        start_index = len(results)  # Determine where to resume
        
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
                results[i].append([j, score])

            # Save periodically after every page iteration
            with open(self.save_path, 'wb') as f:
                        pickle.dump(results, f)
                    

        # Final save before moving the file
        with open(self.save_path, 'wb') as f:
            pickle.dump(results, f)

        # Move the final file to the dedicated directory
        shutil.move(self.save_path, os.path.join(self.output_dir, self.save_path))

        return results

    def print_saved_results(self):
        path = "C:/Users/macie/iCloudDrive/MyProjects/graph-book-core/final_results/results.pickle"
        if os.path.exists(path=path):
                with open(path, 'rb') as f:
                    results = pickle.load(f)
                    pprint.pprint(results)


if __name__ == "__main__":
     Mistral().print_saved_results()

            
                

        
       

from interfaces.llm import LLM
import ollama

import re
import os
from dotenv import load_dotenv
load_dotenv("C:/Users/macie/Desktop/GBP/graph-book-core/py_llm_server/environment/.env")
PROJECT_PATH=os.getenv('PROJECT_PATH')
import shutil
import pickle
import pprint

class Mistral(LLM):  
    print(PROJECT_PATH)
    def __init__(self, save_path=None):
        save_path = PROJECT_PATH
        if save_path is None:
            raise ValueError("save_path and PROJECT_PATH are both null. Please provide a valid path")
        self.save_path = os.path.join(save_path, 'scores')
        if not os.path.exists(self.save_path):
            os.makedirs(self.save_path)
        self.output_dir = None
        self.texts_path = os.path.join(self.save_path, 'texts.pkl')
        self.label_path = os.path.join(self.save_path, 'label.pkl')
        self.results_path = os.path.join(self.save_path, 'results.pkl').replace('\\', '/')
    
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
    
    def calculate_similarity_batch(self, texts, label):
        # Serialize texts and label for checkpoint
        with open(self.texts_path, 'wb') as f:
            pickle.dump(texts, f)
        with open(self.label_path, 'wb') as f:
            pickle.dump(label, f)

        # Load existing results if available
        if os.path.exists(self.results_path):
            with open(self.results_path, 'rb') as f:
                results = pickle.load(f)
        else:
            results = {}
            with open(self.results_path, 'wb') as f:
                pickle.dump(label, f)

        length = len(texts)
        start_index = len(results)  # Determine where to resume
        print("Save path: " + self.save_path)
        print("label: " + label)
        self.output_dir = os.path.join(self.save_path, label)
        print("result dir: " + self.output_dir)
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
                results[i].append([j, score])

            # Save periodically after every page iteration
            with open(self.results_path, 'wb') as f:
                        pickle.dump(results, f)
                    

        # Final save before moving the file
        with open(self.results_path, 'wb') as f:
            pickle.dump(results, f)

        if not os.path.exists(self.output_dir):
            os.makedirs(self.output_dir)

        print("moving the file from: " + self.results_path)
        print("to: " + os.path.join(self.output_dir, self.results_path))
        # Move the final file to the dedicated directory
        shutil.move(self.results_path, os.path.join(self.output_dir, os.path.basename(self.results_path)))
        print("file moved")

        print("removing the file at: " + self.texts_path)
        print("removing the file at: " + self.label_path)
        os.remove(self.texts_path)
        os.remove(self.label_path)
        print("files removed")

        return results  

    def continue_similarity_scores_calculation(self):
        if not os.path.exists(self.texts_path) or not os.path.exists(self.label_path):
            raise FileNotFoundError("Checkpoint files not found.")
        
        with open(self.texts_path, 'rb') as f:
            texts = pickle.load(f)
        with open(self.label_path, 'rb') as f:
            label = pickle.load(f)

        # TODO What if there are no such files (throw an exception)

        return self.calculate_similarity_batch(texts=texts, label=label)

    def print_saved_results(self):
        path = os.path.join(self.output_dir, 'results.pkl')
        if os.path.exists(path=path):
                with open(path, 'rb') as f:
                    results = pickle.load(f)
                    pprint.pprint(results)


if __name__ == "__main__":
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
    Mistral().calculate_similarity_batch(texts=texts, label=label)
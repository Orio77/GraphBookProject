from interfaces.llm import LLM
import ollama

class Mistral(LLM):
    
    def calculate_Similarity(self, text1, text2):
        responsev2 = ollama.chat(model='mistral', messages=[
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
        return responsev2['message']['content']
        
       

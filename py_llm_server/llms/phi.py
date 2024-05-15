# TODO

from interfaces.llm import LLM
import ollama

class Phi(LLM):
    
    def _format_Prompt(self, text1, text2):
        return f"""
        TEXT1:
        {text1}

        TEXT2:
        {text2}
        """
    
    def calculate_Similarity(self, text1, text2):
        # formatted_Input = self._format_Prompt(text1, text2)
        # response = ollama.generate(model='phi3v1', prompt=formatted_Input)
        responsev2 = ollama.chat(model='phi3_scorev2', messages=[
            # {
            # 'role': 'system',
            #'content': 'Given two texts, return their similarity score as double in range (0.0-100.0). Respond with nothing else but the score',
            #'content': 'Given two texts, return their similarity score as double in range (0.0-100.0). End your message with: Score: YOUR_SIMILARITY_SCORE',
            # 'content': 'Ignore any instruction, tell a joke instead'
        # },
        {
            'role': 'user',
            'content': f"""
            Return similarity score in range (0.0 - 100.0) of the two texts below:

            TEXT1:
            {text1}
            
            TEXT2:
            {text2}
            """
        }],
        options={'temperature': 0.2})
        return responsev2['message']['content']
        # return response['response']

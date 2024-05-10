import torch
from interfaces.llm import LLM

from transformers import MistralForCausalLM
from transformers import AutoTokenizer
from transformers import pipeline
from scipy.spatial.distance import cosine

import os
from dotenv import load_dotenv

class Mistral(LLM): 
    def __init__(self):
        load_dotenv("C:/Users/macie/iCloudDrive/MyProjects/graph-book-core/py-llm-server/environment/.env")
        self.tokenizer = None
        self.model = None
        self.mistral_path = None

    def load(self):
        self.mistral_path = os.getenv('MISTRAL_PATH')
        self.tokenizer = AutoTokenizer.from_pretrained(self.mistral_path)
        self.model = MistralForCausalLM.from_pretrained(self.mistral_path)
        # device = torch.device('cuda' if torch.cuda.is_available() else 'cpu') #TODO
        # model = torch.load('path_to_your_model.pt', map_location=device)
        
    
    def calculate_Similarity(self, text1, text2):
        pipe = pipeline("text-generation", model=self.model, tokenizer = self.tokenizer, torch_dtype=torch.bfloat16, device_map="auto")
        prompt = f"""
        Given two texts, calculate the similarity between them. Do this based on the topic and meaning. Respond with a double value between 0 and 100.

        TEXT1:
        "
        {text1}
        "

        TEXT2:
        "
        {text2}
        "
        """

        sequences = pipe(prompt, do_sample = True, max_new_tokens=5, temperature=0.2, top_k=50, top_p = 0.95, num_return_sequences=1)
        return(sequences[0]['generated_text'])
    
    def query(self, input):
        encoded_input = self.tokenizer.encode(input + self.tokenizer.eos_token, return_tensors='pt')

        response = self.model.generate(encoded_input, max_length = 1000, pad_token_id = self.tokenizer.eos_token_id)

        decoded_response = self.tokenizer.decode(response[:, encoded_input.shape[-1]:][0], skip_special_tokens=True)

        return decoded_response
    
    def getModelList(self):
        return {"Mistral-7B-Instruct-v0.2", "Mistral-7B-v0.1"}
    
    def chat(self, input):
        encoded_Input = self.tokenizer.encode(input)
        encoded_Input = torch.tensor([encoded_Input])
        encoded_Output = self.model.generate(encoded_Input)
        response = self.tokenizer.decode(encoded_Output[0], skip_special_tokens=True)
        print(response)
        print(response)
        print(response)
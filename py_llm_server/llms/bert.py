from interfaces.llm import LLM

from transformers import BertTokenizer
from transformers import BertModel
from scipy.spatial.distance import cosine

class BERT(LLM):

    def __init__(self, model_name):
        self.tokenizer = BertTokenizer.from_pretrained(model_name)
        self.model = BertModel.from_pretrained(model_name)

    def _get_embedding(self, text):
        inputs = self.tokenizer(text, return_tensors = 'pt')
        outputs = self.model(**inputs)
        return outputs.last_hidden_state.mean(dim=1).squeeze().detach().numpy()
    
    def calculate_Similarity(self, text1, text2):
        embedding1 = self._get_embedding(text1)
        embedding2 = self._get_embedding(text2)
        return (1 - cosine(embedding1, embedding2)) * 100
    
    def query(self, input):
        return "BERT model cannot be queried, since it's an embedding model"
    
    def getModelList(self):
        return {"bert-base-uncased", "bert-large-uncased", "bert-base-multilingual-uncased", "bert-large-uncased-whole-word-masking"}

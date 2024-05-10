from interfaces.ILLMHandler import ILLMHandler
from interfaces.llm import LLM

class HF_LLMHandler(ILLMHandler):

    def __init__(self, llm: LLM):
        self.llm = llm

    def query(self, input):
        return self.llm.query(input)

    def get_Similarity_Score(self, text1, text2):
        return self.llm.calculate_Similarity(text1, text2)
    
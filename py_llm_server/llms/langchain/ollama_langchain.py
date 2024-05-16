from langchain_community.llms import Ollama
from langchain_community.chat_models import ChatOllama

ChatOllama(model='phi3', temperature=0.2)

llm = Ollama(model="llama3")

llm.invoke("Tell me a joke")
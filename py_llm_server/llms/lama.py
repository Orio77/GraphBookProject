import ollama

class OllamaTests:

    def test_Simple_Chat(self, input):
        response = ollama.generate(model='phi3', prompt=input)
        print(response['response'])
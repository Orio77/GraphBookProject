# Import necessary modules
import threading
import time
import ollama

# Define the queries
queries = [
    [{'role': 'user', 'content': 'Why is quantum mechanics considered hard?'}, {'role': 'assistant', 'content': 'Answer: '}],
    [{'role': 'user', 'content': 'How did Nikola Tesla\'s brother die? How did Nikola react to that?'}, {'role': 'assistant', 'content': 'Answer: '}],
    [{'role': 'user', 'content': 'What is the theory of relativity?'}, {'role': 'assistant', 'content': 'Answer: '}],
    [{'role': 'user', 'content': 'What are black holes?'}, {'role': 'assistant', 'content': 'Answer: '}],
    [{'role': 'user', 'content': 'How does quantum computing work?'}, {'role': 'assistant', 'content': 'Answer: '}],
]

# Define the function to send messages
def send_message(query):
    response = ollama.chat(model='mistral', messages=query, options={})
    print(response['message']['content'])

# Threaded execution
start_time_threaded = time.time()
threads = [threading.Thread(target=send_message, args=(query,)) for query in queries]
for thread in threads:
    thread.start()
for thread in threads:
    thread.join()
end_time_threaded = time.time()
print(f"Threaded execution time: {end_time_threaded - start_time_threaded} seconds")

# Sequential execution
start_time_sequential = time.time()
for query in queries:
    send_message(query)
end_time_sequential = time.time()
print(f"Sequential execution time: {end_time_sequential - start_time_sequential} seconds")
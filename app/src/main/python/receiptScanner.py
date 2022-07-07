# Make sure to first install the SDK using 'pip install butler-sdk'
from butler import Client
from os.path import dirname, join

# Specify variables for use in script below
api_key = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhdXRoMHw2MmM1ZjBmZjdhODVkNDYyNDllNmQwYzMiLCJlbWFpbCI6InN0ZXBoYW5pZWd1YW5AZmIuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImlhdCI6MTY1NzEzOTUxNzI1NX0.e35FL4w2rS4r_MSKdhV6jj1PUwO1hjjGnB1wsJLDvgY'
queue_id = '8d7f8be8-017c-4b0a-9b0c-e78d9148d378'

# Specify the path to the file you would like to process
file_location = join(dirname(__file__), "example.jpg")

response = Client(api_key).extract_document(file_location, queue_id).form_fields

# receipt parameters
merchant = response[0].value
date = response[3].value
total = response[8].value

def main():
    return total
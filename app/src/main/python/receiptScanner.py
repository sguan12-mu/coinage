from butler import Client
from os.path import dirname, join

api_key = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhdXRoMHw2MmM1ZjBmZjdhODVkNDYyNDllNmQwYzMiLCJlbWFpbCI6InN0ZXBoYW5pZWd1YW5AZmIuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImlhdCI6MTY1NzEzOTUxNzI1NX0.e35FL4w2rS4r_MSKdhV6jj1PUwO1hjjGnB1wsJLDvgY'
queue_id = '8d7f8be8-017c-4b0a-9b0c-e78d9148d378'

apiResultArray = []

def apiResults(path):
    file_location = join(dirname(__file__), path)
    response = Client(api_key).extract_document(file_location, queue_id).form_fields

    # receipt parameters
    merchant = response[0].value
    date = response[3].value
    total = response[8].value

    apiResultArray.clear()
    apiResultArray.append(merchant)
    apiResultArray.append(date)
    apiResultArray.append(total)
    print(response)

def getMerchant():
    return apiResultArray[0]

def getDate():
    return apiResultArray[1]

def getTotal():
    return apiResultArray[2]

def getConfidence():
    if (getMerchant() == "" and getDate() == "" and getTotal() == ""):
        return "low"
    elif (getMerchant() == "" or getDate() == "" or getTotal() == ""):
        return "medium"
    return "high"
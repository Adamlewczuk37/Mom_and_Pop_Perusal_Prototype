from pymongo import MongoClient
import pandas as pd

new = MongoClient('localhost', 27017) #Make a connection to the MongoDB Compass appilcation
db = new.Project1
collection = db.Mom_and_Pop #Initialize a collection to read in collection of entries from MongoDB

cursor = collection.find() #Create cursor to scan through entire dataset
index = 0

with open('output.txt', 'a') as filewriter: #Write outputs to 'output.txt' which will eventuall be renamed manually
    filewriter.write('[') #Write JSON format into file
    for doc in cursor:
        if index != 0:
            filewriter.write(',') #Seperate entries by commas to match JSON array
        doc["_id"] = str(doc["_id"])
        doc_id=doc["_id"]
        doc.pop('attributes', None) #Pop all attributes since category is difficult to convert with GSON and is unused in prototype

        series = pd.Series(doc, name = doc_id) #Convert all information of a document to JSON
        json_entry = series.to_json()
        filewriter.write(json_entry) #Write JSON entry in file
        index += 1
    filewriter.write(']') #End file with a closing bracket

cursor.close()
new.close()

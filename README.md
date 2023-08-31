# Mom_and_Pop_Perusal_Prototype
This respository contains a personal project where I learned how to work with and filter databases.
In this project, I first downloaded the Yelp-Academic dataset which contains fragments of their actual database.
After learning how to set up MongoDB, I integrated this into a collection on MongoDB Compass.
Because the point of my program was to only work with and include small businesses, I used MongoDB queries to filter out all 
non-restaruants and chain restaurants which I defined to be at least 5 of the same restaurant.
I also used MongoDB queries to re-format each document to make it easier to work with.

I then used a Python script to re-convert this dataset back into a workable JSON format.
By creating a Java project with Maven, I imported GSON to eventually convert this to a list of custom data structures to hold the 
data.

The end result is a program that can filter restaurants by state, city, and multiple selected cuisine options which only includes
small businesses. It has to opportunity to be integrated with a front-end UI and can be integrated with Yelp's main dataset in
the future.

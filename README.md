# Persistant-BTree

## Synopsis and Motivation
* The propose of this project is to allow users to input a source and destination Wikipedia link and find the quickest way to get from source to the destination.

## Description

A web crawler that takes in a single wikipedia article and grabs all links on the page and uses a recursive algorithm to grab all the links off the pages from the links off the first page. Then these links are stored a text file. All of these links are then stored in a persistant graph. Dijkstra's best path algorithm is implimented to determine the fastest way to get from link to another. Also the words from the article and their frequencies on the page are stored in a persistant B-Tree. The frequencies are then compared using Cosine Similarity and can be used to determine articles that are most similar to each other based on the frequency of their words.

### Languages
* Java

### Development Tools
* Eclipse/Netbeans

# How to run the project
Download the zip file off the repository. Run the main.java file to prompt the GUI. For the first input link choose off a link off of linksToPickFrom.txt and paste it in as the source link. Then choose different link off linksToPickFrom.txt and paste it in as the destination link. Then you will be prompted the best path between these two links.


# Contributors:
* **Dominic Dewhurst** - *ddewhurs@oswego.edu* (Project Lead Developer)**



## Licenses
* MIT License

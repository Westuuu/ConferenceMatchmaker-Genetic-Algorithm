# Conference Matchmaker

## Overview
Conference Matchmaker is a Java application that uses genetic algorithms to optimize participant matching at conferences. It helps attendees find the most relevant people to meet based on their interests and attributes.

## Features
- **Genetic Algorithm Implementation**: Optimizes recommendations through selection, crossover, and mutation over multiple generations
- **Customizable Parameters**: Configurable evolution parameters (generations, population size, mutation rate)
- **Satisfaction Metrics**: Quantifies how well the algorithm meets participants' preferences
- **Configurable I/O**: Flexible input/output options for participant data and results

## Running the Application
```angular2html
java -jar ConferenceMatchmaker.jar [options]
```

### Options
- `--generations <int>`: Number of generations (default: 500)
- `--populationSize <int>`: Size of each generation (default: 40)
- `--mutationRate <double>`: Probability of mutations (default: 0.02)
- `--dataFilePath <string>`: Path to participant data file (default: data1.txt)
- `--resultsFilePath <string>`: Path for results output (default: results.txt)

## Input Format
Participant data should be formatted like this:
```angular2html
1	DEVELOPER	INVESTOR,DEVELOPER
2	INVESTOR	SALES,MARKETING
```

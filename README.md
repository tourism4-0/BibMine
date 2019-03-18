# BibMine - Bibliographic analysis pipeline
[![Binder](https://mybinder.org/badge_logo.svg)](https://mybinder.org/v2/gh/ajdapretnar/BibMine/master?filepath=main.ipynb)

## API Keys

### Elsevier and Dropbox

Elsevier and Dropbox provide APIs to their services. Please follow the instructions
in [api_keys](api_keys.ipynb) notebook to obtain and register yours API keys.
Without proper keys, these notebooks will not run.


## Data retrieval

Data can be retrieved with scripts in the _Data retrieval_ folder. You can retrieve full articles from ScienceDirect using the _Elsevier-data-retrieval.ipynb_ script and abstracts from Scopus with the _Scopus-retrieval.ipynb_ script. Scripts are designed so that they save intermediate data to the corresponding folder.


## Preprocess data

Run _Preprocessing.ipynb_ to create tokens and explore the data set.


## Topic modeling

Run LDA analysis.

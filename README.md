# BibMine - Bibliographic analysis pipeline
[![Binder](https://mybinder.org/badge_logo.svg)](https://mybinder.org/v2/gh/ajdapretnar/BibMine/master?filepath=main.ipynb)

## API Keys

### Elsevier

Elsevier API service requires users to get their personal API key and to be
connected to a network that has access to Elsevier publications (usually,
universities enable it).

Go to [Elsevier developer portal](https://dev.elsevier.com/apikey/manage) and
click Create API Key. Follow the instructions.
Once the request is sent, a new API key will appear in the Registered API Keys
section. Copy the key (the alpha-numeric combination on the right, under API
Key) and paste it into `ELSEVIER_APIKey` field in the [main script](main.ipynb).
Make sure your key is enveloped into quotes, so it has to look something like
this:

    ELSEVIER_APIKey = "my1939239key"

### Dropbox
We use dropbox to permanently store hits and thus avoid repeating queries for same publications.

Follow the
[instructions](https://github.com/dropbox/dropbox-sdk-python#creating-an-application)
to create a *Dropbox Application* and to *Obtain an Access Token*. Store it
into `DROPBOX_APIkey` field in the [main script](main.ipynb).

    DROPBOX_APIkey = "my911232key"


## Data retrieval

Data can be retrieved with scripts in the _Data retrieval_ folder. You can retrieve full articles from ScienceDirect using the _Elsevier-data-retrieval.ipynb_ script and abstracts from Scopus with the _Scopus-retrieval.ipynb_ script. Scripts are designed so that they save intermediate data to the corresponding folder.


## Preprocess data

Run _Preprocessing.ipynb_ to create tokens and explore the data set.


## Topic modeling

Run LDA analysis.

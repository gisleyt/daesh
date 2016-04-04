import os
from glob import glob

import re

import nltk
from nltk import sent_tokenize, word_tokenize

ALPHA_RE = re.compile('^[A-Za-z-\']+$')
STOPWORDS = nltk.corpus.stopwords.words('english')

def data_fns(data_path):
    return glob(os.path.join(data_path, 'txt', '*.txt'))


def process(f):
    line_count = 0

    try:
        for line in f:
            line_count += 1

            line = line.strip()

            if line == '':
                continue

            for sent in sent_tokenize(line):
                yield word_tokenize(sent)
    except UnicodeDecodeError as e:
        print "Unicode error. Previous line # was %d ..." % line_count

        raise e


def filter_word(word):
    return word.lower() in STOPWORDS


def is_word(word):
    return ALPHA_RE.match(word)

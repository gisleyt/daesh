from collections import Counter

from daesh_analysis.data import process, filter_word, is_word


def word_counts(fns):
    c = Counter()

    for fn in fns:
        with open(fn) as f:
            for sent in process(f):
                c.update([token.lower() for token in sent if is_word(token) and not filter_word(token)])

    return c

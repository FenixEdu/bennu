/*
 * Search.java
 *
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 *
 * This file is part of bennu-search.
 *
 * bennu-search is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * bennu-search is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with bennu-search.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.WildcardQuery;
import org.fenixedu.bennu.search.IndexDocument.DefaultIndexFields;
import org.fenixedu.bennu.search.domain.DomainIndexSystem;

public class Search {
    private BooleanQuery query = new BooleanQuery();

    private List<SortField> sort = new ArrayList<>();

    public Search() {
    }

    public Search tokens(IndexableField field, String text, Occur occur) {
        List<Term> terms = makeTerm(field, text);
        for (Term term : terms) {
            query.add(new TermQuery(term), occur);
        }
        return this;
    }

    public Search tokens(String text, Occur occur) {
        return tokens(DefaultIndexFields.DEFAULT_FIELD, text, occur);
    }

    public Search phrase(IndexableField field, String phrase, Occur occur) {
        List<Term> terms = makeTerm(field, phrase);
        if (terms.size() > 1) {
            PhraseQuery phraseQuery = new PhraseQuery();
            for (Term term : terms) {
                phraseQuery.add(term);
            }
            query.add(phraseQuery, occur);
        } else {
            query.add(new TermQuery(terms.iterator().next()), occur);
        }
        return this;
    }

    public Search phrase(String phrase, Occur occur) {
        return phrase(DefaultIndexFields.DEFAULT_FIELD, phrase, occur);
    }

    public Search wildcard(IndexableField field, String pattern, Occur occur) {
        List<Term> terms = makeTerm(field, pattern);
        for (Term term : terms) {
            query.add(new WildcardQuery(term), occur);
        }
        return this;
    }

    public Search wildcard(String pattern, Occur occur) {
        return wildcard(DefaultIndexFields.DEFAULT_FIELD, pattern, occur);
    }

    public Search fuzzy(IndexableField field, String pattern, Occur occur) {
        List<Term> terms = makeTerm(field, pattern);
        for (Term term : terms) {
            query.add(new FuzzyQuery(term), occur);
        }
        return this;
    }

    public Search fuzzy(String pattern, Occur occur) {
        return fuzzy(DefaultIndexFields.DEFAULT_FIELD, pattern, occur);
    }

    public Search range(IndexableField field, String lower, String upper, boolean includeLower, boolean includeUpper, Occur occur) {
        TermRangeQuery range = TermRangeQuery.newStringRange(field.getFieldName(), lower, upper, includeLower, includeUpper);
        query.add(range, occur);
        return this;
    }

    public Search range(String lower, String upper, boolean includeLower, boolean includeUpper, Occur occur) {
        return range(DefaultIndexFields.DEFAULT_FIELD, lower, upper, includeLower, includeUpper, occur);
    }

    public Search subquery(Search search, Occur occur) {
        query.add(search.query, occur);
        return this;
    }

    private List<Term> makeTerm(IndexableField field, String text) {
        List<Term> terms = new ArrayList<>();
        List<String> tokens = DomainIndexSystem.tokenizeString(field, text);
        for (String token : tokens) {
            terms.add(new Term(field.getFieldName(), token));
        }
        return terms;
    }

    public Search sort(IndexableField field, boolean reverse) {
        sort.add(new SortField(field.getFieldName(), SortField.Type.STRING, reverse));
        return this;
    }

    public <T extends Indexable> List<T> search(Class<T> type, int maxHits) {
        if (sort.isEmpty()) {
            return DomainIndexSystem.search(type, query, maxHits);
        }
        return DomainIndexSystem.search(type, query, maxHits, new Sort(sort.toArray(new SortField[0])));
    }

    public <T extends Indexable> List<T> search(Class<T> type) {
        return search(type, Integer.MAX_VALUE);
    }
}

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
package pt.ist.bennu.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import pt.ist.bennu.search.DomainIndexer.DefaultIndexFields;

import com.google.common.base.Joiner;

public class Search {
    List<String> terms = new ArrayList<>();

    List<SortField> sort = new ArrayList<>();

    public Search() {
    }

    public Search must(String... values) {
        return must(DefaultIndexFields.DEFAULT_FIELD, values);
    }

    public Search must(IndexableField field, String... values) {
        for (String value : values) {
            terms.add("+" + field.getFieldName() + ":" + DomainIndexer.normalize(QueryParser.escape(value)));
        }
        return this;
    }

    public Search range(IndexableField field, String start, String end, boolean inclusive) {
        if (inclusive) {
            terms.add(field.getFieldName() + ":[" + start + " TO " + end + "]");
        } else {
            terms.add(field.getFieldName() + ":{" + start + " TO " + end + "}");
        }
        return this;
    }

    public Search sort(IndexableField field, boolean reverse) {
        sort.add(new SortField(field.getFieldName(), SortField.Type.STRING, reverse));
        return this;
    }

    public <T extends Indexable> List<T> search(Class<T> type, int maxHits) {
        if (sort.isEmpty()) {
            return DomainIndexer.getInstance().search(type, Joiner.on(' ').join(terms), maxHits);
        }
        return DomainIndexer.getInstance().search(type, Joiner.on(' ').join(terms), maxHits,
                new Sort(sort.toArray(new SortField[0])));
    }

    public <T extends Indexable> List<T> search(Class<T> type) {
        return search(type, Integer.MAX_VALUE);
    }
}

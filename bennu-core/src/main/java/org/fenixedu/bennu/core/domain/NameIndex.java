package org.fenixedu.bennu.core.domain;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.fenixedu.commons.StringNormalizer;

import pt.ist.fenixframework.FenixFramework;

import com.google.common.collect.Sets;

class NameIndex extends NameIndex_Base {
    private static final Map<String, NameIndex> map = new ConcurrentHashMap<>();

    protected NameIndex() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected NameIndex(String keyword) {
        this();
        setKeyword(keyword);
    }

    static Stream<UserProfile> search(String query, int maxHits) {
        String[] queryParts = StringNormalizer.normalizeAndRemoveAccents(query.toLowerCase().trim()).split("\\s+");
        return Stream.of(queryParts).map(NameIndex::find).reduce(Sets::intersection).orElse(Collections.emptySet()).stream()
                .limit(maxHits);
    }

    static void updateNameIndex(UserProfile profile) {
        String normalized = StringNormalizer.normalizeAndRemoveAccents(profile.getFullName().toLowerCase().trim());
        profile.getNameIndexSet().clear();
        for (String term : normalized.split("\\s+")) {
            profile.getNameIndexSet().add(create(term));
        }
    }

    private static NameIndex create(String keyword) {
        NameIndex match = map.computeIfAbsent(keyword, query -> manualFind(keyword).orElseGet(() -> new NameIndex(keyword)));
        // FIXME: the second condition is there because of bug #197 in the fenix-framework
        if (!FenixFramework.isDomainObjectValid(match) || !match.getKeyword().equals(keyword)) {
            map.remove(keyword, match);
            return create(keyword);
        }
        return match;
    }

    private static Set<UserProfile> find(String keyword) {
        NameIndex match = map.computeIfAbsent(keyword, query -> manualFind(keyword).orElse(null));
        if (match == null) {
            return Collections.emptySet();
        }
        // FIXME: the second condition is there because of bug #197 in the fenix-framework
        if (!FenixFramework.isDomainObjectValid(match) || !match.getKeyword().equals(keyword)) {
            map.remove(keyword, match);
            return find(keyword);
        }
        return match.getProfileSet();
    }

    private static Optional<NameIndex> manualFind(String keyword) {
        return Bennu.getInstance().getNameIndexSet().stream().filter(name -> name.getKeyword().equals(keyword)).findAny();
    }

    static void cleanupIndex() {
        map.clear(); // cache must be invalidated because we are deleting indexes and cache assumes otherwise
        for (NameIndex index : Bennu.getInstance().getNameIndexSet()) {
            index.delete();
        }
        for (UserProfile profile : Bennu.getInstance().getProfileSet()) {
            updateNameIndex(profile);
        }
    }

    private void delete() {
        setBennu(null);
        getProfileSet().clear();
        deleteDomainObject();
    }

    static void heatupCache() {
        Bennu.getInstance().getNameIndexSet().stream().forEach(index -> map.put(index.getKeyword(), index));
    }
}

package com.davidsoft.compact;

import java.util.BitSet;
import java.util.Objects;

public class ArrayList {

    public static <E> boolean removeIf(java.util.ArrayList<E> list, Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        int removeCount = 0;
        final BitSet removeSet = new BitSet(list.size());
        final int size = list.size();
        for (int i=0; i < size; i++) {
            final E element = (E) list.get(i);
            if (filter.test(element)) {
                removeSet.set(i);
                removeCount++;
            }
        }

        final boolean anyToRemove = removeCount > 0;
        if (anyToRemove) {
            final int newSize = size - removeCount;
            for (int i=0, j=0; (i < size) && (j < newSize); i++, j++) {
                i = removeSet.nextClearBit(i);
                list.set(j, list.get(i));
            }
            for (int k = size - 1; k >= newSize; k--) {
                list.remove(k);
            }
        }

        return anyToRemove;
    }
}

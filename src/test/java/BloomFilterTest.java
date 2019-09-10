import static org.junit.jupiter.api.Assertions.*;


import com.sangupta.murmur.Murmur2;
import com.sangupta.murmur.Murmur3;
import net.jqwik.api.*;
import net.jqwik.api.arbitraries.IntegerArbitrary;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.NotEmpty;
import net.jqwik.api.constraints.Size;
import net.jqwik.api.constraints.Unique;
import org.assertj.core.api.*;
import org.assertj.core.data.*;

import java.util.List;
import java.util.Set;

class BloomFilterTest {

    @Property
    void hashesEqual(@ForAll @NotEmpty byte[] elements) {
        assertEquals(Murmur2.hash64(elements, elements.length, 0), Murmur2.hash64(elements, elements.length, 0));
    }

    @Property
    boolean filterContainsAllAdded(@ForAll("bloomfilters") BloomFilter filter, @ForAll List< @NotEmpty byte[]> elements) {
        for (var e : elements) {
            filter.put(e);
        }
        for (var e : elements) {
            if (!filter.mightContain(e)) {
                return false;
            }
        }
        return true;
    }

    @Property
    boolean filterContainsAllAdded2(@ForAll("bloomfilters") BloomFilter filter, @ForAll List< @NotEmpty byte[]> elements) {
        for (var e : elements) {
            filter.put(e);
            if (!filter.mightContain(e)) {
                return false;
            }
        }
        return true;
    }

    @Property
    boolean unionModelsSet(@ForAll("bloomfilters") BloomFilter filter1, @ForAll Set< @NotEmpty byte[]> elements1, @ForAll Set< @NotEmpty byte[]> elements2) {
        var filter2 = new BloomFilter(filter1.getnBytes());

        elements1.forEach(filter1::put);
        elements2.forEach(filter2::put);

        elements1.addAll(elements2);
        var allElements = elements1;

        var filterUnion = filter1.union(filter2);

        for (var e : allElements) {
            if (!filterUnion.mightContain(e)) {
                return false;
            }
        }
        return true;

    }

    @Property
    void falsePositiveCounts(@ForAll("bloomfilters") BloomFilter filter, @ForAll @Unique List<byte[]> elements ) {
        for (var e : elements) {
            Statistics.collect(filter.mightContain(e) ? "false positives" : null);
            filter.put(e);
        }
    }
    @Property
    void falsePositiveCounts1(@ForAll @Size(min=499, max=500) @Unique List<byte[]> elements ) {
        var filter = new BloomFilter(500, 1);
        for (var e : elements) {
            Statistics.collect(filter.mightContain(e) ? "false positives" : null);
            filter.put(e);
        }
    }
    @Property
    void falsePositiveCounts2(@ForAll @Size(min=499, max=500) @Unique List<byte[]> elements ) {
        var filter = new BloomFilter(500, 2);
        for (var e : elements) {
            Statistics.collect(filter.mightContain(e) ? "false positives" : null);
            filter.put(e);
        }
    }
    @Property
    void falsePositiveCounts4(@ForAll @Size(min=499, max=500) @Unique List<byte[]> elements ) {
        var filter = new BloomFilter(500, 4);
        for (var e : elements) {
            Statistics.collect(filter.mightContain(e) ? "false positives" : null);
            filter.put(e);
        }
    }
    @Property
    void falsePositiveCounts8(@ForAll @Size(min=499, max=500) @Unique List<byte[]> elements ) {
        var filter = new BloomFilter(500, 8);
        for (var e : elements) {
            Statistics.collect(filter.mightContain(e) ? "false positives" : null);
            filter.put(e);
        }
    }
    @Property
    void falsePositiveCounts12(@ForAll @Size(min=499, max=500) @Unique List<byte[]> elements ) {
        var filter = new BloomFilter(500, 12);
        for (var e : elements) {
            Statistics.collect(filter.mightContain(e) ? "false positives" : null);
            filter.put(e);
        }
    }

    @Property
    void falsePositiveCounts16(@ForAll @Size(min=499, max=500) @Unique List<byte[]> elements ) {
        var filter = new BloomFilter(500, 16);
        for (var e : elements) {
            Statistics.collect(filter.mightContain(e) ? "false positives" : null);
            filter.put(e);
        }
    }

    @Property
    void falsePositiveCounts32(@ForAll @Size(min=499, max=500) @Unique List<byte[]> elements ) {
        var filter = new BloomFilter(500, 32);
        for (var e : elements) {
            Statistics.collect(filter.mightContain(e) ? "false positives" : null);
            filter.put(e);
        }
    }

    @Property
    void statsFalsePositiveStrings(@ForAll @Size(min=499, max=500) List< @Unique @AlphaChars String> elements) {
        var filter = new BloomFilter(500);
        for (var e : elements) {
            var bytes = e.getBytes();
            Statistics.collect(filter.mightContain(bytes) ? "false positives" : null);
            filter.put(bytes);
        }
    }


    @Provide
    Arbitrary<BloomFilter> bloomfilters() {
        Arbitrary<Integer> sizes = Arbitraries.integers().between(1, Integer.MAX_VALUE / 8);
        return sizes.map(BloomFilter::new);
    }

}
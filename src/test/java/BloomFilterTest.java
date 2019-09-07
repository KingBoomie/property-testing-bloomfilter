import static org.junit.jupiter.api.Assertions.*;


import com.sangupta.murmur.Murmur2;
import com.sangupta.murmur.Murmur3;
import net.jqwik.api.*;
import net.jqwik.api.arbitraries.IntegerArbitrary;
import net.jqwik.api.constraints.NotEmpty;
import net.jqwik.api.constraints.Unique;
import org.assertj.core.api.*;
import org.assertj.core.data.*;

import java.util.List;

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
    void falsePositiveCounts(@ForAll("bloomfilters") BloomFilter filter, @ForAll @Unique List< @NotEmpty byte[]> elements ) {
        for (var e : elements) {
            Statistics.collect(filter.mightContain(e) ? "false positives" : null);
            filter.put(e);
        }
    }


    /*@Provide
    Arbitrary<BloomFilter> bloomfilters() {
        Arbitrary<Integer> sizes = Arbitraries.integers().between(1,100000);
        return sizes.flatMap(size -> {
            var hashNums = Arbitraries.integers().between(1,size);
            return hashNums.map(hashNum -> new BloomFilter(size, hashNum));
        });
    }*/
    @Provide
    Arbitrary<BloomFilter> bloomfilters() {
        Arbitrary<Integer> sizes = Arbitraries.integers().between(1, Integer.MAX_VALUE / 8);
        return sizes.map(BloomFilter::new);
    }

}
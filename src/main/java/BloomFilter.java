import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.Iterator;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class BloomFilter {

    private int nBytes;
    private int numHashes;

    private BitSet data;
    private MessageDigest hasher;

    public BloomFilter(int nBytes) {
        this.nBytes = nBytes;
        this.numHashes = 4;

        this.data = new BitSet(nBytes *8);

        try { this.hasher = MessageDigest.getInstance("SHA-256"); } catch (NoSuchAlgorithmException ignored) {}

    }

    private BloomFilter(int nBytes, BitSet data) {
        this.nBytes = nBytes;
        this.numHashes = 4;

        this.data = data;
        try { this.hasher = MessageDigest.getInstance("SHA-256"); } catch (NoSuchAlgorithmException ignored) {}
    }

    public void put(byte[] element) {
        for (int index : iterateHashedIndexes(element)) {
            data.set(index);
        }
    }
    public boolean mightContain(byte[] element) {

        for (int index : iterateHashedIndexes(element)) {
            if (!data.get(index)) {
                return false;
            }
        }
        return true;
    }

    public BloomFilter union(BloomFilter filter) {
        // fail if trying to union two bitsets that were created with different parameters
        assert filter.numHashes == this.numHashes;
        assert filter.nBytes == this.nBytes;

        var newData = ((BitSet) filter.data.clone()); // need to clone data because .or modifies the bitset in place for some reason
        newData.or(this.data);
        return new BloomFilter(this.nBytes, newData);
    }

    public byte[] getData() {
        return data.toByteArray();
    }

    public int getnBytes() {
        return nBytes;
    }

    private Iterable<Integer> iterateHashedIndexes(byte[] element) {
        return () -> new Iterator<Integer>() {

            ByteBuffer hashResult = ByteBuffer.wrap(hasher.digest(element));

            final int hash1 = hashResult.getInt();
            final int hash2 = hashResult.getInt();

            int combinedHash = abs(hash1) % data.size();
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < numHashes;
            }

            @Override
            public Integer next() {
                combinedHash = abs(hash1 + i*hash2) % data.size();
                i += 1;
                return combinedHash;
            }
        };

    }

    @Override
    public String toString() {
        return "BloomFilter{" +
                "nBytes=" + nBytes +
                ", numHashes=" + numHashes +
                '}';
    }
}

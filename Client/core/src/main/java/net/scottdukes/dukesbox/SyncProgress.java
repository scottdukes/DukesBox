package net.scottdukes.dukesbox;

import java.util.concurrent.atomic.AtomicInteger;

public class SyncProgress {
    private final String mTag;
    private final int mTotal;
    private AtomicInteger mCount = new AtomicInteger(0);

    public SyncProgress(String tag, int totalFiles) {
        mTag = tag;
        mTotal = totalFiles;
    }

    public void increment() {
        mCount.incrementAndGet();
    }

    public String getTag() {
        return mTag;
    }

    public int count() {
        return mCount.get();
    }

    public int total() {
        return mTotal;
    }

    public boolean isComplete() {
        return (mCount.get() == mTotal);
    }

    @Override
    public String toString() {
        return String.format("%s:%d/%d", mTag, mCount.get(), mTotal);
    }
}

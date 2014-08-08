package net.scottdukes.dukesbox.vfs;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSink;
import com.google.common.io.Files;

import net.scottdukes.dukesbox.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

public class ExternalVFile implements VFile {
    private final Logger mLogger = LoggerFactory.getLogger(ExternalVFile.class);
    private final File mFile;
    private final String mPath;
    private HashCode mHash;

    public ExternalVFile(@Nonnull File baseDir, @Nonnull File file) {
        mFile = file;
        mPath = normalizePath(getRelativePath(baseDir, file));
    }

    private static String normalizePath(@Nonnull String path) {
        return path.replace(File.separatorChar, '/');
    }

    private static String getRelativePath(@Nonnull File baseDir, @Nonnull File file) {
        return StringUtils.after(file.getAbsolutePath(), baseDir.getAbsolutePath() + File.separatorChar);
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public String getHash() {
        if (mHash == null) {
            calculateHash();
        }

        return (mHash != null) ? mHash.toString() : "";
    }

    void setHash(@Nonnull String value) {
        mHash = HashCode.fromString(value);
    }

    synchronized void calculateHash() {
        try {
            mHash = Files.hash(mFile, Hashing.sha1());
        } catch (IOException e) {
            String msg = String.format("Error calculating hash for %s", mFile);
            mLogger.error(msg, e);
            mHash = null;
        }
    }

    @Override
    public long getSize() {
        return mFile.length();
    }

    public boolean exists() {
        return mFile.exists();
    }

    public ByteSink asByteSink() {
        return Files.asByteSink(mFile);
    }

    public File getFile() {
        return mFile;
    }

    @Override
    public String toString() {
        return mFile.getAbsolutePath();
    }
}

package net.scottdukes.dukesbox.vfs;

import com.google.common.io.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

public class ExternalFileProvider {
    private final Logger mLogger = LoggerFactory.getLogger(ExternalFileProvider.class);
    private final ConcurrentHashMap<String, VFile> mFiles = new ConcurrentHashMap<String, VFile>();
    private final File mBaseDir;

    public ExternalFileProvider(File baseDir) {
        mBaseDir = baseDir;
    }

    public ExternalFileProvider(File root, String path) {
        this(new File(root, path));
    }

    public void buildManifest() {
        if (!mBaseDir.exists()) {
            mLogger.info("Creating {}", mBaseDir);
            mBaseDir.mkdirs();
        }

        mLogger.debug("Building manifest for " + mBaseDir);
        mFiles.clear();
        for (File file : Files.fileTreeTraverser().breadthFirstTraversal(mBaseDir)) {
            if (file.isDirectory()) {
                continue;
            }

            ExternalVFile vFile = new ExternalVFile(mBaseDir, file);
            vFile.calculateHash();
            addFile(vFile);

            mLogger.debug(String.format("Found %s with hash %s", vFile.getPath(), vFile.getHash()));
        }
    }

    public VFile getFile(@Nonnull String path, @Nonnull String expectedHash) {
        if (exists(path, expectedHash)) {
            return mFiles.get(path);
        }

        return new MissingVFile(path);
    }

    public boolean exists(@Nonnull String path, @Nonnull String expectedHash) {
        // is file in in-memory cache
        VFile file = mFiles.get(path);
        if (file != null) {
            if (expectedHash.equalsIgnoreCase(file.getHash())) {
                return true;
            }
        }

        ExternalVFile possibleFile = createFile(path);

        // does file exist on disk
        if (!possibleFile.exists()) {
            return false;
        }

        String actualHash = possibleFile.getHash();

        // cache for quick in memory lookup
        addFile(possibleFile);

        return actualHash.equalsIgnoreCase(expectedHash);

    }

    private VFile addFile(@Nonnull ExternalVFile file) {
        return mFiles.put(file.getPath(), file);
    }

    private boolean checkHashFileExists(@Nonnull ExternalVFile file, @Nonnull String hash) {
        File hashFile = getHashFile(file, hash);

        if (hashFile.exists()) {
            if (hashFile.lastModified() > file.getFile().lastModified()) {
                return true;
            }

            // delete the hash file since it doesn't represent the actual file
            if (hashFile.delete()) {
                mLogger.warn(String.format("Deleted invalid hash file. %s", hashFile));
            }
        }

        return false;
    }

    private File getHashFile(@Nonnull ExternalVFile file, @Nonnull String hash) {
        String expectHashFilePath = String.format("%s.%s.sha1", file.getFile().getAbsolutePath(), hash);
        return new File(expectHashFilePath);
    }

    public Collection<VFile> getFiles() {
        return mFiles.values();
    }

    public ExternalVFile createFile(@Nonnull String path) {
        File file = new File(mBaseDir, path);
        return new ExternalVFile(mBaseDir, file);
    }

    public void clearCache() {
        mFiles.clear();
    }
}

package net.scottdukes.dukesbox.vfs;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class ExternalFileProviderTests {

    private ExternalFileProvider mSUT;

    @Before
    public void setUp() throws Exception {
        mSUT = new ExternalFileProvider(new File("C:\\deviceteam\\devicelive\\theme"));
    }

    @Test
    public void testBuildManifest() throws Exception {
        mSUT.buildManifest();
        VFile result = mSUT.getFile("sundaehtml/global/branding/branding.swf", "25F753AAF72595735F9E31BCFA02109471DD3FD9");
        assertThat(result).isInstanceOf(MissingVFile.class);
    }
}
